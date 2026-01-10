export interface NewsletterSubscription {
  id: string
  email: string
  status: 'active' | 'unsubscribed' | 'pending'
  subscribedAt: string
  preferences: {
    frequency: 'daily' | 'weekly' | 'monthly'
    categories: string[]
  }
}

export interface NewsletterPost {
  id: string
  postId: string
  sentAt: string
  openRate?: number
  clickRate?: number
}

