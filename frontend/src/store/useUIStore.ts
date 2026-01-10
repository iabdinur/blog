import { create } from 'zustand'

interface UIState {
  theme: 'light' | 'dark'
  sidebarOpen: boolean
  newsletterPopupOpen: boolean
  searchPopupOpen: boolean
  setTheme: (theme: 'light' | 'dark') => void
  toggleSidebar: () => void
  setSidebarOpen: (open: boolean) => void
  setNewsletterPopupOpen: (open: boolean) => void
  setSearchPopupOpen: (open: boolean) => void
}

export const useUIStore = create<UIState>((set) => ({
  theme: (localStorage.getItem('theme') as 'light' | 'dark') || 'light',
  sidebarOpen: false,
  newsletterPopupOpen: false,
  searchPopupOpen: false,
  setTheme: (theme) => {
    localStorage.setItem('theme', theme)
    set({ theme })
  },
  toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
  setSidebarOpen: (open) => set({ sidebarOpen: open }),
  setNewsletterPopupOpen: (open) => set({ newsletterPopupOpen: open }),
  setSearchPopupOpen: (open) => set({ searchPopupOpen: open }),
}))

