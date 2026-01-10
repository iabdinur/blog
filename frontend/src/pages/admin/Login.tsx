import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { VStack, Heading, Box, Input, Button, Text, Alert, AlertIcon, HStack, Checkbox, Link, Flex, useColorModeValue } from '@chakra-ui/react'
import { useSendVerificationCode, useVerifyCode, useLogin } from '@/api/admin'
import { Footer } from '@/components/layout/Footer'

export const AdminLogin = () => {
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [codeSent, setCodeSent] = useState(false)
  const [rememberMe, setRememberMe] = useState(true)
  const [usePassword, setUsePassword] = useState(false)
  const [password, setPassword] = useState('')
  const navigate = useNavigate()
  const sendCodeMutation = useSendVerificationCode()
  const verifyCodeMutation = useVerifyCode()
  const loginMutation = useLogin()

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
      navigate('/admin/posts')
    } catch (error) {
      // Error handled by mutation
    }
  }

  const handlePasswordLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await loginMutation.mutateAsync({ username: email, password })
      navigate('/admin/posts')
    } catch (error) {
      // Error handled by mutation
    }
  }

  const pageBg = useColorModeValue('#FFFFFF', 'gray.900')
  const cardBg = useColorModeValue('white', 'gray.800')
  const infoBoxBg = useColorModeValue('white', 'gray.700')
  const infoBoxColor = useColorModeValue('gray.700', 'gray.300')
  const infoBoxBorderColor = useColorModeValue('gray.200', 'gray.600')
  const iconBg = useColorModeValue('gray.400', 'gray.600')
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const labelColor = useColorModeValue('gray.700', 'gray.300')
  const inputBorderColor = useColorModeValue('gray.200', 'gray.600')
  const inputHoverBorderColor = useColorModeValue('gray.300', 'gray.500')
  const inputFocusBorderColor = '#2C6CB0'

  return (
    <Flex minH="100vh" direction="column" bg={pageBg}>
      <Box flex="1" display="flex" alignItems="center" justifyContent="center" py={8}>
        <Box w="400px" p={8} bg={cardBg} borderRadius="lg" boxShadow="lg">
          <VStack spacing={6} align="stretch">
            <Heading size="lg" textAlign="center" color={textColor}>
              Log In
            </Heading>

          {(sendCodeMutation.isError || verifyCodeMutation.isError || loginMutation.isError) && (
            <Alert status="error">
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
                  <Text mb={2} fontSize="sm" fontWeight="medium" color={labelColor}>
                    Email
                  </Text>
                  <Input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@email.com"
                    required
                    bg={cardBg}
                    borderColor={inputBorderColor}
                    _hover={{ borderColor: inputHoverBorderColor }}
                    _focus={{ borderColor: inputFocusBorderColor, boxShadow: `0 0 0 1px ${inputFocusBorderColor}` }}
                  />
                </Box>

                <Box w="100%">
                  <Text mb={2} fontSize="sm" fontWeight="medium" color={labelColor}>
                    Password
                  </Text>
                  <Input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter password"
                    required
                    bg={cardBg}
                    borderColor={inputBorderColor}
                    _hover={{ borderColor: inputHoverBorderColor }}
                    _focus={{ borderColor: inputFocusBorderColor, boxShadow: `0 0 0 1px ${inputFocusBorderColor}` }}
                  />
                </Box>

                <Button
                  type="submit"
                  w="100%"
                  bg="#2C6CB0"
                  color="white"
                  _hover={{ bg: '#23568D' }}
                  isLoading={loginMutation.isPending}
                  loadingText="Logging in..."
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
                  <Text mb={2} fontSize="sm" fontWeight="medium" color={labelColor}>
                    Email
                  </Text>
                  <Input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@email.com"
                    required
                    bg={cardBg}
                    borderColor={inputBorderColor}
                    _hover={{ borderColor: inputHoverBorderColor }}
                    _focus={{ borderColor: inputFocusBorderColor, boxShadow: `0 0 0 1px ${inputFocusBorderColor}` }}
                  />
                </Box>

                <HStack w="100%" spacing={2} align="start">
                  <Checkbox
                    isChecked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    sx={{
                      '& .chakra-checkbox__control': {
                        _checked: {
                          bg: '#2C6CB0',
                          borderColor: '#2C6CB0',
                          _hover: {
                            bg: '#23568D',
                            borderColor: '#23568D',
                          },
                        },
                      },
                    }}
                  />
                  <Text fontSize="sm" color="gray.600" _dark={{ color: 'gray.400' }}>
                    Remember me
                  </Text>
                </HStack>

                <Button
                  type="submit"
                  w="100%"
                  bg="#2C6CB0"
                  color="white"
                  _hover={{ bg: '#23568D' }}
                  isLoading={sendCodeMutation.isPending}
                  loadingText="Sending..."
                >
                  Log In
                </Button>
              </VStack>
            </form>
          ) : (
            <form onSubmit={handleVerifyCode}>
              <VStack spacing={4}>
                <Box w="100%">
                  <Text mb={2} fontSize="sm" fontWeight="medium" color={labelColor}>
                    Verification Code
                  </Text>
                  <Input
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="Enter verification code"
                    required
                    maxLength={6}
                    bg={cardBg}
                    borderColor={inputBorderColor}
                    _hover={{ borderColor: inputHoverBorderColor }}
                    _focus={{ borderColor: inputFocusBorderColor, boxShadow: `0 0 0 1px ${inputFocusBorderColor}` }}
                  />
                </Box>

                <Button
                  type="submit"
                  w="100%"
                  bg="#2C6CB0"
                  color="white"
                  _hover={{ bg: '#23568D' }}
                  isLoading={verifyCodeMutation.isPending}
                  loadingText="Verifying..."
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

          <Box
            bg={infoBoxBg}
            p={4}
            borderRadius="md"
            fontSize="sm"
            color={infoBoxColor}
            borderWidth="1px"
            borderColor={infoBoxBorderColor}
          >
            <HStack spacing={3} align="start">
              <Box
                w={6}
                h={6}
                borderRadius="full"
                bg={iconBg}
                display="flex"
                alignItems="center"
                justifyContent="center"
                color="white"
                fontSize="xs"
                fontWeight="bold"
                flexShrink={0}
              >
                ?
              </Box>
              <Text>
                We will email you a code for a password-free log in. Or you can{' '}
                <Link
                  as="button"
                  onClick={() => setUsePassword(true)}
                  textDecoration="underline"
                  color="#2C6CB0"
                  _dark={{ color: '#4A90E2' }}
                  _hover={{ color: '#23568D', _dark: { color: '#357ABD' } }}
                >
                  log in with a password
                </Link>{' '}
                instead.
              </Text>
            </HStack>
          </Box>
        </VStack>
      </Box>
      </Box>
      <Footer />
    </Flex>
  )
}

