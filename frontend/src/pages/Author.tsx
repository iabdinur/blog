import { VStack, HStack, Heading, Text, Box, Image, Spinner, Center, Link, Icon } from '@chakra-ui/react'
import { usePosts } from '@/api/posts'
import { useAuthor } from '@/api/authors'
import { PostList } from '@/components/blog/PostList'
import { Avatar } from '@/components/ui/Avatar'
import { Layout } from '@/components/layout/Layout'
import { FaGithub, FaLinkedin, FaRss } from 'react-icons/fa'

export const Author = () => {
  // Hardcode username for personal blog
  const username = 'iabdinur'
  const { data: author, isLoading: authorLoading } = useAuthor(username)
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
            <Avatar 
              name={author.name} 
              src={
                author.email 
                  ? `/api/v1/users/${encodeURIComponent(author.email)}/profile-image`
                  : undefined
              }
              size="lg"
              onError={() => {
                // Silently fail if image can't load - will show initials instead
              }}
            />
            <VStack align="start" spacing={1} flex={1}>
              <Heading fontSize="xl" fontWeight="bold">Ibrahim Abdinur's Blog</Heading>
              {((author.website && author.website.trim() !== '') ||
                (author.socialLinks?.github && author.socialLinks.github.trim() !== '') || 
                (author.socialLinks?.linkedin && author.socialLinks.linkedin.trim() !== '')) && (
                <HStack spacing={4}>
                  {author.website && author.website.trim() !== '' && (
                    <Link
                      href={author.website.trim().startsWith('http') 
                        ? author.website.trim() 
                        : `https://${author.website.trim()}`}
                      isExternal
                      color="gray.600"
                      _dark={{ color: 'gray.400' }}
                      _hover={{ color: 'gray.900', _dark: { color: 'gray.200' } }}
                      aria-label="Website"
                      title={`Visit ${author.name}'s website`}
                    >
                      <Icon as={FaRss} boxSize={5} />
                    </Link>
                  )}
                  {author.socialLinks?.github && author.socialLinks.github.trim() !== '' && (
                    <Link
                      href={`https://github.com/${author.socialLinks.github.trim()}`}
                      isExternal
                      color="gray.600"
                      _dark={{ color: 'gray.400' }}
                      _hover={{ color: 'gray.900', _dark: { color: 'gray.200' } }}
                      aria-label="GitHub"
                      title={`View ${author.socialLinks.github.trim()} on GitHub`}
                    >
                      <Icon as={FaGithub} boxSize={5} />
                    </Link>
                  )}
                  {author.socialLinks?.linkedin && author.socialLinks.linkedin.trim() !== '' && (
                    <Link
                      href={author.socialLinks.linkedin.trim().startsWith('http') 
                        ? author.socialLinks.linkedin.trim() 
                        : `https://linkedin.com/in/${author.socialLinks.linkedin.trim()}`}
                      isExternal
                      color="gray.600"
                      _dark={{ color: 'gray.400' }}
                      _hover={{ color: 'gray.900', _dark: { color: 'gray.200' } }}
                      aria-label="LinkedIn"
                      title={`Connect with ${author.name} on LinkedIn`}
                    >
                      <Icon as={FaLinkedin} boxSize={5} />
                    </Link>
                  )}
                </HStack>
              )}
            </VStack>
          </HStack>
          {author.bio && (
            <Text mb={4} color="gray.600" _dark={{ color: 'gray.400' }}>
              {author.bio}
            </Text>
          )}
          <HStack spacing={6} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }} flexWrap="wrap">
            <Text>{author.postsCount} posts published</Text>
            {author.location && <Text>{author.location}</Text>}
          </HStack>
        </Box>

        <Box>
          <Heading fontSize="24px" mb={6}>
            Posts
          </Heading>
          <PostList posts={postsData?.posts || []} isLoading={postsLoading} />
        </Box>
      </VStack>
    </Layout>
  )
}

