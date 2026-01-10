import { useMutation, useQuery } from '@tanstack/react-query'
import { apiClient } from './client'
import { NewsletterSubscription } from '@/types'

export const newsletterApi = {
  subscribe: async (email: string) => {
    const { data } = await apiClient.post<NewsletterSubscription>('/newsletter/subscribe', { email })
    return data
  },
  unsubscribe: async (email: string) => {
    await apiClient.post('/newsletter/unsubscribe', { email })
  },
  getSubscription: async (email: string) => {
    const { data } = await apiClient.get<NewsletterSubscription>(`/newsletter/subscription/${email}`)
    return data
  },
  updatePreferences: async (email: string, preferences: NewsletterSubscription['preferences']) => {
    const { data } = await apiClient.put<NewsletterSubscription>(`/newsletter/subscription/${email}`, { preferences })
    return data
  },
}

export const useSubscribeNewsletter = () => {
  return useMutation({
    mutationFn: newsletterApi.subscribe,
  })
}

export const useNewsletterSubscription = (email: string) => {
  return useQuery({
    queryKey: ['newsletter', email],
    queryFn: () => newsletterApi.getSubscription(email),
    enabled: !!email,
  })
}

