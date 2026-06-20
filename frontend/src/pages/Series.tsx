import { VStack, Heading, Text, Spinner, Center, IconButton, Flex, Box } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'
import { useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { usePosts } from '@/api/posts'
import { useTag } from '@/api/tags'
import { PostList } from '@/components/blog/PostList'
import { SortTabs, SortValue } from '@/components/blog/SortTabs'
import { Layout } from '@/components/layout/Layout'
import { useUIStore } from '@/store/useUIStore'

export const Series = () => {
  const { slug } = useParams<{ slug: string }>()
  const [sort, setSort] = useState<SortValue>('latest')
  const { data: tag, isLoading: tagLoading } = useTag(slug || '')
  const { data: postsData, isLoading: postsLoading } = usePosts({ tag: slug, limit: 1000, sort })
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

        {/* Sort tabs with Search */}
        <Flex justify="space-between" align="center" w="100%">
          <SortTabs value={sort} onChange={setSort} />

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