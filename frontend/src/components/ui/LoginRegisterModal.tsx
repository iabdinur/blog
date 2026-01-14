import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  VStack,
  Heading,
  Box,
  Input,
  Button,
  Text,
  Alert,
  AlertIcon,
  HStack,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  useColorModeValue,
  FormControl,
  FormLabel,
  Textarea,
  Checkbox,
  Avatar,
  Center,
  Icon,
} from '@chakra-ui/react'
import { useState } from 'react'
import { useSendVerificationCode, useVerifyCode, useLogin } from '@/api/admin'
import { apiClient } from '@/api/client'
import { FaCamera } from 'react-icons/fa'
import { useUIStore } from '@/store/useUIStore'

interface LoginRegisterModalProps {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
}

export const LoginRegisterModal = ({ isOpen, onClose, onSuccess }: LoginRegisterModalProps) => {
  const [tabIndex, setTabIndex] = useState(0)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [bio, setBio] = useState('')
  const [avatar, setAvatar] = useState<File | null>(null)
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const [subscribeNewsletter, setSubscribeNewsletter] = useState(false)
  const [agreeToTerms, setAgreeToTerms] = useState(false)
  const [code, setCode] = useState('')
  const [codeSent, setCodeSent] = useState(false)
  const [usePassword, setUsePassword] = useState(false)
  
  const sendCodeMutation = useSendVerificationCode()
  const verifyCodeMutation = useVerifyCode()
  const loginMutation = useLogin()
  const [isRegistering, setIsRegistering] = useState(false)
  const { setNewsletterPopupOpen } = useUIStore()

  const bg = useColorModeValue('white', 'gray.800')
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const hoverBg = useColorModeValue('gray.50', 'gray.700')

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name || !email || !password || !agreeToTerms) return

    setIsRegistering(true)
    try {
      // First, register the user
      const response = await apiClient.post('/users', {
        name,
        email,
        password,
      })
      
      // Extract token from Authorization header
      const token = response.headers['authorization'] || response.headers['Authorization']
      if (token) {
        localStorage.setItem('auth_token', token)
      }

      // If user uploaded a profile image, upload it
      if (avatar) {
        try {
          const formData = new FormData()
          formData.append('file', avatar)
          await apiClient.post(`/users/${email}/profile-image`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
              Authorization: `Bearer ${token}`,
            },
          })
        } catch (error) {
          console.error('Failed to upload profile image:', error)
          // Continue even if image upload fails
        }
      }

      // If user wants to subscribe to newsletter, open newsletter popup
      if (subscribeNewsletter) {
        setNewsletterPopupOpen(true)
      }

      setIsRegistering(false)
      resetForm()
      onSuccess()
      onClose()
    } catch (error: any) {
      setIsRegistering(false)
      console.error('Registration failed:', error)
      // Error will be shown by the form validation or toast
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setAvatar(file)
      // Create preview URL
      const reader = new FileReader()
      reader.onloadend = () => {
        setAvatarPreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleSendCode = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await sendCodeMutation.mutateAsync({ email })
      setCodeSent(true)
    } catch (error) {
      // Error handled by mutation
    }
  }

  const handleVerifyCode = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await verifyCodeMutation.mutateAsync({ email, code })
      onSuccess()
      onClose()
    } catch (error) {
      // Error handled by mutation
    }
  }

  const handlePasswordLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await loginMutation.mutateAsync({ username: email, password })
      onSuccess()
      onClose()
    } catch (error) {
      // Error handled by mutation
    }
  }

  const resetForm = () => {
    setEmail('')
    setPassword('')
    setName('')
    setBio('')
    setAvatar(null)
    setAvatarPreview(null)
    setSubscribeNewsletter(false)
    setAgreeToTerms(false)
    setCode('')
    setCodeSent(false)
    setUsePassword(false)
  }

  const handleClose = () => {
    resetForm()
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md" isCentered>
      <ModalOverlay />
      <ModalContent bg={bg}>
        <ModalHeader color={textColor}>Login or Register</ModalHeader>
        <ModalCloseButton />
        <ModalBody pb={6}>
          <Tabs index={tabIndex} onChange={setTabIndex}>
            <TabList>
              <Tab>Login</Tab>
              <Tab>Register</Tab>
            </TabList>

            <TabPanels>
              {/* Login Tab */}
              <TabPanel>
                {(sendCodeMutation.isError || verifyCodeMutation.isError || loginMutation.isError) && (
                  <Alert status="error" mb={4}>
                    <AlertIcon />
                    {sendCodeMutation.isError
                      ? 'Failed to send verification code'
                      : verifyCodeMutation.isError
                      ? 'Invalid verification code'
                      : 'Invalid email or password'}
                  </Alert>
                )}

                {usePassword ? (
                  <form onSubmit={handlePasswordLogin}>
                    <VStack spacing={4}>
                      <Box w="100%">
                        <Text mb={2} fontSize="sm" fontWeight="medium">
                          Email
                        </Text>
                        <Input
                          type="email"
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          placeholder="you@email.com"
                          required
                        />
                      </Box>

                      <Box w="100%">
                        <Text mb={2} fontSize="sm" fontWeight="medium">
                          Password
                        </Text>
                        <Input
                          type="password"
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                          placeholder="Enter password"
                          required
                        />
                      </Box>

                      <Button
                        type="submit"
                        w="100%"
                        colorScheme="brand"
                        isLoading={loginMutation.isPending}
                      >
                        Log In
                      </Button>

                      <Button
                        type="button"
                        variant="ghost"
                        w="100%"
                        size="sm"
                        onClick={() => setUsePassword(false)}
                      >
                        Use verification code instead
                      </Button>
                    </VStack>
                  </form>
                ) : !codeSent ? (
                  <form onSubmit={handleSendCode}>
                    <VStack spacing={4}>
                      <Box w="100%">
                        <Text mb={2} fontSize="sm" fontWeight="medium">
                          Email
                        </Text>
                        <Input
                          type="email"
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          placeholder="you@email.com"
                          required
                        />
                      </Box>

                      <Button
                        type="submit"
                        w="100%"
                        colorScheme="brand"
                        isLoading={sendCodeMutation.isPending}
                      >
                        Send Verification Code
                      </Button>

                      <Button
                        type="button"
                        variant="ghost"
                        w="100%"
                        size="sm"
                        onClick={() => setUsePassword(true)}
                      >
                        Use password instead
                      </Button>
                    </VStack>
                  </form>
                ) : (
                  <form onSubmit={handleVerifyCode}>
                    <VStack spacing={4}>
                      <Box w="100%">
                        <Text mb={2} fontSize="sm" fontWeight="medium">
                          Verification Code
                        </Text>
                        <Input
                          value={code}
                          onChange={(e) => setCode(e.target.value)}
                          placeholder="Enter verification code"
                          required
                          maxLength={6}
                        />
                      </Box>

                      <Button
                        type="submit"
                        w="100%"
                        colorScheme="brand"
                        isLoading={verifyCodeMutation.isPending}
                      >
                        Verify Code
                      </Button>

                      <Button
                        type="button"
                        variant="ghost"
                        w="100%"
                        size="sm"
                        onClick={() => {
                          setCodeSent(false)
                          setCode('')
                        }}
                      >
                        Use different email
                      </Button>
                    </VStack>
                  </form>
                )}
              </TabPanel>

              {/* Register Tab */}
              <TabPanel>
                <form onSubmit={handleRegister}>
                  <VStack spacing={4} align="stretch">
                    <Center>
                      <Box position="relative" cursor="pointer">
                        <Input
                          type="file"
                          accept="image/*"
                          onChange={handleFileChange}
                          display="none"
                          id="avatar-upload-register"
                        />
                        <label htmlFor="avatar-upload-register">
                          <Avatar
                            size="xl"
                            src={avatarPreview || undefined}
                            name={name || 'User'}
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
                        value={name}
                        onChange={(e) => setName(e.target.value)}
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
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        borderColor={borderColor}
                        color={textColor}
                        _placeholder={{ color: 'gray.500' }}
                      />
                    </FormControl>

                    <FormControl isRequired>
                      <FormLabel color={textColor}>Password</FormLabel>
                      <Input
                        type="password"
                        placeholder="Create a password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        borderColor={borderColor}
                        color={textColor}
                        _placeholder={{ color: 'gray.500' }}
                        minLength={6}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel color={textColor}>Bio</FormLabel>
                      <Textarea
                        placeholder="Say something about yourself..."
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
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
                        I agree to the Terms of Use and Privacy Policy.
                      </Text>
                    </Checkbox>

                    <Button
                      type="submit"
                      colorScheme="brand"
                      isLoading={isRegistering}
                      isDisabled={!name || !email || !password || !agreeToTerms}
                      mt={2}
                    >
                      Create Account
                    </Button>
                  </VStack>
                </form>
              </TabPanel>
            </TabPanels>
          </Tabs>
        </ModalBody>
      </ModalContent>
    </Modal>
  )
}
