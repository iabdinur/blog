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
import { useCreateTag, useUpdateTag } from '@/api/admin'
import { useTag } from '@/api/tags'
import { AuthorLayout } from './AuthorLayout'

export const TagForm = () => {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const isEdit = !!slug

  const { data: existingTag } = useTag(slug || '')
  const createMutation = useCreateTag()
  const updateMutation = useUpdateTag()

  const [formData, setFormData] = useState({
    name: '',
    slug: '',
    description: '',
  })

  useEffect(() => {
    if (existingTag) {
      setFormData({
        name: existingTag.name || '',
        slug: existingTag.slug || '',
        description: existingTag.description || '',
      })
    }
  }, [existingTag])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (isEdit && slug) {
        await updateMutation.mutateAsync({ slug, tag: formData })
      } else {
        await createMutation.mutateAsync(formData)
      }
      navigate('/authors/tags')
    } catch (error) {
      // Error handled by mutation
    }
  }

  return (
    <AuthorLayout>
      <Box maxW="800px" mx="auto">
        <VStack spacing={6} align="stretch">
        <Heading size="lg">{isEdit ? 'Edit Tag' : 'Create New Tag'}</Heading>

        {(createMutation.isError || updateMutation.isError) && (
          <Alert status="error">
            <AlertIcon />
            Failed to {isEdit ? 'update' : 'create'} tag
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <VStack spacing={4} align="stretch">
            <FormControl isRequired>
              <FormLabel>Name</FormLabel>
              <Input
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="Tag name"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Slug</FormLabel>
              <Input
                value={formData.slug}
                onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                placeholder="tag-slug"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Description</FormLabel>
              <Textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Tag description"
                rows={3}
              />
            </FormControl>

            <HStack spacing={4}>
              <Button
                type="submit"
                colorScheme="blue"
                isLoading={createMutation.isPending || updateMutation.isPending}
                loadingText={isEdit ? 'Updating...' : 'Creating...'}
              >
                {isEdit ? 'Update Tag' : 'Create Tag'}
              </Button>
              <Button variant="ghost" onClick={() => navigate('/authors/tags')}>
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

