import { VStack, IconButton, Flex, Text, Center } from '@chakra-ui/react'
import { useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { usePosts } from '@/api/posts'
import { PostList } from '@/components/blog/PostList'
import { SortTabs, SortValue } from '@/components/blog/SortTabs'
import { Layout } from '@/components/layout/Layout'
import { useUIStore } from '@/store/useUIStore'

export const Blog = () => {
  const [sort, setSort] = useState<SortValue>('latest')
  const { data, isLoading, error } = usePosts({ limit: 100, sort })
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
        <Flex justify="space-between" align="center" w="100%">
          <SortTabs value={sort} onChange={setSort} />
          <IconButton
            aria-label="Search"
            icon={<CiSearch size={20} />}
            variant="ghost"
            onClick={() => setSearchPopupOpen(true)}
            size="sm"
          />
        </Flex>

        <PostList posts={posts} isLoading={isLoading} variant="horizontal" />
      </VStack>
    </Layout>
  )
}
