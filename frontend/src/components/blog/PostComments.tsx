import { Box, VStack, Heading, Textarea, Button, HStack, Text, useToast, IconButton, useDisclosure, AlertDialog, AlertDialogBody, AlertDialogFooter, AlertDialogHeader, AlertDialogContent, AlertDialogOverlay } from '@chakra-ui/react'
import { useState, useRef } from 'react'
import { Comment } from '@/types'
import { useComments, useCreateComment, useUpdateComment, useDeleteComment } from '@/api/comments'
import { PostAuthor } from './PostAuthor'
import { formatRelativeTime } from '@/utils/date'
import { LoginRegisterModal } from '@/components/ui/LoginRegisterModal'
import { getUserEmailFromToken } from '@/utils/auth'
import { FaEdit, FaTrash } from 'react-icons/fa'

export interface PostCommentsProps {
  slug: string
}

const CommentItem = ({ 
  comment, 
  slug, 
  onUpdate, 
  onDelete 
}: { 
  comment: Comment
  slug: string
  onUpdate: (commentId: string, content: string) => void
  onDelete: (commentId: string) => void
}) => {
  const [isEditing, setIsEditing] = useState(false)
  const [editContent, setEditContent] = useState(comment.content)
  const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure()
  const cancelRef = useRef<HTMLButtonElement>(null)
  const token = localStorage.getItem('auth_token')
  const userEmail = getUserEmailFromToken(token)
  const canEdit = userEmail && comment.author?.email === userEmail

  const handleSaveEdit = () => {
    if (editContent.trim() && editContent !== comment.content) {
      onUpdate(comment.id, editContent.trim())
    }
    setIsEditing(false)
  }

  const handleCancelEdit = () => {
    setEditContent(comment.content)
    setIsEditing(false)
  }

  const handleDeleteConfirm = () => {
    onDelete(comment.id)
    onDeleteClose()
  }

  return (
    <Box borderLeft="2px" borderColor="gray.200" _dark={{ borderColor: 'gray.700' }} pl={4} py={2}>
      <HStack justify="space-between" mb={2}>
        <PostAuthor author={comment.author} size="sm" />
        <HStack spacing={2}>
          <Text fontSize="xs" color="gray.500" _dark={{ color: 'gray.400' }}>
            {formatRelativeTime(comment.createdAt)}
          </Text>
          {canEdit && !isEditing && (
            <>
              <IconButton
                aria-label="Edit comment"
                icon={<FaEdit />}
                size="xs"
                variant="ghost"
                onClick={() => setIsEditing(true)}
              />
              <IconButton
                aria-label="Delete comment"
                icon={<FaTrash />}
                size="xs"
                variant="ghost"
                colorScheme="red"
                onClick={onDeleteOpen}
              />
            </>
          )}
        </HStack>
      </HStack>
      {isEditing ? (
        <VStack align="stretch" spacing={2}>
          <Textarea
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            rows={3}
            fontSize="sm"
          />
          <HStack justify="flex-end" spacing={2}>
            <Button size="xs" variant="ghost" onClick={handleCancelEdit}>
              Cancel
            </Button>
            <Button size="xs" colorScheme="blue" onClick={handleSaveEdit}>
              Save
            </Button>
          </HStack>
        </VStack>
      ) : (
        <Text fontSize="sm" mb={2}>
          {comment.content}
        </Text>
      )}
      {comment.replies && comment.replies.length > 0 && (
        <VStack align="stretch" pl={4} mt={2}>
          {comment.replies.map((reply) => (
            <CommentItem 
              key={reply.id} 
              comment={reply} 
              slug={slug}
              onUpdate={onUpdate}
              onDelete={onDelete}
            />
          ))}
        </VStack>
      )}

      <AlertDialog
        isOpen={isDeleteOpen}
        leastDestructiveRef={cancelRef}
        onClose={onDeleteClose}
      >
        <AlertDialogOverlay>
          <AlertDialogContent>
            <AlertDialogHeader fontSize="lg" fontWeight="bold">
              Delete Comment
            </AlertDialogHeader>
            <AlertDialogBody>
              Are you sure you want to delete this comment? This action cannot be undone.
            </AlertDialogBody>
            <AlertDialogFooter>
              <Button ref={cancelRef} onClick={onDeleteClose}>
                Cancel
              </Button>
              <Button colorScheme="red" onClick={handleDeleteConfirm} ml={3}>
                Delete
              </Button>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialogOverlay>
      </AlertDialog>
    </Box>
  )
}

export const PostComments = ({ slug }: PostCommentsProps) => {
  const { data: comments, isLoading } = useComments(slug)
  const createComment = useCreateComment()
  const updateComment = useUpdateComment()
  const deleteComment = useDeleteComment()
  const [content, setContent] = useState('')
  const [showLoginModal, setShowLoginModal] = useState(false)
  const [pendingComment, setPendingComment] = useState<string | null>(null)
  const toast = useToast()

  // Authentication is checked directly when needed (in handleSubmit)

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
    // If there's a pending comment, post it now
    if (pendingComment) {
      postComment(pendingComment)
    }
  }

  const handleUpdateComment = async (commentId: string, newContent: string) => {
    try {
      await updateComment.mutateAsync({ slug, commentId, content: newContent })
      toast({
        title: 'Comment updated',
        status: 'success',
        duration: 2000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to update comment',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    }
  }

  const handleDeleteComment = async (commentId: string) => {
    try {
      await deleteComment.mutateAsync({ slug, commentId })
      toast({
        title: 'Comment deleted',
        status: 'success',
        duration: 2000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to delete comment',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
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
              <CommentItem 
                key={comment.id} 
                comment={comment} 
                slug={slug}
                onUpdate={handleUpdateComment}
                onDelete={handleDeleteComment}
              />
            ))
          )}
        </VStack>
      )}
    </Box>
  )
}

