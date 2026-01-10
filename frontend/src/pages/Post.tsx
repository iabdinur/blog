import { Box, Flex, Spinner, Center, Text } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'
import { usePost } from '@/api/posts'
import { PostDetail } from '@/components/blog/PostDetail'
import { PostShare } from '@/components/blog/PostShare'
import { PostReactions } from '@/components/blog/PostReactions'
import { PostComments } from '@/components/blog/PostComments'
import { TableOfContents } from '@/components/blog/TableOfContents'
import { ReadingProgress } from '@/components/blog/ReadingProgress'
import { Layout } from '@/components/layout/Layout'

export const Post = () => {
  const { slug } = useParams<{ slug: string }>()
  const { data: post, isLoading, error } = usePost(slug || '')

  if (isLoading) {
    return (
      <Layout>
        <Center py={8}>
          <Spinner size="xl" />
        </Center>
      </Layout>
    )
  }

  if (error || !post) {
    return (
      <Layout>
        <Center py={8}>
          <Text>Post not found</Text>
        </Center>
      </Layout>
    )
  }

  return (
    <Layout>
      <ReadingProgress />
      <Box>
        <PostDetail post={post} />
        <Box mt={8} pt={8} borderTop="1px" borderColor="gray.200" _dark={{ borderColor: 'gray.700' }}>
          <Flex justify="space-between" align="center" mb={6}>
            <PostReactions post={post} />
            <PostShare post={post} />
          </Flex>
        </Box>
        <PostComments slug={post.slug} />
      </Box>
    </Layout>
  )
}

