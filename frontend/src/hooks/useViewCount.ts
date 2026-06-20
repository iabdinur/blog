import { useQuery } from '@tanstack/react-query'
import { analyticsEnabled, goatcounterEndpoint } from '@/config/analytics'

// Best-effort live view count from GoatCounter's public counter API.
// Returns null when analytics is disabled or the count is unavailable.
export const useViewCount = (path: string) => {
  return useQuery({
    queryKey: ['views', path],
    enabled: analyticsEnabled && !!path,
    retry: false,
    staleTime: 1000 * 60 * 5,
    queryFn: async (): Promise<string | null> => {
      try {
        const res = await fetch(`${goatcounterEndpoint}/counter/${encodeURIComponent(path)}.json`)
        if (!res.ok) return null
        const json = await res.json()
        return typeof json.count === 'string' ? json.count : null
      } catch {
        return null
      }
    },
  })
}
