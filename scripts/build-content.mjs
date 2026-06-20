#!/usr/bin/env node
import { readFileSync, readdirSync, writeFileSync, mkdirSync, rmSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import matter from 'gray-matter'

const __dirname = dirname(fileURLToPath(import.meta.url))
const ROOT = join(__dirname, '..')
const CONTENT = join(ROOT, 'content')
const POSTS_DIR = join(CONTENT, 'posts')
const AUTHORS_DIR = join(CONTENT, 'authors')
const SERIES_FILE = join(CONTENT, 'series.json')
const OUT = join(ROOT, 'frontend', 'public', 'data')

// Curated series (a.k.a. tags) that always appear in navigation, even with 0 posts.
const loadSeries = () => {
  if (!existsSync(SERIES_FILE)) return []
  return JSON.parse(readFileSync(SERIES_FILE, 'utf8'))
}

const readingTime = (text) => Math.max(1, Math.round(text.trim().split(/\s+/).length / 200))

const writeJson = (path, data) => {
  mkdirSync(dirname(path), { recursive: true })
  writeFileSync(path, JSON.stringify(data, null, 2))
}

const loadAuthors = () => {
  const authors = {}
  if (!existsSync(AUTHORS_DIR)) return authors
  for (const file of readdirSync(AUTHORS_DIR).filter((f) => f.endsWith('.md'))) {
    const { data, content } = matter(readFileSync(join(AUTHORS_DIR, file), 'utf8'))
    const username = data.username || file.replace(/\.md$/, '')
    authors[username] = {
      id: String(data.id ?? username),
      name: data.name ?? username,
      username,
      bio: data.bio ?? content.trim(),
      avatar: data.avatar ?? '',
      coverImage: data.coverImage ?? '',
      location: data.location ?? '',
      website: data.website ?? '',
      socialLinks: data.socialLinks ?? {},
      followersCount: 0,
      postsCount: 0,
      joinedAt: data.joinedAt ?? new Date().toISOString(),
    }
  }
  return authors
}

const loadPosts = (authors) => {
  const posts = []
  if (!existsSync(POSTS_DIR)) return posts
  for (const file of readdirSync(POSTS_DIR).filter((f) => f.endsWith('.md'))) {
    const { data, content } = matter(readFileSync(join(POSTS_DIR, file), 'utf8'))
    if (data.draft === true) continue
    const author = authors[data.author]
    if (!author) throw new Error(`Post "${file}" references unknown author "${data.author}"`)
    const tagObjects = (data.tags ?? []).map((name) => {
      const slug = String(name).toLowerCase().trim().replace(/\s+/g, '-')
      return { id: slug, name: String(name), slug, postsCount: 0 }
    })
    posts.push({
      id: String(data.id ?? data.slug),
      title: data.title ?? '',
      slug: data.slug,
      content: content.trim(),
      excerpt: data.excerpt ?? '',
      coverImage: data.coverImage ?? '',
      contentImage: data.contentImage ?? '',
      publishedAt: new Date(data.date).toISOString(),
      updatedAt: data.updated ? new Date(data.updated).toISOString() : undefined,
      author,
      tags: tagObjects,
      readingTime: readingTime(content),
      views: 0,
      likes: 0,
      commentsCount: 0,
      isPublished: true,
      featured: data.featured === true,
    })
  }
  posts.sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
  return posts
}

// Fetch real comment counts from GitHub Discussions (giscus backend).
// Discussions are titled by the giscus term (the post slug, mapping="specific").
// Env-gated: returns {} (so counts default to 0) when token/repo are absent or on error.
const fetchCommentCounts = async () => {
  const token = process.env.GITHUB_TOKEN
  const repo = process.env.GISCUS_REPO || process.env.GITHUB_REPOSITORY
  if (!token || !repo) {
    console.log('Comment counts: skipped (set GITHUB_TOKEN + GISCUS_REPO to enable).')
    return {}
  }
  const [owner, name] = repo.split('/')
  const query = `query($owner:String!,$name:String!,$cursor:String){repository(owner:$owner,name:$name){discussions(first:100,after:$cursor){nodes{title comments{totalCount}} pageInfo{hasNextPage endCursor}}}}`
  const counts = {}
  let cursor = null
  try {
    do {
      const res = await fetch('https://api.github.com/graphql', {
        method: 'POST',
        headers: { Authorization: `bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, variables: { owner, name, cursor } }),
      })
      const json = await res.json()
      const discussions = json?.data?.repository?.discussions
      if (!discussions) {
        console.warn('Comment counts: unexpected response, defaulting to 0.')
        return {}
      }
      for (const node of discussions.nodes) counts[node.title] = node.comments.totalCount
      cursor = discussions.pageInfo.hasNextPage ? discussions.pageInfo.endCursor : null
    } while (cursor)
    console.log(`Comment counts: fetched ${Object.keys(counts).length} discussions.`)
  } catch (err) {
    console.warn(`Comment counts: fetch failed (${err.message}); defaulting to 0.`)
    return {}
  }
  return counts
}

const build = async () => {
  const authors = loadAuthors()
  const posts = loadPosts(authors)

  const commentCounts = await fetchCommentCounts()
  for (const post of posts) post.commentsCount = commentCounts[post.slug] ?? 0

  // Seed curated series, then derive counts from posts.
  const seedSeries = loadSeries()
  const tagMap = {}
  const keyToSlug = {}
  for (const s of seedSeries) {
    tagMap[s.slug] = { id: s.slug, name: s.name, slug: s.slug, description: s.description ?? '', postsCount: 0 }
    keyToSlug[s.slug] = s.slug
    keyToSlug[s.name.toLowerCase()] = s.slug
  }

  for (const post of posts) {
    post.author.postsCount = (post.author.postsCount || 0) + 1
    const resolved = []
    for (const tag of post.tags) {
      const key = tag.name.toLowerCase().trim()
      const slug = keyToSlug[key] ?? key.replace(/\s+/g, '-')
      if (!tagMap[slug]) tagMap[slug] = { id: slug, name: tag.name, slug, description: '', postsCount: 0 }
      tagMap[slug].postsCount += 1
      resolved.push(tagMap[slug])
    }
    post.tags = resolved
  }
  const tags = Object.values(tagMap).sort((a, b) => a.name.localeCompare(b.name))

  if (existsSync(OUT)) rmSync(OUT, { recursive: true, force: true })

  const index = posts.map(({ content, ...rest }) => rest)
  writeJson(join(OUT, 'posts-index.json'), index)
  for (const post of posts) writeJson(join(OUT, 'posts', `${post.slug}.json`), post)
  writeJson(join(OUT, 'tags.json'), tags)
  for (const author of Object.values(authors)) {
    writeJson(join(OUT, 'authors', `${author.username}.json`), author)
  }
  writeJson(join(OUT, 'search-index.json'), {
    posts: index,
    authors: Object.values(authors),
    tags,
  })
  writeJson(join(OUT, 'manifest.json'), {
    generatedAt: new Date().toISOString(),
    counts: { posts: posts.length, tags: tags.length, authors: Object.keys(authors).length },
  })

  console.log(`Built ${posts.length} posts, ${tags.length} tags, ${Object.keys(authors).length} authors → ${OUT}`)
}

build().catch((err) => {
  console.error(err)
  process.exit(1)
})
