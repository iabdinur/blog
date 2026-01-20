import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  VStack,
  Heading,
  Box,
  Input,
  Textarea,
  Button,
  FormControl,
  FormLabel,
  HStack,
  Alert,
  AlertIcon,
  Center,
  Icon,
  Image,
  useToast,
  useColorModeValue,
  Text,
} from '@chakra-ui/react'
import { useCreateAuthor, useUpdateAuthor } from '@/api/admin'
import { useQuery } from '@tanstack/react-query'
import { apiClient, uploadUserProfileImage } from '@/api/client'
import { AuthorLayout } from './AuthorLayout'
import { FaCamera } from 'react-icons/fa'
import { Avatar } from '@/components/ui/Avatar'

export const AuthorForm = () => {
  const { username } = useParams<{ username: string }>()
  const navigate = useNavigate()
  const toast = useToast()
  const isEdit = !!username
  const bg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')

  const { data: existingAuthor } = useQuery({
    queryKey: ['author', username],
    queryFn: () => apiClient.get(`/authors/${username}`).then((res) => res.data),
    enabled: isEdit && !!username,
  })

  const createMutation = useCreateAuthor()
  const updateMutation = useUpdateAuthor()

  const [formData, setFormData] = useState({
    name: '',
    username: '',
    email: '',
    bio: '',
    avatar: '',
    coverImage: '',
    location: '',
    website: '',
    github: '',
    linkedin: '',
  })

  const [avatarFile, setAvatarFile] = useState<File | null>(null)
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const [coverImagePreview, setCoverImagePreview] = useState<string | null>(null)
  const [isUploading, setIsUploading] = useState(false)

  useEffect(() => {
    if (existingAuthor) {
      setFormData({
        name: existingAuthor.name || '',
        username: existingAuthor.username || '',
        email: existingAuthor.email || '',
        bio: existingAuthor.bio || '',
        avatar: existingAuthor.avatar || '',
        coverImage: existingAuthor.coverImage || '',
        location: existingAuthor.location || '',
        website: existingAuthor.website || '',
        github: existingAuthor.github || '',
        linkedin: existingAuthor.linkedin || '',
      })
      // Set previews for existing images
      if (existingAuthor.avatar) {
        setAvatarPreview(existingAuthor.avatar)
      }
      if (existingAuthor.coverImage) {
        setCoverImagePreview(existingAuthor.coverImage)
      }
    }
  }, [existingAuthor])

  const handleAvatarFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setAvatarFile(file)
      const reader = new FileReader()
      reader.onloadend = () => {
        setAvatarPreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleCoverImageFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const reader = new FileReader()
      reader.onloadend = () => {
        setCoverImagePreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsUploading(true)
    
    try {
      let finalFormData = { ...formData }

      // Upload avatar if a new file was selected
      if (avatarFile && formData.email) {
        try {
          await uploadUserProfileImage(formData.email, avatarFile)
          // Set avatar URL to point to user profile image
          finalFormData.avatar = `/api/v1/users/${encodeURIComponent(formData.email)}/profile-image`
          toast({
            title: 'Avatar uploaded',
            status: 'success',
            duration: 2000,
            isClosable: true,
          })
        } catch (error: any) {
          toast({
            title: 'Failed to upload avatar',
            description: error.response?.data?.message || 'Please try again',
            status: 'error',
            duration: 3000,
            isClosable: true,
          })
          setIsUploading(false)
          return
        }
      }

      // For cover image, we'll keep it as URL for now
      // TODO: Add cover image upload endpoint if needed

      if (isEdit && username) {
        await updateMutation.mutateAsync({ username, author: finalFormData })
        toast({
          title: 'Author updated',
          status: 'success',
          duration: 2000,
          isClosable: true,
        })
      } else {
        await createMutation.mutateAsync(finalFormData)
        toast({
          title: 'Author created',
          status: 'success',
          duration: 2000,
          isClosable: true,
        })
      }
      navigate('/authors/authors')
    } catch (error: any) {
      toast({
        title: `Failed to ${isEdit ? 'update' : 'create'} author`,
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
        isClosable: true,
      })
    } finally {
      setIsUploading(false)
    }
  }

  return (
    <AuthorLayout>
      <Box maxW="800px" mx="auto">
        <VStack spacing={6} align="stretch">
        <Heading size="lg">{isEdit ? 'Edit Author' : 'Create New Author'}</Heading>

        {(createMutation.isError || updateMutation.isError) && (
          <Alert status="error">
            <AlertIcon />
            Failed to {isEdit ? 'update' : 'create'} author
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <VStack spacing={4} align="stretch">
            <FormControl isRequired>
              <FormLabel>Name</FormLabel>
              <Input
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Author name"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Username</FormLabel>
              <Input
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                placeholder="username"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Email</FormLabel>
              <Input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="author@example.com"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Bio</FormLabel>
              <Textarea
                value={formData.bio}
                onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                placeholder="Author bio"
                rows={3}
              />
              <Text fontSize="xs" color="gray.500" mt={1}>
                {formData.bio.length} characters
              </Text>
            </FormControl>

            <FormControl>
              <FormLabel>Avatar</FormLabel>
              <VStack spacing={4} align="stretch">
                <Center>
                  <Box position="relative" cursor="pointer">
                    <Input
                      type="file"
                      accept="image/*"
                      onChange={handleAvatarFileChange}
                      display="none"
                      id="avatar-upload"
                    />
                    <FormLabel htmlFor="avatar-upload" m={0} cursor="pointer">
                      <Avatar
                        size="xl"
                        src={avatarPreview || formData.avatar || undefined}
                        name={formData.name || 'Author'}
                        cursor="pointer"
                        border="2px solid"
                        borderColor={borderColor}
                        _hover={{ opacity: 0.8, borderColor: 'brand.500' }}
                        transition="all 0.2s"
                      />
                      <Box
                        position="absolute"
                        bottom={0}
                        right={0}
                        bg="brand.500"
                        color="white"
                        borderRadius="full"
                        p={2}
                        border="2px solid"
                        borderColor={bg}
                        _hover={{ bg: 'brand.600' }}
                        transition="all 0.2s"
                      >
                        <Icon as={FaCamera} boxSize={3} />
                      </Box>
                    </FormLabel>
                  </Box>
                </Center>
                <Input
                  value={formData.avatar}
                  onChange={(e) => {
                    setFormData({ ...formData, avatar: e.target.value })
                    setAvatarPreview(e.target.value || null)
                  }}
                  placeholder="Or enter avatar URL"
                  size="sm"
                />
              </VStack>
            </FormControl>

            <FormControl>
              <FormLabel>Cover Image</FormLabel>
              <VStack spacing={4} align="stretch">
                {coverImagePreview && (
                  <Box>
                    <Image
                      src={coverImagePreview}
                      alt="Cover preview"
                      maxH="200px"
                      objectFit="cover"
                      borderRadius="md"
                      border="1px solid"
                      borderColor={borderColor}
                    />
                  </Box>
                )}
                <Input
                  type="file"
                  accept="image/*"
                  onChange={handleCoverImageFileChange}
                  display="none"
                  id="cover-image-upload"
                />
                <HStack>
                  <Button
                    as="label"
                    htmlFor="cover-image-upload"
                    size="sm"
                    variant="outline"
                    cursor="pointer"
                  >
                    Upload Cover Image
                  </Button>
                  {coverImagePreview && (
                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={() => {
                        setCoverImagePreview(null)
                      }}
                    >
                      Clear
                    </Button>
                  )}
                </HStack>
                <Input
                  value={formData.coverImage}
                  onChange={(e) => {
                    setFormData({ ...formData, coverImage: e.target.value })
                    setCoverImagePreview(e.target.value || null)
                  }}
                  placeholder="Or enter cover image URL"
                  size="sm"
                />
              </VStack>
            </FormControl>

            <FormControl>
              <FormLabel>Location</FormLabel>
              <Input
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                placeholder="City, Country"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Website</FormLabel>
              <Input
                value={formData.website}
                onChange={(e) => setFormData({ ...formData, website: e.target.value })}
                placeholder="https://example.com"
              />
            </FormControl>

            <FormControl>
              <FormLabel>GitHub</FormLabel>
              <Input
                value={formData.github}
                onChange={(e) => setFormData({ ...formData, github: e.target.value })}
                placeholder="github_username"
              />
            </FormControl>

            <FormControl>
              <FormLabel>LinkedIn</FormLabel>
              <Input
                value={formData.linkedin}
                onChange={(e) => setFormData({ ...formData, linkedin: e.target.value })}
                placeholder="https://linkedin.com/in/username"
              />
            </FormControl>

            <HStack spacing={4}>
              <Button
                type="submit"
                colorScheme="blue"
                isLoading={createMutation.isPending || updateMutation.isPending || isUploading}
                loadingText={isEdit ? 'Updating...' : 'Creating...'}
              >
                {isEdit ? 'Update Author' : 'Create Author'}
              </Button>
              <Button variant="ghost" onClick={() => navigate('/authors/authors')}>
                Cancel
              </Button>
            </HStack>
          </VStack>
        </form>
      </VStack>
      </Box>
    </AuthorLayout>
  )
}

