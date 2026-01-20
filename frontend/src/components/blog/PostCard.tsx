import { Heading, Text, HStack, VStack, Link, Image, Icon, Box } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { FaClock } from 'react-icons/fa'
import { Post } from '@/types'
import { formatShortDate, formatDate } from '@/utils/date'
import { PostTags } from './PostTags'
import { Card } from '../ui/Card'
import { getFirstSentence } from '@/utils/text'

export interface PostCardProps {
  post: Post
  showPublishedLabel?: boolean
  variant?: 'vertical' | 'horizontal'
}

export const PostCard = ({ post, showPublishedLabel = false, variant = 'vertical' }: PostCardProps) => {
  if (variant === 'horizontal') {
    return (
      <Link 
        as={RouterLink} 
        to={`/post/${post.slug}`} 
        _hover={{ textDecoration: 'none' }}
      >
        <Box 
          borderWidth="1px"
          borderRadius="lg"
          overflow="hidden"
          p={4}
          _hover={{ shadow: 'md' }}
        >
          <HStack spacing={4} align="start">
            <VStack align="start" spacing={2} flex={1}>
              <Heading as="h3" size="md">
                {post.title}
              </Heading>
              {post.excerpt && (
                <Text fontSize="sm" color="gray.500" noOfLines={2}>
                  {post.excerpt}
                </Text>
              )}
              <HStack spacing={4} fontSize="xs" color="gray.400">
                <Text>{formatDate(post.publishedAt)}</Text>
                <Text>•</Text>
                <Text>{post.author.name}</Text>
                {post.readingTime && (
                  <>
                    <Text>•</Text>
                    <Text>{post.readingTime} min read</Text>
                  </>
                )}
              </HStack>
            </VStack>
            {post.coverImage && (
              <Image
                src={post.coverImage}
                alt={post.title}
                w="200px"
                h="120px"
                objectFit="cover"
                borderRadius="md"
                flexShrink={0}
              />
            )}
          </HStack>
        </Box>
      </Link>
    )
  }

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
          {showPublishedLabel ? (
            <>
              <Text fontWeight="medium">Published:</Text>
              <Text>{formatShortDate(post.publishedAt)}</Text>
            </>
          ) : (
            <Text>{formatShortDate(post.publishedAt)}</Text>
          )}
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

