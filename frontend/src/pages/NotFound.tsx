import { VStack, Heading, Text, Button, Box } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { Layout } from '@/components/layout/Layout'

export const NotFound = () => {
  return (
    <Layout>
      <Box textAlign="center" py={16}>
        <VStack spacing={6}>
          <Heading size="4xl">404</Heading>
          <Heading size="xl">Page Not Found</Heading>
          <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }}>
            The page you're looking for doesn't exist or has been moved.
          </Text>
          <Button as={RouterLink} to="/" colorScheme="brand">
            Go Home
          </Button>
        </VStack>
      </Box>
    </Layout>
  )
}

