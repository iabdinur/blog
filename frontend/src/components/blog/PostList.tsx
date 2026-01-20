import { VStack, Spinner, Center, Text } from '@chakra-ui/react'
import { Post } from '@/types'
import { PostCard } from './PostCard'

export interface PostListProps {
  posts: Post[]
  isLoading?: boolean
  showPublishedLabel?: boolean
  variant?: 'vertical' | 'horizontal'
}

export const PostList = ({ posts, isLoading, showPublishedLabel, variant = 'vertical' }: PostListProps) => {
  if (isLoading) {
    return (
      <Center py={8}>
        <Spinner size="xl" />
      </Center>
    )
  }

  if (posts.length === 0) {
    return (
      <Center py={8}>
        <Text color="gray.500" _dark={{ color: 'gray.400' }}>No posts found.</Text>
      </Center>
    )
  }

  return (
    <VStack spacing={6} align="stretch">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} showPublishedLabel={showPublishedLabel} variant={variant} />
      ))}
    </VStack>
  )
}

