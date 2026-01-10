import { VStack, Heading, Text, Spinner, Center, HStack, Button, IconButton, Flex, Box } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'
import { useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { usePosts } from '@/api/posts'
import { useTag } from '@/api/tags'
import { PostList } from '@/components/blog/PostList'
import { Layout } from '@/components/layout/Layout'
import { useUIStore } from '@/store/useUIStore'

export const Series = () => {
  const { slug } = useParams<{ slug: string }>()
  const [activeTab, setActiveTab] = useState<'latest' | 'top' | 'discussions'>('latest')
  const { data: tag, isLoading: tagLoading } = useTag(slug || '')
  const { data: postsData, isLoading: postsLoading } = usePosts({ tag: slug, limit: 1000, sort: activeTab })
  const { setSearchPopupOpen } = useUIStore()

  const posts = postsData?.posts || []

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
          <Heading fontSize="32px">{tag.name}</Heading>
          {tag.description && (
            <Text color="gray.600" _dark={{ color: 'gray.400' }}>
              {tag.description}
            </Text>
          )}
          <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
            {postsData?.total || 0} {(postsData?.total || 0) === 1 ? 'post' : 'posts'}
          </Text>
        </VStack>

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

        <PostList posts={posts} isLoading={postsLoading} />
      </VStack>
    </Layout>
  )
}