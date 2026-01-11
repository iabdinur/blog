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
  Switch,
  Select,
  Checkbox,
  CheckboxGroup,
  HStack,
  Alert,
  AlertIcon,
} from '@chakra-ui/react'
import { useCreatePost, useUpdatePost } from '@/api/admin'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/api/client'
import { useTags } from '@/api/tags'
import { useAuthors } from '@/api/admin'
import { AdminLayout } from './AdminLayout'

export const PostForm = () => {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const isEdit = !!slug

  const { data: existingPost } = useQuery({
    queryKey: ['post', 'admin', slug],
    queryFn: () => apiClient.get(`/posts/${slug}/admin`).then((res) => res.data),
    enabled: isEdit && !!slug,
  })
  const { data: tagsData } = useTags()
  const { data: authorsData } = useAuthors()

  const createMutation = useCreatePost()
  const updateMutation = useUpdatePost()

  const [formData, setFormData] = useState({
    title: '',
    slug: '',
    content: '',
    excerpt: '',
    coverImage: '',
    authorId: '',
    tagIds: [] as string[],
    isPublished: false,
    readingTime: 0,
  })

  useEffect(() => {
    if (existingPost) {
      setFormData({
        title: existingPost.title || '',
        slug: existingPost.slug || '',
        content: existingPost.content || '',
        excerpt: existingPost.excerpt || '',
        coverImage: existingPost.coverImage || '',
        authorId: existingPost.author?.id || '',
        tagIds: existingPost.tags?.map((t) => t.id) || [],
        isPublished: existingPost.isPublished || false,
        readingTime: existingPost.readingTime || 0,
      })
    }
  }, [existingPost])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (isEdit && slug) {
        await updateMutation.mutateAsync({ slug, post: formData })
      } else {
        await createMutation.mutateAsync(formData)
      }
      navigate('/admin/posts')
    } catch (error) {
      // Error handled by mutation
    }
  }

  const tags = tagsData || []
  const authors = authorsData || []

  return (
    <AdminLayout>
      <Box maxW="800px" mx="auto">
        <VStack spacing={6} align="stretch">
        <Heading size="lg">{isEdit ? 'Edit Post' : 'Create New Post'}</Heading>

        {(createMutation.isError || updateMutation.isError) && (
          <Alert status="error">
            <AlertIcon />
            Failed to {isEdit ? 'update' : 'create'} post
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <VStack spacing={4} align="stretch">
            <FormControl isRequired>
              <FormLabel>Title</FormLabel>
              <Input
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                placeholder="Post title"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Slug</FormLabel>
              <Input
                value={formData.slug}
                onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                placeholder="post-slug"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Content</FormLabel>
              <Textarea
                value={formData.content}
                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                placeholder="Post content (Markdown supported)"
                minH="300px"
              />
            </FormControl>

            <FormControl>
              <FormLabel>Excerpt</FormLabel>
              <Textarea
                value={formData.excerpt}
                onChange={(e) => setFormData({ ...formData, excerpt: e.target.value })}
                placeholder="Short excerpt"
                rows={3}
              />
            </FormControl>

            <FormControl>
              <FormLabel>Cover Image URL</FormLabel>
              <Input
                value={formData.coverImage}
                onChange={(e) => setFormData({ ...formData, coverImage: e.target.value })}
                placeholder="https://example.com/image.jpg"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Author</FormLabel>
              <Select
                value={formData.authorId}
                onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
                placeholder="Select author"
              >
                {authors.map((author: any) => (
                  <option key={author.id} value={author.id}>
                    {author.name} ({author.username})
                  </option>
                ))}
              </Select>
            </FormControl>

            <FormControl>
              <FormLabel>Tags</FormLabel>
              <CheckboxGroup
                value={formData.tagIds}
                onChange={(values) => setFormData({ ...formData, tagIds: values as string[] })}
              >
                <VStack align="start" spacing={2}>
                  {tags.map((tag: any) => (
                    <Checkbox key={tag.id} value={tag.id}>
                      {tag.name}
                    </Checkbox>
                  ))}
                </VStack>
              </CheckboxGroup>
            </FormControl>

            <FormControl>
              <FormLabel>Reading Time (minutes)</FormLabel>
              <Input
                type="number"
                value={formData.readingTime}
                onChange={(e) => setFormData({ ...formData, readingTime: parseInt(e.target.value) || 0 })}
                placeholder="5"
              />
            </FormControl>

            <FormControl display="flex" alignItems="center">
              <FormLabel mb={0}>Published</FormLabel>
              <Switch
                isChecked={formData.isPublished}
                onChange={(e) => setFormData({ ...formData, isPublished: e.target.checked })}
              />
            </FormControl>

            <HStack spacing={4}>
              <Button
                type="submit"
                colorScheme="blue"
                isLoading={createMutation.isPending || updateMutation.isPending}
                loadingText={isEdit ? 'Updating...' : 'Creating...'}
              >
                {isEdit ? 'Update Post' : 'Create Post'}
              </Button>
              <Button variant="ghost" onClick={() => navigate('/admin/posts')}>
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

