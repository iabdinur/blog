import { Author, Tag } from './index'

export interface Post {
  id: string
  title: string
  slug: string
  content: string
  excerpt: string
  coverImage?: string
  contentImage?: string
  publishedAt: string
  scheduledAt?: string
  updatedAt?: string
  author: Author
  tags: Tag[]
  readingTime: number
  views: number
  likes: number
  commentsCount: number
  isPublished: boolean
}

