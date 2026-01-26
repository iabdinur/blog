import { Link } from 'react-router-dom'
import { VStack, Heading, Box, Button, Table, Thead, Tbody, Tr, Th, Td, IconButton, HStack, useColorModeValue, Text, useToast } from '@chakra-ui/react'
import { useDrafts, usePublishDraft } from '@/api/posts'
import { FaEdit, FaCheck, FaPlus } from 'react-icons/fa'
import { AuthorLayout } from './AuthorLayout'
import { Post } from '@/types'

export const DraftsList = () => {
  const { data, isLoading, error } = useDrafts({ limit: 100 })
  const publishMutation = usePublishDraft()
  const toast = useToast()
  const textColor = useColorModeValue('gray.900', 'gray.100')

  const posts = (data as any)?.posts || []

  // Show error message if API call failed
  if (error) {
    console.error('Failed to load drafts:', error)
  }

  const handlePublish = async (slug: string, title: string) => {
    try {
      await publishMutation.mutateAsync(slug)
      toast({
        title: 'Post published',
        description: `"${title}" has been published successfully`,
        status: 'success',
        duration: 3000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to publish post',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    }
  }

  return (
    <AuthorLayout>
      <VStack spacing={6} align="stretch">
        <HStack justify="space-between">
          <Heading size="lg" color={textColor}>Drafts</Heading>
          <Link to="/authors/posts/new">
            <Button colorScheme="blue" leftIcon={<FaPlus />}>New Post</Button>
          </Link>
        </HStack>

        {isLoading ? (
          <Box>
            <Text color={textColor}>Loading...</Text>
          </Box>
        ) : error ? (
          <Box>
            <Text color="red.500">
              Failed to load drafts. Please make sure you're logged in and have an author profile.
            </Text>
          </Box>
        ) : posts.length === 0 ? (
          <Box>
            <Text color={textColor}>No drafts found. Create a new post to get started!</Text>
          </Box>
        ) : (
          <Box overflowX="auto">
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>Title</Th>
                  <Th>Slug</Th>
                  <Th>Scheduled For</Th>
                  <Th>Last Updated</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {posts.map((post: Post) => (
                  <Tr key={post.id}>
                    <Td>{post.title}</Td>
                    <Td>{post.slug}</Td>
                    <Td>
                      {post.scheduledAt ? (
                        <Text fontSize="sm" color="blue.500">
                          {(() => {
                            // Backend returns LocalDateTime as ISO string without timezone (e.g., "2026-01-14T21:36:00")
                            // Parse it manually to ensure it's treated as local time (not UTC)
                            const dateStr = post.scheduledAt
                            const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/)
                            if (match) {
                              const [, year, month, day, hour, minute, second] = match
                              // Create date in local timezone (not UTC)
                              const date = new Date(
                                parseInt(year),
                                parseInt(month) - 1, // Month is 0-indexed
                                parseInt(day),
                                parseInt(hour),
                                parseInt(minute),
                                parseInt(second || '0')
                              )
                              return date.toLocaleString(undefined, {
                                year: 'numeric',
                                month: '2-digit',
                                day: '2-digit',
                                hour: '2-digit',
                                minute: '2-digit',
                                second: '2-digit',
                                hour12: false
                              })
                            }
                            // Fallback to default parsing
                            return new Date(dateStr).toLocaleString()
                          })()}
                        </Text>
                      ) : (
                        <Text fontSize="sm" color="gray.500">Not scheduled</Text>
                      )}
                    </Td>
                    <Td>{post.updatedAt ? new Date(post.updatedAt).toLocaleDateString() : '-'}</Td>
                    <Td>
                      <HStack spacing={2}>
                        <Link to={`/authors/posts/edit/${post.slug}`}>
                          <IconButton aria-label="Edit" icon={<FaEdit />} size="sm" variant="ghost" />
                        </Link>
                        {!post.scheduledAt && (
                          <IconButton
                            aria-label="Publish"
                            icon={<FaCheck />}
                            size="sm"
                            variant="ghost"
                            colorScheme="green"
                            onClick={() => handlePublish(post.slug, post.title)}
                            isLoading={publishMutation.isPending}
                          />
                        )}
                      </HStack>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </Box>
        )}
      </VStack>
    </AuthorLayout>
  )
}
