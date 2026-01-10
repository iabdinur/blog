import { Avatar as ChakraAvatar, AvatarProps as ChakraAvatarProps, AvatarBadge, AvatarGroup } from '@chakra-ui/react'

export interface AvatarProps extends ChakraAvatarProps {
  name?: string
  src?: string
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl'
}

export const Avatar = ({ name, src, size = 'md', ...props }: AvatarProps) => {
  return <ChakraAvatar name={name} src={src} size={size} {...props} />
}

export { AvatarBadge, AvatarGroup }

