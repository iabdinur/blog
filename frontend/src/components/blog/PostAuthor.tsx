import { HStack, VStack, Text, Link } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { Author } from '@/types'
import { Avatar } from '../ui/Avatar'
import { formatRelativeTime } from '@/utils/date'

export interface PostAuthorProps {
  author: Author
  size?: 'sm' | 'md' | 'lg'
  showBio?: boolean
  showJoinedDate?: boolean
}

export const PostAuthor = ({ author, size = 'md', showBio = false, showJoinedDate = false }: PostAuthorProps) => {
  const avatarSize = size === 'sm' ? 'sm' : size === 'lg' ? 'lg' : 'md'
  const fontSize = size === 'sm' ? 'sm' : 'md'

  // Use profile image endpoint when email exists
  const getAvatarSrc = () => {
    if (author.email) {
      return `/api/v1/users/${encodeURIComponent(author.email)}/profile-image`
    }
    return undefined
  }

  return (
    <HStack spacing={3}>
      <Link as={RouterLink} to="/author">
        <Avatar 
          name={author.name} 
          src={getAvatarSrc()} 
          size={avatarSize}
          onError={() => {
            // Silently fail if image can't load - will show initials instead
          }}
        />
      </Link>
      <VStack align="start" spacing={0}>
        <Link
          as={RouterLink}
          to="/author"
          fontWeight="medium"
          fontSize={fontSize}
          _hover={{ color: 'brand.500' }}
        >
          {author.name}
        </Link>
        {showBio && author.bio && (
          <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }} noOfLines={2}>
            {author.bio}
          </Text>
        )}
        {showJoinedDate && (
          <Text fontSize="xs" color="gray.500" _dark={{ color: 'gray.400' }}>
            Joined {formatRelativeTime(author.joinedAt)}
          </Text>
        )}
      </VStack>
    </HStack>
  )
}

