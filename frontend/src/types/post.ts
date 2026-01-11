import { Author, Tag } from './index'

export interface Post {
  id: string
  title: string
  slug: string
  content: string
  excerpt: string
  coverImage?: string
  publishedAt: string
  updatedAt?: string
  author: Author
  tags: Tag[]
  readingTime: number
  views: number
  likes: number
  commentsCount: number
  isPublished: boolean
}

export interface PostMeta {
  title: string
  description: string
  image?: string
  publishedTime?: string
  modifiedTime?: string
  author?: string
  tags?: string[]
}

