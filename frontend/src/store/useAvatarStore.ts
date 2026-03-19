import { create } from 'zustand'

interface AvatarState {
  avatarVersion: number
  bumpAvatarVersion: () => void
  setAvatarVersion: (version: number) => void
}

export const useAvatarStore = create<AvatarState>((set) => ({
  avatarVersion: 0,
  bumpAvatarVersion: () => set({ avatarVersion: Date.now() }),
  setAvatarVersion: (version) => set({ avatarVersion: version }),
}))

