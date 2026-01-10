import { useQuery } from '@tanstack/react-query'
import { Tag } from '@/types'
import { apiClient } from './client'

export const tagsApi = {
  getAll: async () => {
    const response = await apiClient.get('/tags')
    return response.data
  },
  getBySlug: async (slug: string) => {
    const response = await apiClient.get(`/tags/${slug}`)
    return response.data
  },
}

export const useTags = () => {
  return useQuery({
    queryKey: ['tags'],
    queryFn: () => tagsApi.getAll(),
  })
}

export const useTag = (slug: string) => {
  return useQuery({
    queryKey: ['tag', slug],
    queryFn: () => tagsApi.getBySlug(slug),
    enabled: !!slug,
  })
}

