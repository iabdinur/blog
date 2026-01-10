import { VStack, FormControl, FormLabel, Input, Textarea, Button, useToast } from '@chakra-ui/react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'

const contactSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  email: z.string().email('Invalid email address'),
  subject: z.string().min(3, 'Subject must be at least 3 characters'),
  message: z.string().min(10, 'Message must be at least 10 characters'),
})

type ContactFormData = z.infer<typeof contactSchema>

export const ContactForm = () => {
  const toast = useToast()
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ContactFormData>({
    resolver: zodResolver(contactSchema),
  })

  const onSubmit = async (_data: ContactFormData) => {
    // TODO: Implement API call
    await new Promise((resolve) => setTimeout(resolve, 1000))
    toast({
      title: 'Message sent!',
      description: "We'll get back to you soon.",
      status: 'success',
      duration: 5000,
    })
    reset()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <VStack spacing={4}>
        <FormControl isInvalid={!!errors.name}>
          <FormLabel>Name</FormLabel>
          <Input {...register('name')} />
          {errors.name && <span>{errors.name.message}</span>}
        </FormControl>

        <FormControl isInvalid={!!errors.email}>
          <FormLabel>Email</FormLabel>
          <Input type="email" {...register('email')} />
          {errors.email && <span>{errors.email.message}</span>}
        </FormControl>

        <FormControl isInvalid={!!errors.subject}>
          <FormLabel>Subject</FormLabel>
          <Input {...register('subject')} />
          {errors.subject && <span>{errors.subject.message}</span>}
        </FormControl>

        <FormControl isInvalid={!!errors.message}>
          <FormLabel>Message</FormLabel>
          <Textarea rows={6} {...register('message')} />
          {errors.message && <span>{errors.message.message}</span>}
        </FormControl>

        <Button type="submit" colorScheme="brand" w="100%" isLoading={isSubmitting}>
          Send Message
        </Button>
      </VStack>
    </form>
  )
}

