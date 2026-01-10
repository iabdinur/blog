import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
  Box,
} from '@chakra-ui/react'
import { useEffect } from 'react'
import { useUIStore } from '@/store/useUIStore'
import { SubscribeForm } from '../newsletter/SubscribeForm'

export const NewsletterPopup = () => {
  const { newsletterPopupOpen, setNewsletterPopupOpen } = useUIStore()
  const { isOpen, onOpen, onClose } = useDisclosure()

  useEffect(() => {
    if (newsletterPopupOpen) {
      onOpen()
    } else {
      onClose()
    }
  }, [newsletterPopupOpen, onOpen, onClose])

  const handleClose = () => {
    setNewsletterPopupOpen(false)
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="xl" isCentered>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Subscribe to my newsletter</ModalHeader>
        <ModalCloseButton />
        <ModalBody pb={6}>
          <Box mb={4}>
            <SubscribeForm onSuccess={handleClose} />
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  )
}

