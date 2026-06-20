import { useQuery } from '@tanstack/react-query'
import { Post } from '@/types'
import { fetchData } from './static'

export interface PostListResponse {
  posts: Post[]
  total: number
  page: number
  totalPages: number
}

export interface PostsQuery {
  page?: number
  limit?: number
  tag?: string
  author?: string
  sort?: 'latest' | 'top' | 'discussions' | string
  exclude?: string
}

let indexCache: Promise<Post[]> | null = null
const loadIndex = () => {
  if (!indexCache) indexCache = fetchData<Post[]>('posts-index.json')
  return indexCache
}

const sortPosts = (posts: Post[], sort?: string) => {
  const sorted = [...posts]
  switch (sort) {
    case 'top':
      return sorted.sort((a, b) => b.likes - a.likes)
    case 'discussions':
      return sorted.sort((a, b) => b.commentsCount - a.commentsCount)
    default:
      return sorted.sort(
        (a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime()
      )
  }
}

export const postsApi = {
  getAll: async (params: PostsQuery = {}): Promise<PostListResponse> => {
    const { page = 1, limit = 10, tag, author, sort, exclude } = params
    let posts = await loadIndex()
    if (tag) posts = posts.filter((p) => p.tags.some((t) => t.slug === tag))
    if (author) posts = posts.filter((p) => p.author.username === author)
    if (exclude) posts = posts.filter((p) => p.id !== exclude && p.slug !== exclude)
    posts = sortPosts(posts, sort)

    const total = posts.length
    const start = (page - 1) * limit
    return {
      posts: posts.slice(start, start + limit),
      total,
      page,
      totalPages: Math.max(1, Math.ceil(total / limit)),
    }
  },
  getBySlug: async (slug: string): Promise<Post> => fetchData<Post>(`posts/${slug}.json`),
}

export const usePosts = (params?: PostsQuery) => {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: () => postsApi.getAll(params),
  })
}

export const usePost = (slug: string) => {
  return useQuery({
    queryKey: ['post', slug],
    queryFn: () => postsApi.getBySlug(slug),
    enabled: !!slug,
  })
}
