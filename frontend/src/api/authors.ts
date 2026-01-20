import { useQuery } from '@tanstack/react-query'
import { apiClient } from './client'

export const authorsApi = {
  getById: async (id: string) => {
    const response = await apiClient.get(`/authors/${id}`)
    return response.data
  },
  getByUsername: async (username: string) => {
    const response = await apiClient.get(`/authors/${username}`)
    return response.data
  },
}

export const useAuthor = (idOrUsername: string) => {
  return useQuery({
    queryKey: ['author', idOrUsername],
    queryFn: () => authorsApi.getByUsername(idOrUsername),
    enabled: !!idOrUsername,
  })
}

