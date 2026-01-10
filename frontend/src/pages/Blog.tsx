import { VStack, HStack, Button, IconButton, Flex, Text, Center } from '@chakra-ui/react'
import { useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { usePosts } from '@/api/posts'
import { PostList } from '@/components/blog/PostList'
import { Layout } from '@/components/layout/Layout'
import { useUIStore } from '@/store/useUIStore'

export const Blog = () => {
  const [activeTab, setActiveTab] = useState<'latest' | 'top' | 'discussions'>('latest')
  const { data, isLoading, error } = usePosts({ limit: 12, sort: activeTab })
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
      <VStack align="stretch" spacing={6}>
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

        <PostList posts={posts} isLoading={isLoading} />
      </VStack>
    </Layout>
  )
}

