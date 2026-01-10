import { HStack, IconButton, Tooltip, useToast } from '@chakra-ui/react'
import { FaLinkedin, FaCopy } from 'react-icons/fa'
import { Post } from '@/types'

export interface PostShareProps {
  post: Post
}

export const PostShare = ({ post }: PostShareProps) => {
  const toast = useToast()
  const url = typeof window !== 'undefined' ? `${window.location.origin}/post/${post.slug}` : ''
  const title = post.title

  const shareLinks = {
    linkedin: `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(url)}`,
  }

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(url)
      toast({
        title: 'Link copied to clipboard',
        status: 'success',
        duration: 2000,
      })
    } catch (error) {
      toast({
        title: 'Failed to copy link',
        status: 'error',
        duration: 2000,
      })
    }
  }

  return (
    <HStack spacing={2}>
      <Tooltip label="Share on LinkedIn">
        <IconButton
          as="a"
          href={shareLinks.linkedin}
          target="_blank"
          rel="noopener noreferrer"
          aria-label="Share on LinkedIn"
          icon={<FaLinkedin />}
          variant="ghost"
          size="sm"
        />
      </Tooltip>
      <Tooltip label="Copy link">
        <IconButton
          aria-label="Copy link"
          icon={<FaCopy />}
          variant="ghost"
          size="sm"
          onClick={handleCopyLink}
        />
      </Tooltip>
    </HStack>
  )
}

