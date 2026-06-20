import { useEffect, useRef } from 'react'
import { Box, Heading, useColorMode } from '@chakra-ui/react'
import { giscusConfig } from '@/config/giscus'

export interface GiscusCommentsProps {
  slug: string
}

export const GiscusComments = ({ slug }: GiscusCommentsProps) => {
  const ref = useRef<HTMLDivElement>(null)
  const { colorMode } = useColorMode()

  useEffect(() => {
    if (!ref.current) return
    if (!giscusConfig.repo || !giscusConfig.repoId) return

    // Reset any previous instance (e.g. on slug change)
    ref.current.innerHTML = ''

    const script = document.createElement('script')
    script.src = 'https://giscus.app/client.js'
    script.async = true
    script.crossOrigin = 'anonymous'
    script.setAttribute('data-repo', giscusConfig.repo)
    script.setAttribute('data-repo-id', giscusConfig.repoId)
    script.setAttribute('data-category', giscusConfig.category)
    script.setAttribute('data-category-id', giscusConfig.categoryId)
    script.setAttribute('data-mapping', 'specific')
    script.setAttribute('data-term', slug)
    script.setAttribute('data-strict', '1')
    script.setAttribute('data-reactions-enabled', '1')
    script.setAttribute('data-emit-metadata', '0')
    script.setAttribute('data-input-position', 'top')
    script.setAttribute('data-theme', colorMode === 'dark' ? 'dark' : 'light')
    script.setAttribute('data-lang', 'en')
    ref.current.appendChild(script)
  }, [slug, colorMode])

  if (!giscusConfig.repo || !giscusConfig.repoId) return null

  return (
    <Box mt={8} id="comments-section">
      <Heading size="md" mb={4}>
        Comments
      </Heading>
      <div ref={ref} />
    </Box>
  )
}
