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
} from '@chakra-ui/react'
import { useCreateAuthor, useUpdateAuthor } from '@/api/admin'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/api/client'
import { AdminLayout } from './AdminLayout'

export const AuthorForm = () => {
  const { username } = useParams<{ username: string }>()
  const navigate = useNavigate()
  const isEdit = !!username

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
    twitter: '',
    github: '',
    linkedin: '',
  })

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
        twitter: existingAuthor.twitter || '',
        github: existingAuthor.github || '',
        linkedin: existingAuthor.linkedin || '',
      })
    }
  }, [existingAuthor])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (isEdit && username) {
        await updateMutation.mutateAsync({ username, author: formData })
      } else {
        await createMutation.mutateAsync(formData)
      }
      navigate('/admin/authors')
    } catch (error) {
      // Error handled by mutation
    }
  }

  return (
    <AdminLayout>
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
            </FormControl>

            <FormControl>
              <FormLabel>Avatar URL</FormLabel>
              <Input
                value={formData.avatar}
                onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
                placeholder="https://example.com/avatar.jpg"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Cover Image URL</FormLabel>
              <Input
                value={formData.coverImage}
                onChange={(e) => setFormData({ ...formData, coverImage: e.target.value })}
                placeholder="https://example.com/cover.jpg"
              />
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
              <FormLabel>Twitter</FormLabel>
              <Input
                value={formData.twitter}
                onChange={(e) => setFormData({ ...formData, twitter: e.target.value })}
                placeholder="twitter_handle"
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
                isLoading={createMutation.isPending || updateMutation.isPending}
                loadingText={isEdit ? 'Updating...' : 'Creating...'}
              >
                {isEdit ? 'Update Author' : 'Create Author'}
              </Button>
              <Button variant="ghost" onClick={() => navigate('/admin/authors')}>
                Cancel
              </Button>
            </HStack>
          </VStack>
        </form>
      </VStack>
      </Box>
    </AdminLayout>
  )
}

