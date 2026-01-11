import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  VStack,
  FormControl,
  FormLabel,
  Input,
  Textarea,
  Button,
  Checkbox,
  Text,
  Box,
  useColorModeValue,
  Avatar,
  Center,
  Icon,
} from '@chakra-ui/react'
import { useState } from 'react'
import { useCreateAuthor } from '@/api/admin'
import { useUIStore } from '@/store/useUIStore'
import { FaCamera } from 'react-icons/fa'

interface CreateProfilePopupProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: (authorId: string) => void
}

export const CreateProfilePopup = ({ isOpen, onClose, onSuccess }: CreateProfilePopupProps) => {
  const createAuthor = useCreateAuthor()
  const { setNewsletterPopupOpen } = useUIStore()
  const bg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const hoverBg = useColorModeValue('gray.50', 'gray.700')

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    username: '',
    bio: '',
    avatar: null as File | null,
  })
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const [subscribeNewsletter, setSubscribeNewsletter] = useState(false)
  const [agreeToTerms, setAgreeToTerms] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.name || !formData.email) {
      return
    }

    if (!agreeToTerms) {
      return
    }

    try {
      // Generate username from name if not provided
      const username = formData.username || formData.name.toLowerCase().replace(/\s+/g, '-')
      
      // For now, we'll skip file upload and just use a placeholder
      // In a real implementation, you'd upload the file to a server first
      const avatarUrl = formData.avatar ? '' : '' // Placeholder for now

      const authorData = {
        name: formData.name,
        username: username,
        email: formData.email,
        bio: formData.bio || '',
        avatar: avatarUrl,
        coverImage: '',
        location: '',
        website: '',
        twitter: '',
        github: '',
        linkedin: '',
      }

      const createdAuthor = await createAuthor.mutateAsync(authorData)
      
      // Store author info in localStorage for future comments
      localStorage.setItem('commentAuthor', JSON.stringify({
        id: createdAuthor.id,
        name: createdAuthor.name,
        username: createdAuthor.username,
        email: createdAuthor.email,
      }))

      // If user wants to subscribe to newsletter, open newsletter popup
      if (subscribeNewsletter) {
        setNewsletterPopupOpen(true)
      }

      onSuccess(createdAuthor.id.toString())
      onClose()
      
      // Reset form
      setFormData({
        name: '',
        email: '',
        username: '',
        bio: '',
        avatar: null,
      })
      setAvatarPreview(null)
      setSubscribeNewsletter(false)
      setAgreeToTerms(false)
    } catch (error) {
      console.error('Failed to create profile:', error)
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setFormData({ ...formData, avatar: file })
      // Create preview URL
      const reader = new FileReader()
      reader.onloadend = () => {
        setAvatarPreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="md" isCentered>
      <ModalOverlay />
      <ModalContent bg={bg}>
        <ModalHeader color={textColor}>Create your profile</ModalHeader>
        <ModalCloseButton />
        <ModalBody pb={6}>
          <form onSubmit={handleSubmit}>
            <VStack spacing={4} align="stretch">
              <Center>
                <Box position="relative" cursor="pointer">
                  <Input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    display="none"
                    id="avatar-upload"
                  />
                  <label htmlFor="avatar-upload">
                    <Avatar
                      size="xl"
                      src={avatarPreview || undefined}
                      name={formData.name || 'User'}
                      cursor="pointer"
                      border="2px solid"
                      borderColor={borderColor}
                      _hover={{
                        opacity: 0.8,
                        borderColor: 'brand.500',
                      }}
                      transition="all 0.2s"
                    />
                    <Box
                      position="absolute"
                      bottom={0}
                      right={0}
                      bg="brand.500"
                      color="white"
                      borderRadius="full"
                      p={2}
                      border="2px solid"
                      borderColor={bg}
                      _hover={{ bg: 'brand.600' }}
                      transition="all 0.2s"
                    >
                      <Icon as={FaCamera} boxSize={3} />
                    </Box>
                  </label>
                </Box>
              </Center>

              <FormControl isRequired>
                <FormLabel color={textColor}>Name</FormLabel>
                <Input
                  placeholder="Type your name..."
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  borderColor={borderColor}
                  color={textColor}
                  _placeholder={{ color: 'gray.500' }}
                />
              </FormControl>

              <FormControl isRequired>
                <FormLabel color={textColor}>Email</FormLabel>
                <Input
                  type="email"
                  placeholder="Enter your email address..."
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  borderColor={borderColor}
                  color={textColor}
                  _placeholder={{ color: 'gray.500' }}
                />
              </FormControl>

              <FormControl>
                <FormLabel color={textColor}>Bio</FormLabel>
                <Textarea
                  placeholder="Say something about yourself..."
                  value={formData.bio}
                  onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                  rows={3}
                  borderColor={borderColor}
                  color={textColor}
                  _placeholder={{ color: 'gray.500' }}
                />
              </FormControl>

              <Checkbox
                isChecked={subscribeNewsletter}
                onChange={(e) => setSubscribeNewsletter(e.target.checked)}
                colorScheme="brand"
              >
                <Text fontSize="sm" color={textColor}>Subscribe to the newsletter</Text>
              </Checkbox>

              <Checkbox
                isChecked={agreeToTerms}
                onChange={(e) => setAgreeToTerms(e.target.checked)}
                colorScheme="brand"
                isRequired
              >
                <Text fontSize="sm" color={textColor}>
                  I agree to Substack's Terms of Use, and acknowledge its Information Collection Notice and Privacy Policy.
                </Text>
              </Checkbox>

              <Button
                type="submit"
                colorScheme="brand"
                isLoading={createAuthor.isPending}
                isDisabled={!formData.name || !formData.email || !agreeToTerms}
                mt={2}
              >
                Save and Post Comment
              </Button>
            </VStack>
          </form>
        </ModalBody>
      </ModalContent>
    </Modal>
  )
}
