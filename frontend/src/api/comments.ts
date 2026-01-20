import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'
import { Comment } from '@/types'

export const commentsApi = {
  getByPost: async (slug: string) => {
    const { data } = await apiClient.get<Comment[]>(`/posts/${slug}/comments`)
    return data
  },
  create: async (slug: string, content: string, parentId?: string) => {
    const { data } = await apiClient.post<Comment>(`/posts/${slug}/comments`, { 
      content, 
      parentId: parentId || null
    })
    return data
  },
  update: async (slug: string, commentId: string, content: string) => {
    const { data } = await apiClient.put<Comment>(`/posts/${slug}/comments/${commentId}`, { content })
    return data
  },
  delete: async (slug: string, commentId: string) => {
    await apiClient.delete(`/posts/${slug}/comments/${commentId}`)
  },
  like: async (slug: string, commentId: string) => {
    await apiClient.post(`/posts/${slug}/comments/${commentId}/like`)
  },
}

export const useComments = (slug: string) => {
  return useQuery({
    queryKey: ['comments', slug],
    queryFn: () => commentsApi.getByPost(slug),
    enabled: !!slug,
  })
}

export const useCreateComment = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ slug, content, parentId }: { slug: string; content: string; parentId?: string }) =>
      commentsApi.create(slug, content, parentId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['comments', variables.slug] })
    },
  })
}

export const useUpdateComment = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ slug, commentId, content }: { slug: string; commentId: string; content: string }) =>
      commentsApi.update(slug, commentId, content),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['comments', variables.slug] })
    },
  })
}

export const useDeleteComment = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ slug, commentId }: { slug: string; commentId: string }) =>
      commentsApi.delete(slug, commentId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['comments', variables.slug] })
    },
  })
}

