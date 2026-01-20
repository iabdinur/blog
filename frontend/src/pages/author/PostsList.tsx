import { Link } from 'react-router-dom'
import { VStack, Heading, Box, Button, Table, Thead, Tbody, Tr, Th, Td, IconButton, HStack, useColorModeValue, Text } from '@chakra-ui/react'
import { usePosts } from '@/api/posts'
import { useDeletePost } from '@/api/admin'
import { FaEdit, FaTrash, FaPlus } from 'react-icons/fa'
import { AuthorLayout } from './AuthorLayout'
import { Post } from '@/types'

export const PostsList = () => {
  const { data, isLoading } = usePosts({ limit: 100 })
  const deleteMutation = useDeletePost()
  const textColor = useColorModeValue('gray.900', 'gray.100')

  const posts = data?.posts || []

  const handleDelete = async (slug: string) => {
    if (window.confirm('Are you sure you want to delete this post?')) {
      try {
        await deleteMutation.mutateAsync(slug)
      } catch (error) {
        // Error handled by mutation
      }
    }
  }

  return (
    <AuthorLayout>
      <VStack spacing={6} align="stretch">
        <HStack justify="space-between">
          <Heading size="lg" color={textColor}>Posts</Heading>
          <Link to="/authors/posts/new">
            <Button colorScheme="blue" leftIcon={<FaPlus />}>New Post</Button>
          </Link>
        </HStack>

        {isLoading ? (
          <Box>
            <Text color={textColor}>Loading...</Text>
          </Box>
        ) : (
          <Box overflowX="auto">
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>Title</Th>
                  <Th>Slug</Th>
                  <Th>Published</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {posts.map((post: Post) => (
                  <Tr key={post.id}>
                    <Td>{post.title}</Td>
                    <Td>{post.slug}</Td>
                    <Td>{post.isPublished ? 'Yes' : 'No'}</Td>
                    <Td>
                      <HStack spacing={2}>
                        <Link to={`/authors/posts/edit/${post.slug}`}>
                          <IconButton aria-label="Edit" icon={<FaEdit />} size="sm" variant="ghost" />
                        </Link>
                        <IconButton
                          aria-label="Delete"
                          icon={<FaTrash />}
                          size="sm"
                          variant="ghost"
                          colorScheme="red"
                          onClick={() => handleDelete(post.slug)}
                          isLoading={deleteMutation.isPending}
                        />
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

