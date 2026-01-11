import { Box, VStack, Heading, Textarea, Button, HStack, Text, useToast } from '@chakra-ui/react'
import { useState, useEffect } from 'react'
import { Comment } from '@/types'
import { useComments, useCreateComment } from '@/api/comments'
import { PostAuthor } from './PostAuthor'
import { formatRelativeTime } from '@/utils/date'
import { CreateProfilePopup } from '@/components/ui/CreateProfilePopup'

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
  const [showProfilePopup, setShowProfilePopup] = useState(false)
  const [authorId, setAuthorId] = useState<string | null>(null)
  const [pendingComment, setPendingComment] = useState<string | null>(null)
  const toast = useToast()

  useEffect(() => {
    // Check if user has a profile stored
    const storedAuthor = localStorage.getItem('commentAuthor')
    if (storedAuthor) {
      try {
        const author = JSON.parse(storedAuthor)
        setAuthorId(author.id.toString())
      } catch (error) {
        console.error('Failed to parse stored author:', error)
      }
    }
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!content.trim()) return

    // Check if user has a profile
    if (!authorId) {
      // Store the comment content and show profile popup
      setPendingComment(content)
      setShowProfilePopup(true)
      return
    }

    // User has profile, post the comment
    await postComment(content, authorId)
  }

  const postComment = async (commentContent: string, commentAuthorId: string) => {
    try {
      await createComment.mutateAsync({ slug, content: commentContent, authorId: commentAuthorId })
      setContent('')
      setPendingComment(null)
      toast({
        title: 'Comment posted',
        status: 'success',
        duration: 3000,
      })
    } catch (error) {
      toast({
        title: 'Failed to post comment',
        status: 'error',
        duration: 3000,
      })
    }
  }

  const handleProfileCreated = (newAuthorId: string) => {
    setAuthorId(newAuthorId)
    setShowProfilePopup(false)
    
    // Post the pending comment if there is one
    if (pendingComment) {
      postComment(pendingComment, newAuthorId)
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
              <Button type="submit" isLoading={createComment.isPending} colorScheme="brand">
                Post
              </Button>
            </HStack>
          </VStack>
        </form>
      </Box>

      <CreateProfilePopup
        isOpen={showProfilePopup}
        onClose={() => {
          setShowProfilePopup(false)
          setPendingComment(null)
        }}
        onSuccess={handleProfileCreated}
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

