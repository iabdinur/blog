import { VStack, IconButton, useToast, Text, Box, useColorModeValue } from '@chakra-ui/react'
import { FaShare } from 'react-icons/fa'
import { Post } from '@/types'

export interface PostShareProps {
  post: Post
}

export const PostShare = ({ post }: PostShareProps) => {
  const toast = useToast()
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const url = typeof window !== 'undefined' ? `${window.location.origin}/post/${post.slug}` : ''

  const handleShare = async () => {
    try {
      if (navigator.share) {
        await navigator.share({
          title: post.title,
          url: url,
        })
      } else {
        // Fallback to copy if Web Share API is not available
        await navigator.clipboard.writeText(url)
        toast({
          title: 'Link copied to clipboard',
          status: 'success',
          duration: 2000,
        })
      }
    } catch (error) {
      // User cancelled or error occurred
      if (error instanceof Error && error.name !== 'AbortError') {
        toast({
          title: 'Failed to share',
          status: 'error',
          duration: 2000,
        })
      }
    }
  }

  return (
    <Box
      onClick={handleShare}
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
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault()
          handleShare()
        }
      }}
    >
      <VStack spacing={0} align="center">
        <IconButton
          aria-label="Share"
          icon={<FaShare />}
          variant="ghost"
          color="#145F95"
          size="lg"
          pointerEvents="none"
          _hover={{ color: '#145F95', bg: 'transparent' }}
        />
        <Text fontSize="sm" fontWeight="medium" mt="-1">0</Text>
      </VStack>
    </Box>
  )
}

