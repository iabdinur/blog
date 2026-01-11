import { HStack, VStack, IconButton, Text, useToast, Box, useColorModeValue } from '@chakra-ui/react'
import { FaRegHeart, FaRegComment } from 'react-icons/fa'
import { FaUsersViewfinder } from 'react-icons/fa6'
import { Post } from '@/types'
import { useLikePost } from '@/api/reactions'

export interface PostReactionsProps {
  post: Post
  commentsCount?: number
}

export const PostReactions = ({ post, commentsCount }: PostReactionsProps) => {
  const likePost = useLikePost()
  const toast = useToast()
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const displayCommentsCount = commentsCount !== undefined ? commentsCount : (post.commentsCount || 0)

  const handleLike = async () => {
    try {
      await likePost.mutateAsync(post.id)
    } catch (error) {
      toast({
        title: 'Failed to like post',
        status: 'error',
        duration: 2000,
      })
    }
  }

  return (
    <HStack spacing={4}>
      <Box
        as="button"
        onClick={handleLike}
        cursor="pointer"
        border="1px solid"
        borderColor={borderColor}
        borderRadius="xl"
        px={1}
        py={1.5}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        _hover={{ opacity: 0.7 }}
      >
        <VStack spacing={0} align="center">
          <IconButton
            aria-label="Like post"
            icon={<FaRegHeart />}
            variant="ghost"
            color="#145F95"
            isLoading={likePost.isPending}
            size="lg"
            _hover={{ color: '#145F95', bg: 'transparent' }}
          />
          <Text fontSize="sm" fontWeight="medium" mt="-1">{post.likes}</Text>
        </VStack>
      </Box>
      <Box
        as="button"
        onClick={() => {
          const commentsSection = document.getElementById('comments-section')
          if (commentsSection) {
            commentsSection.scrollIntoView({ behavior: 'smooth' })
          }
        }}
        cursor="pointer"
        border="1px solid"
        borderColor={borderColor}
        borderRadius="xl"
        px={1}
        py={1.5}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        _hover={{ opacity: 0.7 }}
      >
        <VStack spacing={0} align="center">
          <IconButton
            aria-label="Comments"
            icon={<FaRegComment />}
            variant="ghost"
            color="#145F95"
            size="lg"
            pointerEvents="none"
            _hover={{ color: '#145F95', bg: 'transparent' }}
          />
          <Text fontSize="sm" fontWeight="medium" mt="-1">{displayCommentsCount}</Text>
        </VStack>
      </Box>
      <Box
        border="1px solid"
        borderColor={borderColor}
        borderRadius="xl"
        px={1}
        py={1.5}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
      >
        <VStack spacing={0} align="center">
          <IconButton
            aria-label="Views"
            icon={<FaUsersViewfinder />}
            variant="ghost"
            color="#145F95"
            size="lg"
            pointerEvents="none"
            _hover={{ color: '#145F95', bg: 'transparent' }}
          />
          <Text fontSize="sm" fontWeight="medium" mt="-1">{post.views || 0}</Text>
        </VStack>
      </Box>
    </HStack>
  )
}

