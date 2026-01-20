import { Link } from 'react-router-dom'
import { VStack, Heading, Box, Button, Table, Thead, Tbody, Tr, Th, Td, IconButton, HStack, useColorModeValue, Text } from '@chakra-ui/react'
import { useTags } from '@/api/tags'
import { useDeleteTag } from '@/api/admin'
import { FaEdit, FaTrash, FaPlus } from 'react-icons/fa'
import { AuthorLayout } from './AuthorLayout'
import { Tag } from '@/types'

export const TagsList = () => {
  const { data: tags, isLoading } = useTags()
  const deleteMutation = useDeleteTag()
  const textColor = useColorModeValue('gray.900', 'gray.100')

  const handleDelete = async (slug: string) => {
    if (window.confirm(`Are you sure you want to delete tag "${slug}"?`)) {
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
          <Heading size="lg" color={textColor}>Tags (Series)</Heading>
          <Link to="/authors/tags/new">
            <Button colorScheme="blue" leftIcon={<FaPlus />}>New Tag</Button>
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
                  <Th>Slug</Th>
                  <Th>Description</Th>
                  <Th>Posts</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {tags
                  ?.slice()
                  .sort((a: Tag, b: Tag) => a.name.localeCompare(b.name))
                  .map((tag: Tag) => (
                    <Tr key={tag.id}>
                      <Td>{tag.name}</Td>
                      <Td>{tag.slug}</Td>
                      <Td>{tag.description || '-'}</Td>
                      <Td>{tag.postsCount || 0}</Td>
                      <Td>
                        <HStack spacing={2}>
                          <Link to={`/authors/tags/edit/${tag.slug}`}>
                            <IconButton aria-label="Edit" icon={<FaEdit />} size="sm" variant="ghost" />
                          </Link>
                          <IconButton
                            aria-label="Delete"
                            icon={<FaTrash />}
                            size="sm"
                            variant="ghost"
                            colorScheme="red"
                            onClick={() => handleDelete(tag.slug)}
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


