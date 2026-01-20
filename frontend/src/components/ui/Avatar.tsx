import { Avatar as ChakraAvatar, AvatarProps as ChakraAvatarProps, AvatarBadge, AvatarGroup, useColorModeValue } from '@chakra-ui/react'
import { colorPalettes } from '@/compositions/lib/color-palettes'

export interface AvatarProps extends ChakraAvatarProps {
  name?: string
  src?: string
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl'
}

export const Avatar = ({ name, src, size = 'md', ...props }: AvatarProps) => {
  const bgColor = useColorModeValue(colorPalettes.grey[200], colorPalettes.grey[700])
  
  return (
    <ChakraAvatar 
      name={name} 
      src={src} 
      size={size}
      bg={bgColor}
      {...props} 
    />
  )
}

export { AvatarBadge, AvatarGroup }

