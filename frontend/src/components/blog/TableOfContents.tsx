import { Box, VStack, Link, Text, useColorModeValue } from '@chakra-ui/react'
import { useEffect, useState } from 'react'

interface Heading {
  id: string
  text: string
  level: number
}

export const TableOfContents = () => {
  const [headings, setHeadings] = useState<Heading[]>([])
  const [activeId, setActiveId] = useState<string>('')
  const bg = useColorModeValue('gray.50', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')

  useEffect(() => {
    const headingElements = document.querySelectorAll('h1, h2, h3, h4, h5, h6')
    const headingData: Heading[] = Array.from(headingElements).map((heading) => {
      const id = heading.id || heading.textContent?.toLowerCase().replace(/\s+/g, '-') || ''
      if (!heading.id) {
        heading.id = id
      }
      return {
        id,
        text: heading.textContent || '',
        level: parseInt(heading.tagName.charAt(1)),
      }
    })
    setHeadings(headingData)
  }, [])

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setActiveId(entry.target.id)
          }
        })
      },
      { rootMargin: '-20% 0% -35% 0%' }
    )

    headings.forEach((heading) => {
      const element = document.getElementById(heading.id)
      if (element) observer.observe(element)
    })

    return () => {
      headings.forEach((heading) => {
        const element = document.getElementById(heading.id)
        if (element) observer.unobserve(element)
      })
    }
  }, [headings])

  if (headings.length === 0) return null

  return (
    <Box
      position="sticky"
      top="80px"
      bg={bg}
      borderRadius="lg"
      p={4}
      border="1px"
      borderColor={borderColor}
      maxH="calc(100vh - 100px)"
      overflowY="auto"
    >
      <Text fontWeight="bold" mb={3} fontSize="sm">
        Table of Contents
      </Text>
      <VStack align="stretch" spacing={1}>
        {headings.map((heading) => (
          <Link
            key={heading.id}
            href={`#${heading.id}`}
            fontSize="sm"
            pl={(heading.level - 1) * 4}
            color={activeId === heading.id ? 'brand.500' : 'gray.600'}
            _dark={{ color: activeId === heading.id ? 'brand.400' : 'gray.400' }}
            _hover={{ color: 'brand.500' }}
            onClick={(e) => {
              e.preventDefault()
              document.getElementById(heading.id)?.scrollIntoView({ behavior: 'smooth' })
            }}
          >
            {heading.text}
          </Link>
        ))}
      </VStack>
    </Box>
  )
}

