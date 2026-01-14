import { Box, VStack, Heading, Textarea, Button, HStack, Text, useToast } from '@chakra-ui/react'
import { useState, useEffect } from 'react'
import { Comment } from '@/types'
import { useComments, useCreateComment } from '@/api/comments'
import { PostAuthor } from './PostAuthor'
import { formatRelativeTime } from '@/utils/date'
import { LoginRegisterModal } from '@/components/ui/LoginRegisterModal'

export interface PostCommentsProps {
  slug: string
}

const CommentItem = ({ comment }: { comment: Comment }) => {
  return (
    <Box borderLeft="2px" borderColor="gray.200" _dark={{ borderColor: 'gray.700' }} pl={4} py={2}>
      <HStack justify="space-between" mb={2}>
        <PostAuthor author={comment.author} size="sm" />
        <Text fontSize="xs" color="gray.500" _dark={{ color: 'gray.400' }}>
          {formatRelativeTime(comment.createdAt)}
        </Text>
      </HStack>
      <Text fontSize="sm" mb={2}>
        {comment.content}
      </Text>
      {comment.replies && comment.replies.length > 0 && (
        <VStack align="stretch" pl={4} mt={2}>
          {comment.replies.map((reply) => (
            <CommentItem key={reply.id} comment={reply} />
          ))}
        </VStack>
      )}
    </Box>
  )
}

export const PostComments = ({ slug }: PostCommentsProps) => {
  const { data: comments, isLoading } = useComments(slug)
  const createComment = useCreateComment()
  const [content, setContent] = useState('')
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [pendingComment, setPendingComment] = useState<string | null>(null)
  const toast = useToast()

  useEffect(() => {
    // Check if user is authenticated (has JWT token)
    const checkAuth = () => {
      const token = localStorage.getItem('auth_token')
      setIsAuthenticated(!!token)
    }
    checkAuth()
    // Listen for storage changes (e.g., when user logs in from another tab)
    window.addEventListener('storage', checkAuth)
    return () => window.removeEventListener('storage', checkAuth)
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!content.trim()) return

    // Check if user is authenticated
    const token = localStorage.getItem('auth_token')
    if (!token) {
      // Store the comment and show login modal
      setPendingComment(content)
      setShowLoginModal(true)
      return
    }

    // User is authenticated, post the comment
    await postComment(content)
  }

  const postComment = async (commentContent: string) => {
    try {
      // Backend extracts authorId from JWT token automatically
      await createComment.mutateAsync({ slug, content: commentContent })
      setContent('')
      setPendingComment(null)
      toast({
        title: 'Comment posted',
        status: 'success',
        duration: 3000,
      })
    } catch (error: any) {
      const errorMessage = error?.response?.data || error?.message || 'Failed to post comment'
      toast({
        title: error?.response?.status === 401 ? 'Authentication required' : 'Failed to post comment',
        description: typeof errorMessage === 'string' ? errorMessage : 'Please login to post comments',
        status: 'error',
        duration: 3000,
      })
    }
  }

  const handleAuthSuccess = () => {
    setIsAuthenticated(true)
    // If there's a pending comment, post it now
    if (pendingComment) {
      postComment(pendingComment)
    }
  }

  const topLevelComments = comments?.filter((c) => !c.parentId) || []

  return (
    <Box mt={8} id="comments-section">
      <Heading size="md" mb={4}>
        Comments ({comments?.length || 0})
      </Heading>

      <Box mb={6}>
        <form onSubmit={handleSubmit}>
          <VStack align="stretch" spacing={3}>
            <Textarea
              placeholder="Write a comment..."
              value={content}
              onChange={(e) => setContent(e.target.value)}
              rows={4}
            />
            <HStack justify="flex-end">
              <Button 
                type="submit" 
                isLoading={createComment.isPending} 
                colorScheme="brand"
              >
                Post
              </Button>
            </HStack>
          </VStack>
        </form>
      </Box>

      <LoginRegisterModal
        isOpen={showLoginModal}
        onClose={() => {
          setShowLoginModal(false)
          setPendingComment(null)
        }}
        onSuccess={handleAuthSuccess}
      />

      {isLoading ? (
        <Text>Loading comments...</Text>
      ) : (
        <VStack align="stretch" spacing={4}>
          {topLevelComments.length === 0 ? (
            <Text color="gray.500" _dark={{ color: 'gray.400' }}>No comments yet. Be the first to comment!</Text>
          ) : (
            topLevelComments.map((comment) => (
              <CommentItem key={comment.id} comment={comment} />
            ))
          )}
        </VStack>
      )}
    </Box>
  )
}

