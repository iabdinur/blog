import { VStack, Heading, Text, Box, Image, HStack, IconButton, Flex, Link, Center, Icon } from '@chakra-ui/react'
import { useState } from 'react'
import { Link as RouterLink } from 'react-router-dom'
import { CiSearch } from 'react-icons/ci'
import { FaClock } from 'react-icons/fa'
import { usePosts } from '@/api/posts'
import { PostList } from '@/components/blog/PostList'
import { SortTabs, SortValue } from '@/components/blog/SortTabs'
import { Layout } from '@/components/layout/Layout'
import { Card } from '@/components/ui/Card'
import { formatShortDate } from '@/utils/date'
import { useUIStore } from '@/store/useUIStore'
import { getFirstSentence } from '@/utils/text'

export const Home = () => {
  const [sort, setSort] = useState<SortValue>('latest')
  const { data, isLoading, error } = usePosts({ limit: 50, sort })
  const { data: featuredData } = usePosts({ limit: 50, sort: 'latest' })
  const { setSearchPopupOpen } = useUIStore()

  const posts = data?.posts || []
  const featuredPost =
    (featuredData?.posts || []).find((p) => p.featured) || (featuredData?.posts || [])[0]
  const listPosts = posts.filter((p) => p.slug !== featuredPost?.slug).slice(0, 6)

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
                    h={{ base: '250px', md: '400px' }}
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

        {/* Sort tabs with Search */}
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

        <Box>
          <PostList posts={listPosts} isLoading={isLoading} />
        </Box>
      </VStack>
    </Layout>
  )
}
