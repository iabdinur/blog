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
    github?: string
    linkedin?: string
  }
  followersCount: number
  postsCount: number
  joinedAt: string
}

