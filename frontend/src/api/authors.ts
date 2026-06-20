import { useQuery } from '@tanstack/react-query'
import { Author } from '@/types'
import { fetchData } from './static'

export const authorsApi = {
  getByUsername: async (username: string): Promise<Author> =>
    fetchData<Author>(`authors/${username}.json`),
}

export const useAuthor = (username: string) => {
  return useQuery({
    queryKey: ['author', username],
    queryFn: () => authorsApi.getByUsername(username),
    enabled: !!username,
  })
}
