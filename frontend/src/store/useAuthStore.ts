import { create } from 'zustand'
import { Author } from '@/types'

interface AuthState {
  user: Author | null
  isAuthenticated: boolean
  setUser: (user: Author | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  setUser: (user) => set({ user, isAuthenticated: !!user }),
  logout: () => {
    localStorage.removeItem('auth_token')
    set({ user: null, isAuthenticated: false })
  },
}))

