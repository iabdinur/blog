import { HStack, Link } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { Tag } from '@/types'
import { Badge } from '../ui/Badge'

export interface PostTagsProps {
  tags: Tag[]
}

export const PostTags = ({ tags }: PostTagsProps) => {
  if (tags.length === 0) return null

  // Sort tags alphabetically by name
  const sortedTags = [...tags].sort((a, b) => a.name.localeCompare(b.name))

  return (
    <HStack spacing={2} flexWrap="wrap">
      {sortedTags.map((tag) => (
        <Link key={tag.id} as={RouterLink} to={`/series/${tag.slug}`} _hover={{ textDecoration: 'none' }}>
          <Badge colorScheme="brand" variant="subtle">
            {tag.name}
          </Badge>
        </Link>
      ))}
    </HStack>
  )
}

