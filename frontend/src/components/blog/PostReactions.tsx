import { HStack, IconButton, Text, Box, useColorModeValue } from '@chakra-ui/react'
import { FaRegComment } from 'react-icons/fa'
import { Post } from '@/types'

export interface PostReactionsProps {
  post: Post
}

const scrollToComments = () => {
  document.getElementById('comments-section')?.scrollIntoView({ behavior: 'smooth' })
}

export const PostReactions = ({ post }: PostReactionsProps) => {
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  void post

  return (
    <HStack spacing={4}>
      <Box
        onClick={scrollToComments}
        cursor="pointer"
        border="1px solid"
        borderColor={borderColor}
        borderRadius="xl"
        px={2}
        py={1.5}
        display="flex"
        alignItems="center"
        _hover={{ opacity: 0.7 }}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault()
            scrollToComments()
          }
        }}
      >
        <HStack spacing={2}>
          <IconButton
            aria-label="Comments"
            icon={<FaRegComment />}
            variant="ghost"
            color="#145F95"
            size="md"
            pointerEvents="none"
            _hover={{ color: '#145F95', bg: 'transparent' }}
          />
          <Text fontSize="sm" fontWeight="medium">Comments</Text>
        </HStack>
      </Box>
    </HStack>
  )
}
