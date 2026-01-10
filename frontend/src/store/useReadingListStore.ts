import { create } from 'zustand'
import { Post } from '@/types'

interface ReadingListState {
  posts: Post[]
  addPost: (post: Post) => void
  removePost: (postId: string) => void
  isInList: (postId: string) => boolean
}

const getInitialPosts = (): Post[] => {
  if (typeof window === 'undefined') return []
  try {
    return JSON.parse(localStorage.getItem('readingList') || '[]')
  } catch {
    return []
  }
}

export const useReadingListStore = create<ReadingListState>((set, get) => ({
  posts: getInitialPosts(),
  addPost: (post) =>
    set((state) => {
      if (state.posts.some((p: Post) => p.id === post.id)) return state
      const newPosts = [...state.posts, post]
      if (typeof window !== 'undefined') {
        localStorage.setItem('readingList', JSON.stringify(newPosts))
      }
      return { posts: newPosts }
    }),
  removePost: (postId) =>
    set((state) => {
      const newPosts = state.posts.filter((p: Post) => p.id !== postId)
      if (typeof window !== 'undefined') {
        localStorage.setItem('readingList', JSON.stringify(newPosts))
      }
      return { posts: newPosts }
    }),
  isInList: (postId: string): boolean => {
    const state = get()
    return state.posts.some((p: Post) => p.id === postId)
  },
}))

