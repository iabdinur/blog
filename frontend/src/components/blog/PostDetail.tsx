import { Box, Heading, VStack, Image, Text, HStack, Link, Icon, Divider, Center, Spinner, Flex, Button, IconButton } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { FaClock } from 'react-icons/fa'
import { CiSearch } from 'react-icons/ci'
import { useState } from 'react'
import { Post } from '@/types'
import { MarkdownRenderer } from '../ui/MarkdownRenderer'
import { PostTags } from './PostTags'
import { Avatar } from '../ui/Avatar'
import { formatDate } from '@/utils/date'
import { getFirstSentence } from '@/utils/text'
import { usePosts } from '@/api/posts'
import { useUIStore } from '@/store/useUIStore'

export interface PostDetailProps {
  post: Post
}

export const PostDetail = ({ post }: PostDetailProps) => {
  const [activeTab, setActiveTab] = useState<'latest' | 'top' | 'discussions'>('latest')
  const { setSearchPopupOpen } = useUIStore()

  // Fetch posts based on active tab excluding the current one
  const { data: tabPostsData, isLoading: isLoadingTab, isError: isErrorTab } = usePosts({ 
    limit: 4, 
    sort: activeTab,
    exclude: post.id?.toString() 
  })

  // Filter out the current post and take up to 3 for tab posts
  // Also filter out articles without valid slugs to prevent navigation errors
  const tabPosts = tabPostsData?.posts
    ?.filter((article: Post) => {
      // Ensure article has a valid slug (not empty, not undefined, and not the literal string "{slug}")
      const hasValidSlug = article.slug && 
                          article.slug.trim() !== '' && 
                          article.slug !== '{slug}' &&
                          article.slug !== '%7Bslug%7D'
      return article.id !== post.id && hasValidSlug
    })
    .slice(0, 3) || []

  return (
    <VStack align="stretch" spacing={6}>
      <VStack align="stretch" spacing={4}>
        <Heading fontSize="32px">{post.title}</Heading>
        <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }}>
          {getFirstSentence(post.excerpt)}
        </Text>
        <PostTags tags={post.tags} />
        <HStack spacing={3} align="start">
          <Link as={RouterLink} to="/author">
            <Avatar 
              name={post.author.name} 
              src={
                post.author.email 
                  ? `/api/v1/users/${encodeURIComponent(post.author.email)}/profile-image`
                  : undefined
              }
              size="md"
              onError={() => {
                // Silently fail if image can't load - will show initials instead
              }}
            />
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
                to="/author"
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

      {/* Divider after main content */}
      <Divider my={4} />

      {/* Tab Controls and Posts Section */}
      <Box mt={2}>
        <Heading as="h2" size="lg" mb={6}>
          More from this blog
        </Heading>
        
        {/* Tab Buttons with Search */}
        <Flex justify="space-between" align="center" w="100%" mb={6}>
          <HStack 
            spacing={0} 
            border="1px solid" 
            borderColor="gray.200" 
            borderRadius="md"
            w="fit-content"
            _dark={{ borderColor: 'gray.600' }}
          >
            <Button
              variant={activeTab === 'latest' ? 'solid' : 'ghost'}
              size="sm"
              onClick={() => setActiveTab('latest')}
              bg={activeTab === 'latest' ? 'gray.100' : 'transparent'}
              color={activeTab === 'latest' ? 'gray.900' : 'gray.600'}
              border="none"
              _dark={{
                bg: activeTab === 'latest' ? 'gray.700' : 'transparent',
                color: activeTab === 'latest' ? 'gray.100' : 'gray.400',
              }}
              _hover={{
                bg: activeTab === 'latest' ? 'gray.100' : 'gray.50',
                _dark: { bg: activeTab === 'latest' ? 'gray.700' : 'gray.800' },
              }}
              borderLeftRadius="md"
              borderRightRadius="0"
              fontWeight="normal"
            >
              Latest
            </Button>
            <Button
              variant={activeTab === 'top' ? 'solid' : 'ghost'}
              size="sm"
              onClick={() => setActiveTab('top')}
              bg={activeTab === 'top' ? 'gray.100' : 'transparent'}
              color={activeTab === 'top' ? 'gray.900' : 'gray.600'}
              border="none"
              _dark={{
                bg: activeTab === 'top' ? 'gray.700' : 'transparent',
                color: activeTab === 'top' ? 'gray.100' : 'gray.400',
              }}
              _hover={{
                bg: activeTab === 'top' ? 'gray.100' : 'gray.50',
                _dark: { bg: activeTab === 'top' ? 'gray.700' : 'gray.800' },
              }}
              borderRadius="0"
              fontWeight="normal"
            >
              Top
            </Button>
            <Button
              variant={activeTab === 'discussions' ? 'solid' : 'ghost'}
              size="sm"
              onClick={() => setActiveTab('discussions')}
              bg={activeTab === 'discussions' ? 'gray.100' : 'transparent'}
              color={activeTab === 'discussions' ? 'gray.900' : 'gray.600'}
              border="none"
              _dark={{
                bg: activeTab === 'discussions' ? 'gray.700' : 'transparent',
                color: activeTab === 'discussions' ? 'gray.100' : 'gray.400',
              }}
              _hover={{
                bg: activeTab === 'discussions' ? 'gray.100' : 'gray.50',
                _dark: { bg: activeTab === 'discussions' ? 'gray.700' : 'gray.800' },
              }}
              borderLeftRadius="0"
              borderRightRadius="md"
              fontWeight="normal"
            >
              Discussions
            </Button>
          </HStack>

          {/* Search Icon */}
          <IconButton
            aria-label="Search"
            icon={<CiSearch size={20} />}
            variant="ghost"
            onClick={() => setSearchPopupOpen(true)}
            size="sm"
          />
        </Flex>

        {/* Posts List based on active tab - limited to 3 */}
        {isLoadingTab ? (
          <Center>
            <Spinner />
          </Center>
        ) : isErrorTab ? (
          <Text>Failed to load posts.</Text>
        ) : tabPosts.length > 0 ? (
          <VStack spacing={4} align="stretch">
            {tabPosts.map((article: Post) => {
              // Double-check slug is valid before rendering link
              if (!article.slug || article.slug === '{slug}' || article.slug === '%7Bslug%7D') {
                return null
              }
              return (
                <Link 
                  as={RouterLink} 
                  to={`/post/${article.slug}`} 
                  key={article.id}
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
                        {article.title}
                      </Heading>
                      {article.excerpt && (
                        <Text fontSize="sm" color="gray.500" noOfLines={2}>
                          {article.excerpt}
                        </Text>
                      )}
                      <HStack spacing={4} fontSize="xs" color="gray.400">
                        <Text>{formatDate(article.publishedAt)}</Text>
                        <Text>•</Text>
                        <Text>{article.author.name}</Text>
                        {article.readingTime && (
                          <>
                            <Text>•</Text>
                            <Text>{article.readingTime} min read</Text>
                          </>
                        )}
                      </HStack>
                    </VStack>
                    {article.coverImage && (
                      <Image
                        src={article.coverImage}
                        alt={article.title}
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
            })}
          </VStack>
        ) : (
          <Text color="gray.500" _dark={{ color: 'gray.400' }}>No posts found.</Text>
        )}
      </Box>
    </VStack>
  )
}
