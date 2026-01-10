import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'
import { Comment } from '@/types'

export const commentsApi = {
  getByPost: async (slug: string) => {
    const { data } = await apiClient.get<Comment[]>(`/posts/${slug}/comments`)
    return data
  },
  create: async (slug: string, content: string, authorId: string, parentId?: string) => {
    const { data } = await apiClient.post<Comment>(`/posts/${slug}/comments`, { 
      content, 
      authorId,
      parentId 
    })
    return data
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
    mutationFn: ({ slug, content, authorId, parentId }: { slug: string; content: string; authorId: string; parentId?: string }) =>
      commentsApi.create(slug, content, authorId, parentId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['comments', variables.slug] })
    },
  })
}

