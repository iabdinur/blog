import { HStack, Text, Icon } from '@chakra-ui/react'
import { FaClock } from 'react-icons/fa'
import { Post } from '@/types'
import { formatDate } from '@/utils/date'

export interface PostMetaProps {
  post: Post
}

export const PostMeta = ({ post }: PostMetaProps) => {
  return (
    <HStack spacing={6} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
      <HStack>
        <Icon as={FaClock} />
        <Text>{formatDate(post.publishedAt)}</Text>
      </HStack>
      <HStack>
        <Icon as={FaClock} />
        <Text>{post.readingTime} min read</Text>
      </HStack>
    </HStack>
  )
}

