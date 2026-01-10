import { create } from 'zustand'
import { NewsletterSubscription } from '@/types'

interface NewsletterState {
  subscription: NewsletterSubscription | null
  setSubscription: (subscription: NewsletterSubscription | null) => void
}

export const useNewsletterStore = create<NewsletterState>((set) => ({
  subscription: null,
  setSubscription: (subscription) => set({ subscription }),
}))

