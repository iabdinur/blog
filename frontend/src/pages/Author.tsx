import { VStack, HStack, Heading, Text, Box, Image, Spinner, Center } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'
import { usePosts } from '@/api/posts'
import { useAuthor } from '@/api/authors'
import { PostList } from '@/components/blog/PostList'
import { Avatar } from '@/components/ui/Avatar'
import { Layout } from '@/components/layout/Layout'

export const Author = () => {
  const { username } = useParams<{ username: string }>()
  const { data: author, isLoading: authorLoading } = useAuthor(username || '')
  const { data: postsData, isLoading: postsLoading } = usePosts({ author: author?.username, limit: 12 })

  if (authorLoading) {
    return (
      <Layout>
        <Center py={8}>
          <Spinner size="xl" />
        </Center>
      </Layout>
    )
  }

  if (!author) {
    return (
      <Layout>
        <Center py={8}>
          <Text>Author not found</Text>
        </Center>
      </Layout>
    )
  }

  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Box>
          {author.coverImage && (
            <Image
              src={author.coverImage}
              alt={author.name}
              w="100%"
              h="200px"
              objectFit="cover"
              borderRadius="lg"
              mb={6}
            />
          )}
          <HStack spacing={4} mb={4}>
            <Avatar name={author.name} src="/images/profile.jpg" size="xl" />
            <VStack align="start" spacing={1}>
              <Heading fontSize="32px">{author.name}</Heading>
              <Text color="gray.600" _dark={{ color: 'gray.400' }}>
                @{author.username}
              </Text>
            </VStack>
          </HStack>
          {author.bio && (
            <Text mb={4} color="gray.600" _dark={{ color: 'gray.400' }}>
              {author.bio}
            </Text>
          )}
          <HStack spacing={6} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
            <Text>{author.postsCount} posts</Text>
            <Text>{author.followersCount} followers</Text>
            {author.location && <Text>{author.location}</Text>}
            {author.website && (
              <Text as="a" href={author.website} target="_blank" rel="noopener noreferrer" color="brand.500">
                Website
              </Text>
            )}
          </HStack>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={6}>
            Posts by {author.name}
          </Heading>
          <PostList posts={postsData?.posts || []} isLoading={postsLoading} />
        </Box>
      </VStack>
    </Layout>
  )
}

