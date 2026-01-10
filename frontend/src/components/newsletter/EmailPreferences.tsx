import { VStack, FormControl, FormLabel, Select, CheckboxGroup, Checkbox, Button, useToast } from '@chakra-ui/react'
import { useState } from 'react'
import { NewsletterSubscription } from '@/types'
import { newsletterApi } from '@/api/newsletter'

export interface EmailPreferencesProps {
  subscription: NewsletterSubscription
  onUpdate?: () => void
}

export const EmailPreferences = ({ subscription, onUpdate }: EmailPreferencesProps) => {
  const [frequency, setFrequency] = useState(subscription.preferences.frequency)
  const [categories, setCategories] = useState(subscription.preferences.categories)
  const [isLoading, setIsLoading] = useState(false)
  const toast = useToast()

  const availableCategories = ['Technology', 'Design', 'Business', 'Lifestyle', 'Tutorials']

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      await newsletterApi.updatePreferences(subscription.email, {
        frequency,
        categories,
      })
      toast({
        title: 'Preferences updated',
        status: 'success',
        duration: 3000,
      })
      onUpdate?.()
    } catch (error) {
      toast({
        title: 'Failed to update preferences',
        status: 'error',
        duration: 3000,
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <VStack align="stretch" spacing={4}>
        <FormControl>
          <FormLabel>Email Frequency</FormLabel>
          <Select value={frequency} onChange={(e) => setFrequency(e.target.value as any)}>
            <option value="daily">Daily</option>
            <option value="weekly">Weekly</option>
            <option value="monthly">Monthly</option>
          </Select>
        </FormControl>

        <FormControl>
          <FormLabel>Categories</FormLabel>
          <CheckboxGroup value={categories} onChange={(values) => setCategories(values as string[])}>
            <VStack align="start" spacing={2}>
              {availableCategories.map((category) => (
                <Checkbox key={category} value={category}>
                  {category}
                </Checkbox>
              ))}
            </VStack>
          </CheckboxGroup>
        </FormControl>

        <Button type="submit" colorScheme="brand" isLoading={isLoading}>
          Save Preferences
        </Button>
      </VStack>
    </form>
  )
}

