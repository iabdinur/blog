import { VStack, Heading, Text, Spinner, Center, HStack, Button, IconButton, Flex, Divider } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'
import { useState, useMemo } from 'react'
import { CiSearch } from 'react-icons/ci'
import { usePosts } from '@/api/posts'
import { useTag } from '@/api/tags'
import { PostList } from '@/components/blog/PostList'
import { Layout } from '@/components/layout/Layout'
import { useUIStore } from '@/store/useUIStore'

export const Tag = () => {
  const { slug } = useParams<{ slug: string }>()
  const { data: tag, isLoading: tagLoading } = useTag(slug || '')
  const { data: postsData, isLoading: postsLoading } = usePosts({ tag: slug, limit: 12 })
  const [activeTab, setActiveTab] = useState<'latest' | 'top' | 'discussions'>('latest')
  const { setSearchPopupOpen } = useUIStore()

  // Sort posts based on active tab
  const sortedPosts = useMemo(() => {
    if (!postsData?.posts) return []
    
    const posts = [...postsData.posts]
    
    switch (activeTab) {
      case 'latest':
        return posts.sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
      case 'top':
        return posts.sort((a, b) => b.likes - a.likes)
      case 'discussions':
        return posts.sort((a, b) => b.commentsCount - a.commentsCount)
      default:
        return posts
    }
  }, [postsData?.posts, activeTab])

  if (tagLoading) {
    return (
      <Layout>
        <Center py={8}>
          <Spinner size="xl" />
        </Center>
      </Layout>
    )
  }

  if (!tag) {
    return (
      <Layout>
        <Center py={8}>
          <Text>Series not found</Text>
        </Center>
      </Layout>
    )
  }

  return (
    <Layout>
      <VStack align="stretch" spacing={6}>
        <VStack align="start" spacing={2}>
          <Heading fontSize="32px">#{tag.name}</Heading>
          {tag.description && (
            <Text color="gray.600" _dark={{ color: 'gray.400' }}>
              {tag.description}
            </Text>
          )}
          <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
            {tag.postsCount} posts
          </Text>
        </VStack>

        <Divider 
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

        <PostList posts={sortedPosts} isLoading={postsLoading} />
      </VStack>
    </Layout>
  )
}