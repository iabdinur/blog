import { Box, Heading, VStack, Image, Text, HStack, Link, Icon } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { FaClock } from 'react-icons/fa'
import { Post } from '@/types'
import { MarkdownRenderer } from '../ui/MarkdownRenderer'
import { PostTags } from './PostTags'
import { Avatar } from '../ui/Avatar'
import { formatDate } from '@/utils/date'
import { getFirstSentence } from '@/utils/text'

export interface PostDetailProps {
  post: Post
}

export const PostDetail = ({ post }: PostDetailProps) => {
  return (
    <VStack align="stretch" spacing={6}>
      <VStack align="stretch" spacing={4}>
        <Heading fontSize="32px">{post.title}</Heading>
        <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }}>
          {getFirstSentence(post.excerpt)}
        </Text>
        <PostTags tags={post.tags} />
        <HStack spacing={3} align="start">
          <Link as={RouterLink} to={`/author/${post.author.username}`}>
            <Avatar name={post.author.name} src="/images/profile.jpg" size="md" />
          </Link>
          <VStack align="start" spacing={1}>
            <HStack spacing={6} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
              <Text>{formatDate(post.publishedAt)}</Text>
              <HStack spacing={1}>
                <Icon as={FaClock} />
                <Text>{post.readingTime} min read</Text>
              </HStack>
            </HStack>
            <HStack spacing={1}>
              <Text fontSize="md" color="gray.600" _dark={{ color: 'gray.400' }}>By</Text>
              <Link
                as={RouterLink}
                to={`/author/${post.author.username}`}
                fontWeight="medium"
                fontSize="md"
                _hover={{ color: 'brand.500' }}
              >
                {post.author.name}
              </Link>
            </HStack>
          </VStack>
        </HStack>
      </VStack>

      {post.coverImage && (
        <Image
          src={post.coverImage}
          alt={post.title}
          borderRadius="lg"
          w="100%"
          maxH="500px"
          objectFit="cover"
        />
      )}

      <Box>
        <MarkdownRenderer content={post.content} />
      </Box>
    </VStack>
  )
}

