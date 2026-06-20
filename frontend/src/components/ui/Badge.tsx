import { Badge as ChakraBadge, BadgeProps as ChakraBadgeProps } from '@chakra-ui/react'

export interface BadgeProps extends ChakraBadgeProps {
  variant?: 'solid' | 'outline' | 'subtle'
}

export const Badge = ({ variant = 'subtle', ...props }: BadgeProps) => {
  return <ChakraBadge variant={variant} {...props} />
}

