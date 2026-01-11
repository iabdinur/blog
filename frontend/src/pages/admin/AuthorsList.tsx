import { Link } from 'react-router-dom'
import { VStack, Heading, Box, Button, Table, Thead, Tbody, Tr, Th, Td, IconButton, HStack, useColorModeValue, Text } from '@chakra-ui/react'
import { useAuthors, useDeleteAuthor } from '@/api/admin'
import { FaEdit, FaTrash, FaPlus } from 'react-icons/fa'
import { AdminLayout } from './AdminLayout'

export const AuthorsList = () => {
  const { data: authors, isLoading } = useAuthors()
  const deleteMutation = useDeleteAuthor()
  const textColor = useColorModeValue('gray.900', 'gray.100')

  const handleDelete = async (username: string) => {
    if (window.confirm(`Are you sure you want to delete author "${username}"?`)) {
      try {
        await deleteMutation.mutateAsync(username)
      } catch (error) {
        // Error handled by mutation
      }
    }
  }

  return (
    <AdminLayout>
      <VStack spacing={6} align="stretch">
        <HStack justify="space-between">
          <Heading size="lg" color={textColor}>Authors</Heading>
          <Link to="/admin/authors/new">
            <Button colorScheme="blue" leftIcon={<FaPlus />}>New Author</Button>
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
                  <Th>Name</Th>
                  <Th>Username</Th>
                  <Th>Email</Th>
                  <Th>Posts</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {authors?.map((author: any) => (
                  <Tr key={author.id}>
                    <Td>{author.name}</Td>
                    <Td>{author.username}</Td>
                    <Td>{author.email}</Td>
                    <Td>{author.postsCount || 0}</Td>
                    <Td>
                      <HStack spacing={2}>
                        <Link to={`/admin/authors/edit/${author.username}`}>
                          <IconButton aria-label="Edit" icon={<FaEdit />} size="sm" variant="ghost" />
                        </Link>
                        <IconButton
                          aria-label="Delete"
                          icon={<FaTrash />}
                          size="sm"
                          variant="ghost"
                          colorScheme="red"
                          onClick={() => handleDelete(author.username)}
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
    </AdminLayout>
  )
}


