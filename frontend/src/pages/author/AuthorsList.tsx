import { useState, useRef } from 'react'
import { Link } from 'react-router-dom'
import {
  VStack,
  Heading,
  Box,
  Button,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  IconButton,
  HStack,
  useColorModeValue,
  Text,
  Input,
  InputGroup,
  InputLeftElement,
  AlertDialog,
  AlertDialogBody,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogContent,
  AlertDialogOverlay,
  useDisclosure,
  Spinner,
  Center,
} from '@chakra-ui/react'
import { useAuthors, useDeleteAuthor } from '@/api/admin'
import { FaEdit, FaTrash, FaPlus, FaSearch, FaExternalLinkAlt } from 'react-icons/fa'
import { AuthorLayout } from './AuthorLayout'
import { Author } from '@/types'
import { Avatar } from '@/components/ui/Avatar'

export const AuthorsList = () => {
  const { data: authors, isLoading } = useAuthors()
  const deleteMutation = useDeleteAuthor()
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const bg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  
  const [searchQuery, setSearchQuery] = useState('')
  const [authorToDelete, setAuthorToDelete] = useState<{ username: string; name: string } | null>(null)
  const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure()
  const cancelRef = useRef<HTMLButtonElement>(null)

  const handleDeleteClick = (username: string, name: string) => {
    setAuthorToDelete({ username, name })
    onDeleteOpen()
  }

  const handleDeleteConfirm = async () => {
    if (authorToDelete) {
      try {
        await deleteMutation.mutateAsync(authorToDelete.username)
        onDeleteClose()
        setAuthorToDelete(null)
      } catch (error) {
        // Error handled by mutation
      }
    }
  }

  // Filter authors based on search query
  const filteredAuthors = authors?.filter((author: Author) => {
    if (!searchQuery.trim()) return true
    const query = searchQuery.toLowerCase()
    return (
      author.name.toLowerCase().includes(query) ||
      author.username.toLowerCase().includes(query) ||
      author.email.toLowerCase().includes(query)
    )
  }) || []

  return (
    <AuthorLayout>
      <VStack spacing={6} align="stretch">
        <HStack justify="space-between">
          <Heading size="lg" color={textColor}>Authors</Heading>
          <Link to="/authors/authors/new">
            <Button colorScheme="blue" leftIcon={<FaPlus />}>New Author</Button>
          </Link>
        </HStack>

        {/* Search Bar */}
        <Box>
          <InputGroup>
            <InputLeftElement pointerEvents="none">
              <FaSearch color="gray.300" />
            </InputLeftElement>
            <Input
              placeholder="Search by name, username, or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              bg={bg}
              borderColor={borderColor}
            />
          </InputGroup>
        </Box>

        {isLoading ? (
          <Center py={8}>
            <Spinner size="xl" />
          </Center>
        ) : filteredAuthors.length === 0 ? (
          <Box textAlign="center" py={8}>
            <Text color={textColor} fontSize="lg">
              {searchQuery ? 'No authors found matching your search' : 'No authors found'}
            </Text>
          </Box>
        ) : (
          <Box overflowX="auto">
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>Avatar</Th>
                  <Th>Name</Th>
                  <Th>Username</Th>
                  <Th>Email</Th>
                  <Th>Posts</Th>
                  <Th>Followers</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {filteredAuthors.map((author: Author) => (
                  <Tr key={author.id}>
                    <Td>
                      <Avatar
                        size="sm"
                        name={author.name}
                        src={
                          author.email
                            ? `/api/v1/users/${encodeURIComponent(author.email)}/profile-image`
                            : undefined
                        }
                      />
                    </Td>
                    <Td>{author.name}</Td>
                    <Td>{author.username}</Td>
                    <Td>{author.email}</Td>
                    <Td>{author.postsCount || 0}</Td>
                    <Td>{author.followersCount || 0}</Td>
                    <Td>
                      <HStack spacing={2}>
                        <Link to={`/authors/authors/edit/${author.username}`}>
                          <IconButton aria-label="Edit" icon={<FaEdit />} size="sm" variant="ghost" />
                        </Link>
                        <Link to="/author" target="_blank">
                          <IconButton aria-label="View Profile" icon={<FaExternalLinkAlt />} size="sm" variant="ghost" />
                        </Link>
                        <IconButton
                          aria-label="Delete"
                          icon={<FaTrash />}
                          size="sm"
                          variant="ghost"
                          colorScheme="red"
                          onClick={() => handleDeleteClick(author.username, author.name)}
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

      {/* Delete Confirmation Dialog */}
      <AlertDialog
        isOpen={isDeleteOpen}
        leastDestructiveRef={cancelRef}
        onClose={onDeleteClose}
      >
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Delete Author
            </AlertDialogHeader>

            <AlertDialogBody>
              Are you sure you want to delete author "{authorToDelete?.name}" ({authorToDelete?.username})?
              This action cannot be undone.
            </AlertDialogBody>

            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onDeleteClose}>
                Cancel
              </Button>
              <Button
                colorScheme="red"
                onClick={handleDeleteConfirm}
                ml={3}
                isLoading={deleteMutation.isPending}
              >
                Delete
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </AuthorLayout>
  )
}


