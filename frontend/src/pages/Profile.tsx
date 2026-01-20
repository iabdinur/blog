import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  VStack,
  HStack,
  Heading,
  FormControl,
  FormLabel,
  Input,
  Button,
  IconButton,
  Stack,
  Text,
  Center,
  useToast,
  useColorModeValue,
  Divider,
  Alert,
  AlertIcon,
  Spinner,
} from '@chakra-ui/react'
import { Avatar } from '@/components/ui/Avatar'
import { FaDownload, FaEdit, FaSave, FaTimes, FaCamera } from 'react-icons/fa'
import { IoMdSave } from 'react-icons/io'
import { Layout } from '@/components/layout/Layout'
import { apiClient, changePassword } from '@/api/client'
import { uploadUserProfileImage, getUserProfileImage, deleteUserProfileImage } from '@/api/client'
import { colorPalettes } from '@/compositions/lib/color-palettes'

interface UserProfile {
  id: string
  name: string
  email: string
  userType?: string // 'REA' or 'AUT'
  profileImageId: string | null
  createdAt: string
  updatedAt: string
}

export const Profile = () => {
  const navigate = useNavigate()
  const toast = useToast()
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [uploading, setUploading] = useState(false)
  
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [avatarFile, setAvatarFile] = useState<File | null>(null)
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const [avatarUrl, setAvatarUrl] = useState<string | null>(null)
  const [isEditingUserInfo, setIsEditingUserInfo] = useState(false)

  const bg = useColorModeValue('white', 'gray.800')
  const borderColor = useColorModeValue('gray.200', 'gray.700')
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const labelColor = useColorModeValue('gray.700', 'gray.300')
  const inputBg = useColorModeValue('white', 'gray.700')

  useEffect(() => {
    const loadUserProfile = async () => {
      try {
        const token = localStorage.getItem('auth_token')
        if (!token) {
          navigate('/login')
          return
        }

        // Extract email from JWT token
        const parts = token.split('.')
        if (parts.length !== 3) {
          navigate('/login')
          return
        }

        const payload = JSON.parse(atob(parts[1]))
        const userEmail = payload.sub || payload.username

        if (!userEmail) {
          navigate('/login')
          return
        }

        // Fetch user data
        const response = await apiClient.get(`/users/${encodeURIComponent(userEmail)}`)
        const userData: UserProfile = response.data
        setUser(userData)
        setName(userData.name)
        setEmail(userData.email)

        // Load profile image if exists
        if (userData.profileImageId) {
          try {
            const imageBlob = await getUserProfileImage(userEmail)
            const imageUrl = URL.createObjectURL(imageBlob)
            setAvatarUrl(imageUrl)
          } catch (error) {
            // Profile image not found or error loading
            console.error('Failed to load profile image:', error)
          }
        }
      } catch (error: any) {
        console.error('Failed to load profile:', error)
        if (error.response?.status === 401) {
          navigate('/login')
        } else {
          toast({
            title: 'Failed to load profile',
            description: error.response?.data?.message || 'Please try again later',
            status: 'error',
            duration: 3000,
          })
        }
      } finally {
        setLoading(false)
      }
    }

    loadUserProfile()
  }, [navigate, toast])

  // Cleanup: revoke object URLs on unmount
  useEffect(() => {
    return () => {
      if (avatarUrl) {
        URL.revokeObjectURL(avatarUrl)
      }
    }
  }, [avatarUrl])

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setAvatarFile(file)
      const reader = new FileReader()
      reader.onloadend = () => {
        setAvatarPreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleUploadImage = async () => {
    if (!avatarFile || !user) return

    setUploading(true)
    try {
      await uploadUserProfileImage(user.email, avatarFile)
      toast({
        title: 'Profile image updated',
        status: 'success',
        duration: 3000,
      })
      setAvatarFile(null)
      // Reload profile image
      try {
        const imageBlob = await getUserProfileImage(user.email)
        const imageUrl = URL.createObjectURL(imageBlob)
        if (avatarUrl) {
          URL.revokeObjectURL(avatarUrl)
        }
        setAvatarUrl(imageUrl)
      } catch (error) {
        console.error('Failed to reload profile image:', error)
      }
    } catch (error: any) {
      toast({
        title: 'Failed to upload image',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    } finally {
      setUploading(false)
    }
  }

  const handleDownloadImage = async () => {
    if (!user || !user.profileImageId) {
      toast({
        title: 'No profile image',
        description: 'You don\'t have a profile image to download',
        status: 'warning',
        duration: 3000,
      })
      return
    }

    try {
      const imageBlob = await getUserProfileImage(user.email)
      const url = URL.createObjectURL(imageBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `profile-image-${user.email}.jpg`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      toast({
        title: 'Image downloaded',
        status: 'success',
        duration: 2000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to download image',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    }
  }

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!user) return

    setSaving(true)
    try {
      // Update profile image if changed
      if (avatarFile) {
        await handleUploadImage()
      }
      
      // Update name/email if changed
      const hasChanges = name !== user.name || email !== user.email
      if (hasChanges) {
        await apiClient.put(`/users/${encodeURIComponent(user.email)}`, {
          name: name.trim(),
          email: email.trim(),
        })
        // Reload user data
        const response = await apiClient.get(`/users/${encodeURIComponent(email.trim())}`)
        setUser(response.data)
        setName(response.data.name)
        setEmail(response.data.email)
      }
      
      setIsEditingUserInfo(false)
      setIsEditingUserInfo(false)
      toast({
        title: 'Profile updated',
        status: 'success',
        duration: 3000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to update profile',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    } finally {
      setSaving(false)
    }
  }

  const handleCancelEdit = () => {
    if (user) {
      setName(user.name)
      setEmail(user.email)
    }
    setIsEditingUserInfo(false)
  }

  const handleRemoveImage = async () => {
    if (!user) return

    try {
      await deleteUserProfileImage(user.email)
      
      // Clear local state
      if (avatarUrl) {
        URL.revokeObjectURL(avatarUrl)
      }
      setAvatarUrl(null)
      setAvatarPreview(null)
      setAvatarFile(null)
      
      // Reload user data
      const response = await apiClient.get(`/users/${encodeURIComponent(user.email)}`)
      setUser(response.data)
      
      toast({
        title: 'Profile image removed',
        status: 'success',
        duration: 3000,
      })
    } catch (error: any) {
      toast({
        title: 'Failed to remove image',
        description: error.response?.data?.message || 'Please try again',
        status: 'error',
        duration: 3000,
      })
    }
  }

  if (loading) {
    return (
      <Layout>
        <Center py={8}>
          <Spinner size="xl" />
        </Center>
      </Layout>
    )
  }

  if (!user) {
    return (
      <Layout>
        <Center py={8}>
          <Alert status="error">
            <AlertIcon />
            User not found
          </Alert>
        </Center>
      </Layout>
    )
  }

  return (
    <Layout>
      <Box maxW="800px" mx="auto" py={8}>
        <VStack spacing={8} align="stretch">
          <Heading size="lg" color={textColor}>Profile Settings</Heading>

          <Box bg={bg} p={6} borderRadius="lg" borderWidth="1px" borderColor={borderColor}>
            <VStack spacing={6} align="stretch">
              {/* Profile Image Section */}
              <FormControl>
                <HStack justify="space-between" align="center" mb={4}>
                  <FormLabel color={labelColor} mb={0}>Profile Image</FormLabel>
                  <Input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    display="none"
                    id="profile-avatar-upload"
                  />
                  {avatarFile ? (
                    <HStack spacing={2}>
                      <Button
                        leftIcon={<FaSave />}
                        size="sm"
                        cursor="pointer"
                        variant="ghost"
                        onClick={handleUploadImage}
                        isLoading={uploading}
                      >
                        Save
                      </Button>
                      <IconButton
                        aria-label="Cancel upload"
                        icon={<FaTimes />}
                        variant="ghost"
                        size="sm"
                        onClick={() => {
                          setAvatarFile(null)
                          setAvatarPreview(null)
                        }}
                      />
                    </HStack>
                  ) : (
                    <HStack spacing={2}>
                      <Button
                        as="label"
                        htmlFor="profile-avatar-upload"
                        leftIcon={<FaCamera />}
                        size="sm"
                        cursor="pointer"
                        variant="ghost"
                      >
                        {avatarPreview || avatarUrl ? 'Edit' : 'Add'}
                      </Button>
                      {avatarUrl && (
                        <IconButton
                          aria-label="Remove profile image"
                          icon={<FaTimes />}
                          variant="ghost"
                          size="sm"
                          onClick={handleRemoveImage}
                        />
                      )}
                    </HStack>
                  )}
                </HStack>
                <VStack spacing={4} align="stretch">
                  <Center>
                    <Stack direction="column" align="center" spacing={4}>
                      <Avatar
                        size="2xl"
                        src={avatarPreview || avatarUrl || undefined}
                        name={user.name}
                        boxSize="160px"
                      />
                      <Text fontSize="md" fontWeight="bold" color={colorPalettes.grey[600]} textAlign="center">
                        {user.name}
                      </Text>
                    </Stack>
                  </Center>
                {avatarUrl && !avatarFile && (
                  <HStack justify="center" mt={4} spacing={3}>
                    <Button
                      size="sm"
                      variant="outline"
                      leftIcon={<FaDownload />}
                      onClick={handleDownloadImage}
                    >
                      Download Image
                    </Button>
                  </HStack>
                )}
                </VStack>
              </FormControl>

              <Divider />

              {/* User Information Section */}
              <FormControl>
                <HStack justify="space-between" align="center" mb={4}>
                  <FormLabel color={labelColor} mb={0}>User Information</FormLabel>
                  {!isEditingUserInfo ? (
                    <Button
                      leftIcon={<FaEdit />}
                      size="sm"
                      cursor="pointer"
                      variant="ghost"
                      onClick={() => setIsEditingUserInfo(true)}
                    >
                      Edit
                    </Button>
                  ) : (
                    <HStack spacing={2}>
                      <Button
                        leftIcon={<IoMdSave size={20} />}
                        size="sm"
                        cursor="pointer"
                        variant="ghost"
                        onClick={(e) => {
                          e.preventDefault()
                          const formEvent = { ...e, preventDefault: () => {} } as React.FormEvent
                          handleSave(formEvent)
                        }}
                        isLoading={saving}
                        isDisabled={name === user.name && email === user.email}
                      >
                        Save
                      </Button>
                      <IconButton
                        aria-label="Cancel"
                        icon={<FaTimes />}
                        variant="ghost"
                        size="sm"
                        onClick={handleCancelEdit}
                      />
                    </HStack>
                  )}
                </HStack>
                <form onSubmit={handleSave}>
                  <VStack spacing={4} align="stretch">
                    <FormControl>
                      <FormLabel color={labelColor}>Name</FormLabel>
                      <Input
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        bg={inputBg}
                        color={textColor}
                        isDisabled={!isEditingUserInfo}
                        opacity={isEditingUserInfo ? 1 : 0.6}
                      />
                    </FormControl>

                    <FormControl>
                      <FormLabel color={labelColor}>Email</FormLabel>
                      <Input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        bg={inputBg}
                        color={textColor}
                        isDisabled={!isEditingUserInfo}
                        opacity={isEditingUserInfo ? 1 : 0.6}
                      />
                    </FormControl>
                  </VStack>
                </form>
              </FormControl>
            </VStack>
          </Box>

          {/* Password Change Section */}
          <Box bg={bg} p={6} borderRadius="lg" borderWidth="1px" borderColor={borderColor}>
            <PasswordChangeForm userEmail={user.email} />
          </Box>

          {/* Account Information */}
          <Box bg={bg} p={6} borderRadius="lg" borderWidth="1px" borderColor={borderColor}>
            <VStack spacing={4} align="stretch">
              <Heading size="sm" color={textColor}>Account Information</Heading>
              <VStack align="start" spacing={2} fontSize="sm">
                <HStack>
                  <Text fontWeight="medium" color={labelColor}>Account Created:</Text>
                  <Text color={textColor}>
                    {new Date(user.createdAt).toLocaleDateString()}
                  </Text>
                </HStack>
                <HStack>
                  <Text fontWeight="medium" color={labelColor}>Last Updated:</Text>
                  <Text color={textColor}>
                    {new Date(user.updatedAt).toLocaleDateString()}
                  </Text>
                </HStack>
              </VStack>
            </VStack>
          </Box>
        </VStack>
      </Box>
    </Layout>
  )
}

const PasswordChangeForm = ({ userEmail }: { userEmail: string }) => {
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isChanging, setIsChanging] = useState(false)
  const [isEditingPassword, setIsEditingPassword] = useState(false)
  const toast = useToast()
  const textColor = useColorModeValue('gray.900', 'gray.100')
  const labelColor = useColorModeValue('gray.700', 'gray.300')
  const inputBg = useColorModeValue('white', 'gray.700')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (newPassword !== confirmPassword) {
      toast({
        title: 'Passwords do not match',
        status: 'error',
        duration: 3000,
      })
      return
    }

    if (newPassword.length < 8) {
      toast({
        title: 'Password too short',
        description: 'Password must be at least 8 characters',
        status: 'error',
        duration: 3000,
      })
      return
    }

    setIsChanging(true)
    try {
      await changePassword(userEmail, currentPassword, newPassword)
      toast({
        title: 'Password changed successfully',
        status: 'success',
        duration: 3000,
      })
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
      setIsEditingPassword(false)
    } catch (error: any) {
      toast({
        title: 'Failed to change password',
        description: error.response?.data?.message || 'Please check your current password and try again',
        status: 'error',
        duration: 3000,
      })
    } finally {
      setIsChanging(false)
    }
  }

  const handleCancelPasswordEdit = () => {
    setCurrentPassword('')
    setNewPassword('')
    setConfirmPassword('')
    setIsEditingPassword(false)
  }

  return (
    <FormControl>
      <HStack justify="space-between" align="center" mb={4}>
        <FormLabel color={labelColor} mb={0} fontSize="md" fontWeight="semibold">Change Password</FormLabel>
        {!isEditingPassword ? (
          <Button
            leftIcon={<FaEdit />}
            size="sm"
            cursor="pointer"
            variant="ghost"
            onClick={() => setIsEditingPassword(true)}
          >
            Edit
          </Button>
        ) : (
          <HStack spacing={2}>
            <Button
              leftIcon={<IoMdSave size={20} />}
              size="sm"
              cursor="pointer"
              variant="ghost"
              onClick={(e) => {
                e.preventDefault()
                const formEvent = { ...e, preventDefault: () => {} } as React.FormEvent
                handleSubmit(formEvent)
              }}
              isLoading={isChanging}
              isDisabled={!currentPassword || !newPassword || !confirmPassword}
            >
              Save
            </Button>
            <IconButton
              aria-label="Cancel"
              icon={<FaTimes />}
              variant="ghost"
              size="sm"
              onClick={handleCancelPasswordEdit}
            />
          </HStack>
        )}
      </HStack>
      <form onSubmit={handleSubmit}>
        <VStack spacing={4} align="stretch">
          <FormControl>
            <FormLabel color={labelColor}>Current Password</FormLabel>
            <Input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              bg={inputBg}
              color={textColor}
              isDisabled={!isEditingPassword}
              opacity={isEditingPassword ? 1 : 0.6}
              required
            />
          </FormControl>

          <FormControl>
            <FormLabel color={labelColor}>New Password</FormLabel>
            <Input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              bg={inputBg}
              color={textColor}
              isDisabled={!isEditingPassword}
              opacity={isEditingPassword ? 1 : 0.6}
              required
              minLength={8}
            />
          </FormControl>

          <FormControl>
            <FormLabel color={labelColor}>Confirm New Password</FormLabel>
            <Input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              bg={inputBg}
              color={textColor}
              isDisabled={!isEditingPassword}
              opacity={isEditingPassword ? 1 : 0.6}
              required
              minLength={8}
            />
          </FormControl>
        </VStack>
      </form>
    </FormControl>
  )
}
