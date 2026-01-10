import { useQuery } from '@tanstack/react-query'
import { Post, Author, Tag } from '@/types'
import { apiClient } from './client'

export interface SearchResults {
  posts: Post[]
  authors: Author[]
  tags: Tag[]
  total: number
}

export const searchApi = {
  search: async (query: string, params?: { page?: number; limit?: number; type?: 'all' | 'posts' | 'authors' | 'tags' }) => {
    const response = await apiClient.get('/search', {
      params: {
        query,
        type: params?.type || 'all',
        limit: params?.limit || 10,
      },
    })
    return response.data
  },
}

export const useSearch = (query: string, params?: { page?: number; limit?: number; type?: 'all' | 'posts' | 'authors' | 'tags' }) => {
  return useQuery({
    queryKey: ['search', query, params],
    queryFn: () => searchApi.search(query, params),
    enabled: !!query && query.length > 0,
  })
}

