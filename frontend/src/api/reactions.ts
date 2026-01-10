import { useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'

export const reactionsApi = {
  like: async (postId: string) => {
    const { data } = await apiClient.post(`/posts/${postId}/like`)
    return data
  },
  unlike: async (postId: string) => {
    const { data } = await apiClient.delete(`/posts/${postId}/like`)
    return data
  },
}

export const useLikePost = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: reactionsApi.like,
    onSuccess: (_, postId) => {
      queryClient.invalidateQueries({ queryKey: ['post', postId] })
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

