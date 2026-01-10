import { HStack, IconButton, Text, useToast } from '@chakra-ui/react'
import { FaHeart, FaBookmark } from 'react-icons/fa'
import { Post } from '@/types'
import { useLikePost } from '@/api/reactions'
import { useReadingListStore } from '@/store/useReadingListStore'

export interface PostReactionsProps {
  post: Post
}

export const PostReactions = ({ post }: PostReactionsProps) => {
  const likePost = useLikePost()
  const { addPost, removePost, isInList } = useReadingListStore()
  const toast = useToast()
  const inReadingList = isInList(post.id)

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

  const handleBookmark = () => {
    if (inReadingList) {
      removePost(post.id)
      toast({
        title: 'Removed from reading list',
        status: 'info',
        duration: 2000,
      })
    } else {
      addPost(post)
      toast({
        title: 'Added to reading list',
        status: 'success',
        duration: 2000,
      })
    }
  }

  return (
    <HStack spacing={4}>
      <HStack>
        <IconButton
          aria-label="Like post"
          icon={<FaHeart />}
          variant="ghost"
          colorScheme="red"
          onClick={handleLike}
          isLoading={likePost.isPending}
        />
        <Text fontSize="sm">{post.likes}</Text>
      </HStack>
      <IconButton
        aria-label={inReadingList ? 'Remove from reading list' : 'Add to reading list'}
        icon={<FaBookmark />}
        variant="ghost"
        colorScheme={inReadingList ? 'brand' : 'gray'}
        onClick={handleBookmark}
      />
    </HStack>
  )
}

