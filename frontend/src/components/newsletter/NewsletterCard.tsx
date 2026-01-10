import { VStack, Heading, Text } from '@chakra-ui/react'
import { Card } from '../ui/Card'
import { SubscribeForm } from './SubscribeForm'

export const NewsletterCard = () => {
  return (
    <Card>
      <VStack align="stretch" spacing={4}>
        <VStack align="start" spacing={2}>
          <Heading size="md">Subscribe to Newsletter</Heading>
          <Text color="gray.600" _dark={{ color: 'gray.400' }}>
            Get the latest posts delivered to your inbox. No spam, unsubscribe anytime.
          </Text>
        </VStack>
        <SubscribeForm />
      </VStack>
    </Card>
  )
}

