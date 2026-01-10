import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalBody,
  useDisclosure,
  Box,
  Input,
  InputGroup,
  InputLeftElement,
  InputRightElement,
  IconButton,
  VStack,
  Text,
  Spinner,
  Link,
  Heading,
  HStack,
  Badge,
} from '@chakra-ui/react'
import { useEffect, useState } from 'react'
import { useNavigate, Link as RouterLink, useSearchParams } from 'react-router-dom'
import { FaTimes } from 'react-icons/fa'
import { CiSearch } from 'react-icons/ci'
import { useUIStore } from '@/store/useUIStore'
import { useSearch } from '@/api/search'

// Helper function to get short name for series
const getShortSeriesName = (fullName: string): string => {
  const shortNames: Record<string, string> = {
    'Artificial Intelligence': 'AI',
    'SWE Interview Preparation': 'Interview Prep',
    'UIUC Master of Computer Science': 'UIUC MCS',
  }
  return shortNames[fullName] || fullName
}

export const SearchPopup = () => {
  const { searchPopupOpen, setSearchPopupOpen } = useUIStore()
  const { isOpen, onOpen, onClose } = useDisclosure()
  const [searchQuery, setSearchQuery] = useState('')
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { data: searchResults, isLoading } = useSearch(searchQuery, { limit: 10 })

  useEffect(() => {
    if (searchPopupOpen) {
      onOpen()
      // Pre-fill search query from URL if present (only when popup first opens)
      const queryParam = searchParams.get('q')
      if (queryParam) {
        setSearchQuery(queryParam)
      }
    } else {
      onClose()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchPopupOpen, onOpen, onClose])

  const handleClose = () => {
    setSearchPopupOpen(false)
    onClose()
    setSearchQuery('')
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    // Don't navigate - just keep the popup open with the search results
    // The results are already being displayed as the user types
  }

  const handlePostClick = () => {
    handleClose()
  }

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault()
        setSearchPopupOpen(!searchPopupOpen)
      }
      if (e.key === 'Escape' && isOpen) {
        setSearchPopupOpen(false)
        onClose()
        setSearchQuery('')
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, searchPopupOpen, setSearchPopupOpen, onClose])

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="xl" isCentered>
      <ModalOverlay />
      <ModalContent maxH="80vh" h="600px" overflow="hidden">
        <ModalBody p={0}>
          <Box pt={2} pb={0}>
            <Box px={6}>
              <form onSubmit={handleSubmit}>
                <InputGroup size="md">
                  <InputLeftElement 
                    pointerEvents="none" 
                    h="calc(2.5rem + 0.25rem)" 
                    left="0"
                    alignItems="center"
                    justifyContent="flex-start"
                    lineHeight="1.5"
                  >
                    <CiSearch color="gray.400" style={{ fontSize: '1.25rem', lineHeight: '1.5' }} />
                  </InputLeftElement>
                  <Input
                    placeholder="Search posts"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    autoFocus
                    border="none"
                    borderRadius={0}
                    pl="2rem"
                    pr="140px"
                    fontSize="md"
                    h="calc(2.5rem + 0.25rem)"
                    py="0.125rem"
                    lineHeight="1.5"
                    _focus={{
                      border: 'none',
                      boxShadow: 'none',
                    }}
                  />
                  <InputRightElement 
                    h="calc(2.5rem + 0.25rem)" 
                    py="0.125rem" 
                    right="0" 
                    width="auto"
                    alignItems="center"
                  >
                    <HStack spacing={2}>
                      <Badge
                        fontSize="xs"
                        px={2}
                        py={1}
                        borderRadius="md"
                        bg="gray.100"
                        color="gray.600"
                        _dark={{ bg: 'gray.700', color: 'gray.300' }}
                        fontWeight="normal"
                      >
                        Cmd + K
                      </Badge>
                      <IconButton
                        aria-label="Close search"
                        icon={<FaTimes />}
                        variant="ghost"
                        size="sm"
                        onClick={handleClose}
                        borderRadius="full"
                      />
                    </HStack>
                  </InputRightElement>
                </InputGroup>
              </form>
            </Box>
            <Box
              borderBottom="1px solid"
              borderBottomColor="gray.200"
              _dark={{ borderBottomColor: 'gray.700' }}
              mt={2}
            />
            {searchQuery && searchResults && (
              <Box
                px={6}
                py={2}
                bg="gray.50"
                _dark={{ bg: 'gray.800' }}
              >
                <Text fontSize="xs" fontWeight="medium" color="gray.600" _dark={{ color: 'gray.400' }} textTransform="uppercase">
                  {searchResults.total || 0} {searchResults.total === 1 ? 'RESULT' : 'RESULTS'}
                </Text>
              </Box>
            )}
          </Box>

          {searchQuery && (
            <Box
              maxH="calc(80vh - 120px)"
              overflowY="auto"
              px={6}
              pb={6}
              pt={0.5}
            >
              {isLoading ? (
                <Box py={4}>
                  <Spinner />
                </Box>
              ) : searchResults && (searchResults.posts.length > 0 || searchResults.authors.length > 0 || searchResults.tags.length > 0) ? (
                <VStack align="stretch" spacing={4} py={1} alignItems="flex-start">
                  {searchResults.authors.length > 0 && (
                    <Box>
                      <Heading size="sm" mb={3}>Authors ({searchResults.authors.length})</Heading>
                      <VStack align="stretch" spacing={2}>
                        {searchResults.authors.slice(0, 3).map((author) => (
                          <Link
                            key={author.id}
                            as={RouterLink}
                            to={`/author/${author.username}`}
                            onClick={handlePostClick}
                            p={2}
                            borderRadius="md"
                            _hover={{ bg: 'gray.100', _dark: { bg: 'gray.800' } }}
                          >
                            <Text fontWeight="medium">{author.name}</Text>
                            {author.bio && (
                              <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }} noOfLines={1}>
                                {author.bio}
                              </Text>
                            )}
                          </Link>
                        ))}
                      </VStack>
                    </Box>
                  )}

                  {searchResults.posts.length > 0 && (
                    <Box>
                      <Heading size="sm" mb={3}>Posts ({searchResults.posts.length})</Heading>
                      <VStack align="stretch" spacing={2}>
                        {searchResults.posts.slice(0, 5).map((post) => (
                          <Link
                            key={post.id}
                            as={RouterLink}
                            to={`/post/${post.slug}`}
                            onClick={handlePostClick}
                            p={2}
                            borderRadius="md"
                            _hover={{ bg: 'gray.100', _dark: { bg: 'gray.800' } }}
                          >
                            <Text fontWeight="medium">{post.title}</Text>
                            <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }} noOfLines={1}>
                              {post.excerpt}
                            </Text>
                          </Link>
                        ))}
                      </VStack>
                    </Box>
                  )}

                  {searchResults.tags.length > 0 && (
                    <Box>
                      <Heading size="sm" mb={3}>Tags ({searchResults.tags.length})</Heading>
                      <VStack align="stretch" spacing={2}>
                        {searchResults.tags.slice(0, 3).map((tag) => (
                          <Link
                            key={tag.id}
                            as={RouterLink}
                            to={`/series/${tag.slug}`}
                            onClick={handlePostClick}
                            p={2}
                            borderRadius="md"
                            _hover={{ bg: 'gray.100', _dark: { bg: 'gray.800' } }}
                          >
                            <Text fontWeight="medium">#{getShortSeriesName(tag.name)}</Text>
                            {tag.description && (
                              <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }} noOfLines={1}>
                                {tag.description}
                              </Text>
                            )}
                          </Link>
                        ))}
                      </VStack>
                    </Box>
                  )}

                </VStack>
              ) : searchQuery ? (
                <Box py={2} pt={4}>
                  <Text color="gray.500" _dark={{ color: 'gray.400' }} textAlign="left">No results found</Text>
                </Box>
              ) : null}
            </Box>
          )}
        </ModalBody>
      </ModalContent>
    </Modal>
  )
}

