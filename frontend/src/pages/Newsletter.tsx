import { VStack, Heading, Text, Box } from '@chakra-ui/react'
import { NewsletterCard } from '@/components/newsletter/NewsletterCard'
import { Layout } from '@/components/layout/Layout'

export const Newsletter = () => {
  return (
    <Layout>
      <VStack align="stretch" spacing={8}>
        <Box textAlign="center">
          <Heading fontSize="32px" mb={4}>
            Newsletter
          </Heading>
          <Text fontSize="lg" color="gray.600" _dark={{ color: 'gray.400' }}>
            Stay updated with our latest posts, tutorials, and insights delivered straight to your inbox.
          </Text>
        </Box>

        <NewsletterCard />

        <Box>
          <Heading fontSize="24px" mb={4}>
            What to Expect
          </Heading>
          <VStack align="stretch" spacing={3}>
            <Text>• Weekly roundup of our best content</Text>
            <Text>• Exclusive tutorials and guides</Text>
            <Text>• Community highlights and featured posts</Text>
            <Text>• No spam, unsubscribe anytime</Text>
          </VStack>
        </Box>
      </VStack>
    </Layout>
  )
}

