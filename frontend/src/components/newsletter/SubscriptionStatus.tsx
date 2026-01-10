import { VStack, HStack, Text, Badge } from '@chakra-ui/react'
import { useNewsletterSubscription } from '@/api/newsletter'
import { formatDate } from '@/utils/date'

export interface SubscriptionStatusProps {
  email: string
}

export const SubscriptionStatus = ({ email }: SubscriptionStatusProps) => {
  const { data: subscription, isLoading } = useNewsletterSubscription(email)

  if (isLoading) {
    return <Text>Loading subscription status...</Text>
  }

  if (!subscription) {
    return <Text>No active subscription found.</Text>
  }

  const statusColors = {
    active: 'green',
    pending: 'yellow',
    unsubscribed: 'gray',
  }

  return (
    <VStack align="stretch" spacing={4}>
      <HStack justify="space-between">
        <Text fontWeight="bold">Subscription Status</Text>
        <Badge colorScheme={statusColors[subscription.status]}>
          {subscription.status}
        </Badge>
      </HStack>
      <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
        Subscribed on {formatDate(subscription.subscribedAt)}
      </Text>
      <Text fontSize="sm">
        Frequency: {subscription.preferences.frequency}
      </Text>
    </VStack>
  )
}

