import { useQuery } from '@tanstack/react-query'
import { Tag } from '@/types'
import { fetchData } from './static'

export const tagsApi = {
  getAll: async (): Promise<Tag[]> => fetchData<Tag[]>('tags.json'),
  getBySlug: async (slug: string): Promise<Tag | undefined> => {
    const tags = await fetchData<Tag[]>('tags.json')
    return tags.find((t) => t.slug === slug)
  },
}

export const useTags = () => {
  return useQuery({
    queryKey: ['tags'],
    queryFn: () => tagsApi.getAll(),
  })
}

export const useTag = (slug: string) => {
  return useQuery({
    queryKey: ['tag', slug],
    queryFn: () => tagsApi.getBySlug(slug),
    enabled: !!slug,
  })
}
