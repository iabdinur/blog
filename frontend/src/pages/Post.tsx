import { Box, Flex, Spinner, Center, Text } from '@chakra-ui/react'
import { useEffect, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { usePost } from '@/api/posts'
import { useComments } from '@/api/comments'
import { postsApi } from '@/api/posts'
import { useQueryClient } from '@tanstack/react-query'
import { PostDetail } from '@/components/blog/PostDetail'
import { PostShare } from '@/components/blog/PostShare'
import { PostReactions } from '@/components/blog/PostReactions'
import { PostComments } from '@/components/blog/PostComments'
import { ReadingProgress } from '@/components/blog/ReadingProgress'
import { Layout } from '@/components/layout/Layout'

export const Post = () => {
  const { slug } = useParams<{ slug: string }>()
  
  // Validate slug - if it's the literal string "{slug}" or URL-encoded version, treat as invalid
  const isValidSlug = slug && 
                       slug.trim() !== '' && 
                       slug !== '{slug}' && 
                       slug !== '%7Bslug%7D'
  
  const { data: post, isLoading, error } = usePost(isValidSlug ? slug : '')
  const { data: comments } = useComments(isValidSlug ? slug : '')
  const queryClient = useQueryClient()
  const viewedPosts = useRef<Set<string>>(new Set())

  useEffect(() => {
    if (slug && post && !viewedPosts.current.has(slug)) {
      viewedPosts.current.add(slug)
      // Increment views when post is loaded (only once per post per session)
      postsApi.incrementViews(slug).then(() => {
        // Refresh post data to get updated view count
        queryClient.invalidateQueries({ queryKey: ['post', slug] })
      }).catch(() => {
        // Silently fail if view increment fails
      })
    }
  }, [slug, post, queryClient])

  if (isLoading) {
    return (
      <Layout>
        <Center py={8}>
          <Spinner size="xl" />
        </Center>
      </Layout>
    )
  }

  if (!isValidSlug) {
    return (
      <Layout>
        <Center py={8}>
          <Text>Invalid post URL</Text>
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
            <PostReactions post={post} commentsCount={comments?.length} />
            <PostShare post={post} />
          </Flex>
        </Box>
        <PostComments slug={post.slug} />
      </Box>
    </Layout>
  )
}

