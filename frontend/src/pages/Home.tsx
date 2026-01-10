import { VStack, Heading, Text, Box, Image, Divider, HStack, Button, IconButton, Flex, Link, Center, Icon } from '@chakra-ui/react'
import { useState } from 'react'
import { Link as RouterLink } from 'react-router-dom'
import { CiSearch } from 'react-icons/ci'
import { FaClock } from 'react-icons/fa'
import { usePosts, usePost } from '@/api/posts'
import { PostList } from '@/components/blog/PostList'
import { NewsletterCard } from '@/components/newsletter/NewsletterCard'
import { Layout } from '@/components/layout/Layout'
import { Card } from '@/components/ui/Card'
import { formatShortDate } from '@/utils/date'
import { useUIStore } from '@/store/useUIStore'
import { getFirstSentence } from '@/utils/text'

export const Home = () => {
  const [activeTab, setActiveTab] = useState<'latest' | 'top' | 'discussions'>('latest')
  const { data, isLoading, error } = usePosts({ limit: 6, sort: activeTab })
  const { data: featuredPost } = usePost('welcome-to-our-weekly-newsletter')
  const { setSearchPopupOpen } = useUIStore()

  const posts = data?.posts || []

  if (error) {
    return (
      <Layout>
        <Center py={8}>
          <VStack spacing={4}>
            <Text fontSize="lg" color="red.500" _dark={{ color: 'red.400' }}>
              Failed to load posts
            </Text>
            <Text fontSize="sm" color="gray.500" _dark={{ color: 'gray.400' }}>
              {error instanceof Error ? error.message : 'An error occurred'}
            </Text>
          </VStack>
        </Center>
      </Layout>
    )
  }

  return (
    <Layout>
      <VStack align="stretch" spacing={12}>
        {/* Featured Article */}
        {featuredPost && (
          <Card>
            <Link as={RouterLink} to={`/post/${featuredPost.slug}`} _hover={{ textDecoration: 'none' }}>
              <Box>
                {featuredPost.coverImage && (
                  <Image
                    src={featuredPost.coverImage}
                    alt={featuredPost.title}
                    w="100%"
                    h={{ base: "250px", md: "400px" }}
                    objectFit="cover"
                    borderRadius="md"
                    mb={6}
                  />
                )}
                <VStack align="stretch" spacing={4}>
                  <Heading fontSize="32px" fontWeight="bold">
                    {featuredPost.title}
                  </Heading>
                  <HStack spacing={2} fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
                    <Text>{formatShortDate(featuredPost.publishedAt)}</Text>
                    <Text>•</Text>
                    <Text>{featuredPost.author.name}</Text>
                    <Text>•</Text>
                    <HStack spacing={1}>
                      <Icon as={FaClock} />
                      <Text>{featuredPost.readingTime} min read</Text>
                    </HStack>
                  </HStack>
                  <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }} lineHeight="tall">
                    {getFirstSentence(featuredPost.excerpt)}
                  </Text>
                </VStack>
              </Box>
            </Link>
          </Card>
        )}

        {/* Divider */}
        <Box 
          borderTop="1px" 
          borderColor="gray.200" 
          _dark={{ borderColor: 'gray.700' }}
          mx={{ base: -4, md: -8 }}
        />

        {/* Tab Buttons with Search */}
        <Flex justify="space-between" align="center" w="100%">
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

        <Box>
          <PostList posts={posts} isLoading={isLoading} />
        </Box>
      </VStack>
    </Layout>
  )
}

