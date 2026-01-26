import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'

export const postsApi = {
  getAll: async (params?: { page?: number; limit?: number; tag?: string; author?: string; sort?: string; exclude?: string }) => {
    const response = await apiClient.get('/posts', {
      params: {
        page: params?.page || 1,
        limit: params?.limit || 10,
        tag: params?.tag,
        author: params?.author,
        sort: params?.sort || 'latest',
        exclude: params?.exclude,
      },
    })
    return response.data
  },
  getBySlug: async (slug: string) => {
    const response = await apiClient.get(`/posts/${slug}`)
    return response.data
  },
  getByAuthor: async (authorId: string, params?: { page?: number; limit?: number }) => {
    const response = await apiClient.get('/posts', {
      params: {
        author: authorId,
        page: params?.page || 1,
        limit: params?.limit || 10,
      },
    })
    return response.data
  },
  getByTag: async (tagSlug: string, params?: { page?: number; limit?: number }) => {
    const response = await apiClient.get('/posts', {
      params: {
        tag: tagSlug,
        page: params?.page || 1,
        limit: params?.limit || 10,
      },
    })
    return response.data
  },
  search: async (query: string, params?: { page?: number; limit?: number }) => {
    const response = await apiClient.get('/search', {
      params: {
        query,
        limit: params?.limit || 10,
      },
    })
    return response.data
  },
  incrementViews: async (slug: string) => {
    await apiClient.post(`/posts/${slug}/views`)
  },
  likePost: async (slug: string) => {
    await apiClient.post(`/posts/${slug}/like`)
  },
  unlikePost: async (slug: string) => {
    await apiClient.delete(`/posts/${slug}/like`)
  },
  getDrafts: async (params?: { page?: number; limit?: number }) => {
    console.log('getDrafts - calling API with params:', params)
    const response = await apiClient.get('/posts/drafts', {
      params: {
        page: params?.page || 1,
        limit: params?.limit || 10,
      },
    })
    console.log('getDrafts - response:', response.data)
    return response.data
  },
  publishDraft: async (slug: string) => {
    const response = await apiClient.post(`/posts/${slug}/publish`)
    return response.data
  },
}

export const useDrafts = (params?: { page?: number; limit?: number }) => {
  const isAuthenticated = !!localStorage.getItem('auth_token')
  return useQuery({
    queryKey: ['drafts', params?.page, params?.limit],
    queryFn: () => postsApi.getDrafts(params),
    enabled: isAuthenticated, // Only fetch when authenticated
    retry: false, // Don't retry on auth errors
  })
}

export const usePublishDraft = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (slug: string) => postsApi.publishDraft(slug),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['drafts'] })
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

export const usePosts = (params?: { page?: number; limit?: number; tag?: string; author?: string; sort?: string; exclude?: string }) => {
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

export const usePostSearch = (query: string, params?: { page?: number; limit?: number }) => {
  return useQuery({
    queryKey: ['posts', 'search', query, params],
    queryFn: () => postsApi.search(query, params),
    enabled: !!query && query.length > 2,
  })
}

