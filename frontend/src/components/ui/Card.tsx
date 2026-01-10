import { Box, BoxProps, useColorModeValue } from '@chakra-ui/react'

export interface CardProps extends BoxProps {
  hoverable?: boolean
}

export const Card = ({ hoverable = false, children, ...props }: CardProps) => {
  const bg = useColorModeValue('white', 'gray.800')
  
  if (hoverable) {
    return (
      <Box
        bg={bg}
        borderRadius="lg"
        boxShadow="md"
        p={6}
        transition="all 0.2s ease"
        _hover={{
          boxShadow: 'lg',
        }}
        {...props}
      >
        {children}
      </Box>
    )
  }

  return (
    <Box
      bg={bg}
      borderRadius="lg"
      boxShadow="md"
      p={6}
      {...props}
    >
      {children}
    </Box>
  )
}

