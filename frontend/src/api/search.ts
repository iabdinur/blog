import { useQuery } from '@tanstack/react-query'
import Fuse from 'fuse.js'
import { Post, Author, Tag } from '@/types'
import { fetchData } from './static'

export interface SearchResults {
  posts: Post[]
  authors: Author[]
  tags: Tag[]
  total: number
}

interface SearchIndex {
  posts: Post[]
  authors: Author[]
  tags: Tag[]
}

let indexCache: Promise<SearchIndex> | null = null
const loadIndex = () => {
  if (!indexCache) indexCache = fetchData<SearchIndex>('search-index.json')
  return indexCache
}

export const searchApi = {
  search: async (query: string, params?: { limit?: number }): Promise<SearchResults> => {
    const q = query.trim()
    if (!q) return { posts: [], authors: [], tags: [], total: 0 }
    const limit = params?.limit ?? 10
    const index = await loadIndex()

    const postFuse = new Fuse(index.posts, {
      keys: ['title', 'excerpt', 'tags.name'],
      threshold: 0.4,
    })
    const tagFuse = new Fuse(index.tags, {
      keys: ['name', 'description'],
      threshold: 0.4,
    })
    const authorFuse = new Fuse(index.authors, {
      keys: ['name', 'bio'],
      threshold: 0.4,
    })

    const posts = postFuse.search(q).slice(0, limit).map((r) => r.item)
    const tags = tagFuse.search(q).map((r) => r.item)
    const authors = authorFuse.search(q).map((r) => r.item)

    return { posts, authors, tags, total: posts.length + authors.length + tags.length }
  },
}

export const useSearch = (query: string, params?: { limit?: number }) => {
  return useQuery({
    queryKey: ['search', query, params],
    queryFn: () => searchApi.search(query, params),
    enabled: !!query && query.length > 0,
  })
}
