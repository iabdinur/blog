import { Heading, Text, HStack, VStack, Link, Image, Box, Icon } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { FaClock } from 'react-icons/fa'
import { Post } from '@/types'
import { formatShortDate } from '@/utils/date'
import { PostTags } from './PostTags'
import { Card } from '../ui/Card'
import { getFirstSentence } from '@/utils/text'

export interface PostCardProps {
  post: Post
}

export const PostCard = ({ post }: PostCardProps) => {
  return (
    <Card hoverable>
      <VStack align="stretch" spacing={3}>
        {post.coverImage && (
          <Link as={RouterLink} to={`/post/${post.slug}`} _hover={{ textDecoration: 'none' }}>
            <Image
              src={post.coverImage}
              alt={post.title}
              borderRadius="md"
              w="100%"
              h="200px"
              objectFit="cover"
            />
          </Link>
        )}
        <PostTags tags={post.tags} />
        <Link as={RouterLink} to={`/post/${post.slug}`} _hover={{ textDecoration: 'none' }}>
          <Heading fontSize="32px" noOfLines={2}>
            {post.title}
          </Heading>
        </Link>
        <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }}>
          {getFirstSentence(post.excerpt)}
        </Text>
        <HStack spacing={2} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
          <Text>{formatShortDate(post.publishedAt)}</Text>
          <Text>•</Text>
          <Text>{post.author.name}</Text>
          <Text>•</Text>
          <HStack spacing={1}>
            <Icon as={FaClock} />
            <Text>{post.readingTime} min read</Text>
          </HStack>
        </HStack>
      </VStack>
    </Card>
  )
}

