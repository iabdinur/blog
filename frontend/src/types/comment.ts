import { Author } from './author'

export interface Comment {
  id: string
  content: string
  author: Author
  postId: string
  parentId?: string
  replies?: Comment[]
  likes: number
  createdAt: string
  updatedAt?: string
}

