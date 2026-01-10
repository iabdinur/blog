import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'

// Account API
export const accountApi = {
  login: async (username: string, password: string) => {
    const response = await apiClient.post('/accounts/login', { username, password })
    if (response.data.token) {
      localStorage.setItem('auth_token', response.data.token)
    }
    return response.data
  },
  sendVerificationCode: async (email: string) => {
    const response = await apiClient.post('/accounts/send-code', { email })
    return response.data
  },
  verifyCode: async (email: string, code: string) => {
    const response = await apiClient.post('/accounts/verify-code', { email, code })
    if (response.data.token) {
      localStorage.setItem('auth_token', response.data.token)
    }
    return response.data
  },
  logout: () => {
    localStorage.removeItem('auth_token')
  },
}

// Posts Admin API
export const adminPostsApi = {
  create: async (post: any) => {
    const response = await apiClient.post('/posts', post)
    return response.data
  },
  update: async (slug: string, post: any) => {
    const response = await apiClient.put(`/posts/${slug}`, post)
    return response.data
  },
  delete: async (slug: string) => {
    await apiClient.delete(`/posts/${slug}`)
  },
  getBySlug: async (slug: string) => {
    const response = await apiClient.get(`/posts/${slug}/admin`)
    return response.data
  },
}

// Tags Admin API
export const adminTagsApi = {
  create: async (tag: any) => {
    const response = await apiClient.post('/tags', tag)
    return response.data
  },
  update: async (slug: string, tag: any) => {
    const response = await apiClient.put(`/tags/${slug}`, tag)
    return response.data
  },
  delete: async (slug: string) => {
    await apiClient.delete(`/tags/${slug}`)
  },
}

// Authors Admin API
export const adminAuthorsApi = {
  getAll: async () => {
    const response = await apiClient.get('/authors')
    return response.data
  },
  create: async (author: any) => {
    const response = await apiClient.post('/authors', author)
    return response.data
  },
  update: async (username: string, author: any) => {
    const response = await apiClient.put(`/authors/${username}`, author)
    return response.data
  },
  delete: async (username: string) => {
    await apiClient.delete(`/authors/${username}`)
  },
}

// React Query hooks
export const useLogin = () => {
  return useMutation({
    mutationFn: ({ username, password }: { username: string; password: string }) =>
      accountApi.login(username, password),
  })
}

export const useSendVerificationCode = () => {
  return useMutation({
    mutationFn: ({ email }: { email: string }) => accountApi.sendVerificationCode(email),
  })
}

export const useVerifyCode = () => {
  return useMutation({
    mutationFn: ({ email, code }: { email: string; code: string }) =>
      accountApi.verifyCode(email, code),
  })
}

export const useCreatePost = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminPostsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

export const useUpdatePost = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ slug, post }: { slug: string; post: any }) => adminPostsApi.update(slug, post),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

export const useDeletePost = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminPostsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

export const useCreateTag = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminTagsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tags'] })
    },
  })
}

export const useUpdateTag = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ slug, tag }: { slug: string; tag: any }) => adminTagsApi.update(slug, tag),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tags'] })
    },
  })
}

export const useDeleteTag = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminTagsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tags'] })
    },
  })
}

export const useAuthors = () => {
  return useQuery({
    queryKey: ['authors', 'all'],
    queryFn: adminAuthorsApi.getAll,
  })
}

export const useCreateAuthor = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminAuthorsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['authors'] })
    },
  })
}

export const useUpdateAuthor = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ username, author }: { username: string; author: any }) =>
      adminAuthorsApi.update(username, author),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['authors'] })
    },
  })
}

export const useDeleteAuthor = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: adminAuthorsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['authors'] })
    },
  })
}

