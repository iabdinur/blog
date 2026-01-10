import { Box, Input, Button, VStack, Text, useToast, InputGroup, InputRightElement } from '@chakra-ui/react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useSubscribeNewsletter } from '@/api/newsletter'

const subscribeSchema = z.object({
  email: z.string().email('Invalid email address'),
})

type SubscribeFormData = z.infer<typeof subscribeSchema>

export interface SubscribeFormProps {
  onSuccess?: () => void
}

export const SubscribeForm = ({ onSuccess }: SubscribeFormProps) => {
  const subscribe = useSubscribeNewsletter()
  const toast = useToast()
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<SubscribeFormData>({
    resolver: zodResolver(subscribeSchema),
  })

  const onSubmit = async (data: SubscribeFormData) => {
    try {
      await subscribe.mutateAsync(data.email)
      toast({
        title: 'Successfully subscribed!',
        description: 'Check your email for confirmation.',
        status: 'success',
        duration: 5000,
      })
      reset()
      onSuccess?.()
    } catch (error: any) {
      toast({
        title: 'Subscription failed',
        description: error.response?.data?.message || 'Please try again later.',
        status: 'error',
        duration: 5000,
      })
    }
  }

  return (
    <Box as="form" onSubmit={handleSubmit(onSubmit)}>
      <VStack spacing={4}>
        <InputGroup size="md">
          <Input
            placeholder="Type your email..."
            type="email"
            {...register('email')}
            isInvalid={!!errors.email}
            pr="120px"
          />
          <InputRightElement width="110px" pr={1}>
            <Button
              type="submit"
              colorScheme="brand"
              size="sm"
              isLoading={subscribe.isPending}
              w="100%"
            >
              Subscribe
            </Button>
          </InputRightElement>
        </InputGroup>
        {errors.email && (
          <Text color="red.500" _dark={{ color: 'red.400' }} fontSize="sm">
            {errors.email.message}
          </Text>
        )}
        <Text fontSize="xs" color="gray.500" _dark={{ color: 'gray.400' }} textAlign="center">
          We'll never share your email. Unsubscribe at any time.
        </Text>
      </VStack>
    </Box>
  )
}

