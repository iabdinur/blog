import { useState, useEffect, useRef } from 'react'
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
  Icon,
} from '@chakra-ui/react'
import { MdImage } from 'react-icons/md'
import { useCreatePost, useUpdatePost } from '@/api/admin'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { apiClient } from '@/api/client'
import { useTags } from '@/api/tags'
import { useAuthors } from '@/api/admin'
import { AuthorLayout } from './AuthorLayout'
import { Author, Tag } from '@/types'

export const PostForm = () => {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
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
  const contentTextareaRef = useRef<HTMLTextAreaElement>(null)

  const [formData, setFormData] = useState({
    title: '',
    slug: '',
    content: '',
    excerpt: '',
    coverImage: '',
    contentImage: '',
    authorId: '',
    tagIds: [] as string[],
    isPublished: false,
    readingTime: 0,
    scheduledAt: '',
  })

  useEffect(() => {
    if (existingPost) {
      // Format scheduledAt for datetime-local input (YYYY-MM-DDTHH:mm)
      // Backend returns LocalDateTime as ISO string without timezone (e.g., "2026-01-14T21:36:00")
      // Parse it manually to treat it as local time
      let scheduledAtFormatted = ''
      if (existingPost.scheduledAt) {
        const dateStr = existingPost.scheduledAt
        const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/)
        if (match) {
          const [, year, month, day, hour, minute] = match
          // Format as datetime-local value (YYYY-MM-DDTHH:mm)
          scheduledAtFormatted = `${year}-${month}-${day}T${hour}:${minute}`
        }
      }
      
      setFormData({
        title: existingPost.title || '',
        slug: existingPost.slug || '',
        content: existingPost.content || '',
        excerpt: existingPost.excerpt || '',
        coverImage: existingPost.coverImage || '',
        contentImage: existingPost.contentImage || '',
        authorId: existingPost.author?.id || '',
        tagIds: existingPost.tags?.map((t: Tag) => t.id) || [],
        isPublished: existingPost.isPublished || false,
        readingTime: existingPost.readingTime || 0,
        scheduledAt: scheduledAtFormatted,
      })
    }
  }, [existingPost])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      // Validate required fields
      if (!formData.title || !formData.title.trim()) {
        alert('Title is required')
        return
      }
      if (!formData.slug || !formData.slug.trim()) {
        alert('Slug is required')
        return
      }
      if (!formData.content || formData.content.trim().length < 10) {
        alert('Content is required and must be at least 10 characters')
        return
      }
      if (!formData.authorId) {
        alert('Author is required')
        return
      }
      
      // Format scheduledAt for backend
      // Send datetime-local value directly (YYYY-MM-DDTHH:mm) without timezone conversion
      // The backend will parse it as LocalDateTime (no timezone)
      const postData: any = {
        title: formData.title.trim(),
        slug: formData.slug.trim(),
        content: formData.content.trim(),
        excerpt: formData.excerpt?.trim() || null,
        coverImage: formData.coverImage?.trim() || null,
        contentImage: formData.contentImage?.trim() || null,
        authorId: formData.authorId,
        tagIds: formData.tagIds || [],
        isPublished: formData.isPublished,
        readingTime: formData.readingTime || null,
      }
      
      // Only include scheduledAt if it has a value
      // Send as "YYYY-MM-DDTHH:mm" format (datetime-local format) - backend treats it as local time
      if (formData.scheduledAt && formData.scheduledAt.trim() !== '') {
        // Ensure format is YYYY-MM-DDTHH:mm (add :00 seconds if missing)
        let scheduledAtValue = formData.scheduledAt.trim()
        if (scheduledAtValue.length === 16) {
          // Format is YYYY-MM-DDTHH:mm, add seconds
          scheduledAtValue = scheduledAtValue + ':00'
        }
        postData.scheduledAt = scheduledAtValue
      }
      
      console.log('Submitting post data:', JSON.stringify(postData, null, 2))
      
      if (isEdit && slug) {
        await updateMutation.mutateAsync({ slug, post: postData })
      } else {
        await createMutation.mutateAsync(postData)
      }
      
      // Explicitly refetch drafts to ensure data is fresh before navigation
      await queryClient.refetchQueries({ queryKey: ['drafts'] })
      
      // Navigate back to drafts page after saving (for both create and update)
      navigate('/authors/drafts')
    } catch (error: any) {
      console.error('Error submitting post:', error)
      console.error('Error response:', error.response?.data)
      console.error('Error status:', error.response?.status)
      // Error handled by mutation
    }
  }

  const tags = tagsData || []
  const authors = authorsData || []

  return (
    <AuthorLayout>
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
              <VStack spacing={2} align="stretch">
                <HStack justify="flex-end">
                  <Button
                    leftIcon={<Icon as={MdImage} />}
                    onClick={() => {
                      if (contentTextareaRef.current) {
                        const textarea = contentTextareaRef.current
                        const start = textarea.selectionStart
                        const end = textarea.selectionEnd
                        const text = textarea.value
                        const before = text.substring(0, start)
                        const after = text.substring(end)
                        const newContent = `${before}{{content_image}}${after}`
                        setFormData({ ...formData, content: newContent })
                        // Set cursor after placeholder
                        setTimeout(() => {
                          textarea.focus()
                          textarea.setSelectionRange(start + 18, start + 18)
                        }, 0)
                      }
                    }}
                    size="sm"
                    variant="outline"
                  >
                    Insert Content Image Position
                  </Button>
                </HStack>
                <Textarea
                  ref={contentTextareaRef}
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="Post content (Markdown supported)"
                  minH="300px"
                />
              </VStack>
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

            <FormControl>
              <FormLabel>Content Image URL</FormLabel>
              <Input
                value={formData.contentImage}
                onChange={(e) => setFormData({ ...formData, contentImage: e.target.value })}
                placeholder="https://example.com/content-image.jpg"
              />
            </FormControl>

            <FormControl isRequired>
              <FormLabel>Author</FormLabel>
              <Select
                value={formData.authorId}
                onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
                placeholder="Select author"
              >
                {authors.map((author: Author) => (
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
                  {tags
                    .slice()
                    .sort((a: Tag, b: Tag) => a.name.localeCompare(b.name))
                    .map((tag: Tag) => (
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
                onChange={(e) => {
                  const isPublished = e.target.checked
                  setFormData({ 
                    ...formData, 
                    isPublished,
                    // Clear scheduled date if manually publishing
                    scheduledAt: isPublished ? '' : formData.scheduledAt
                  })
                }}
              />
            </FormControl>

            {!formData.isPublished && (
              <FormControl>
                <FormLabel>Schedule Post (Optional)</FormLabel>
                <Input
                  type="datetime-local"
                  value={formData.scheduledAt}
                  onChange={(e) => setFormData({ ...formData, scheduledAt: e.target.value })}
                  placeholder="Select date and time"
                />
                <Box fontSize="sm" color="gray.500" mt={1}>
                  Leave empty to save as draft. Set a date/time to automatically publish the post.
                </Box>
              </FormControl>
            )}

            <HStack spacing={4}>
              <Button
                type="submit"
                colorScheme="blue"
                isLoading={createMutation.isPending || updateMutation.isPending}
                loadingText={isEdit ? 'Updating...' : 'Creating...'}
              >
                {isEdit ? 'Update Post' : 'Create Post'}
              </Button>
              <Button variant="ghost" onClick={() => navigate('/authors/posts')}>
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

