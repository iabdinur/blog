export interface Author {
  id: string
  name: string
  username: string
  email: string
  bio?: string
  avatar?: string
  coverImage?: string
  location?: string
  website?: string
  socialLinks?: {
    twitter?: string
    github?: string
    linkedin?: string
  }
  followersCount: number
  followingCount: number
  postsCount: number
  joinedAt: string
}

