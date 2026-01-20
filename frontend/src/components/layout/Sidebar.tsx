import {
  Box,
  VStack,
  Link,
  Text,
  useColorModeValue,
  Divider,
} from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { useTags } from '@/api/tags'
import { Badge } from '../ui/Badge'
import { Tag } from '@/types'

export const Sidebar = () => {
  const { data: tagsData, isLoading } = useTags()
  const bg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')

  return (
    <Box
      position="sticky"
      top="80px"
      bg={bg}
      borderRadius="lg"
      p={6}
      border="1px"
      borderColor={borderColor}
      h="fit-content"
    >
      <VStack align="stretch" spacing={4}>
        <Text fontSize="lg" fontWeight="bold">
          Popular Tags
        </Text>
        <Divider />
        {isLoading ? (
          <Text fontSize="sm" color="gray.500" _dark={{ color: 'gray.400' }}>
            Loading tags...
          </Text>
        ) : (
          <VStack align="stretch" spacing={2}>
            {tagsData
              ?.slice()
              .sort((a: Tag, b: Tag) => a.name.localeCompare(b.name))
              .slice(0, 10)
              .map((tag: Tag) => (
                <Link
                  key={tag.id}
                  as={RouterLink}
                  to={`/series/${tag.slug}`}
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  _hover={{ color: 'brand.500' }}
                >
                  <Text fontSize="sm">{tag.name}</Text>
                  <Badge>{tag.postsCount}</Badge>
                </Link>
              ))}
          </VStack>
        )}
      </VStack>
    </Box>
  )
}

