import { Box, useColorModeValue } from '@chakra-ui/react'
import { useEffect, useState } from 'react'

export const ReadingProgress = () => {
  const [progress, setProgress] = useState(0)
  const bgColor = useColorModeValue('brand.500', 'brand.400')

  useEffect(() => {
    const updateProgress = () => {
      const windowHeight = window.innerHeight
      const documentHeight = document.documentElement.scrollHeight
      const scrollTop = window.scrollY
      const scrollableHeight = documentHeight - windowHeight
      const progressPercent = (scrollTop / scrollableHeight) * 100
      setProgress(Math.min(100, Math.max(0, progressPercent)))
    }

    window.addEventListener('scroll', updateProgress)
    updateProgress()

    return () => window.removeEventListener('scroll', updateProgress)
  }, [])

  return (
    <Box
      position="fixed"
      top={0}
      left={0}
      right={0}
      h="3px"
      bg="gray.200"
      _dark={{ bg: 'gray.700' }}
      zIndex={1001}
    >
      <Box
        h="100%"
        bg={bgColor}
        transition="width 0.1s ease-out"
        style={{ width: `${progress}%` }}
      />
    </Box>
  )
}

