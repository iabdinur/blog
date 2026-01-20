import axios from 'axios'

// Get base URL from env or use default, and ensure it includes /api/v1
const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const API_BASE_URL = baseUrl.endsWith('/api/v1') ? baseUrl : `${baseUrl}/api/v1`

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      // Only redirect if we're on an authors panel page
      if (window.location.pathname.startsWith('/authors')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// User profile image functions
export const uploadUserProfileImage = async (email: string, file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return apiClient.post(
    `/users/${encodeURIComponent(email)}/profile-image`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  )
}

export const updateUser = async (email: string, data: { name?: string; email?: string }) => {
  const response = await apiClient.put(`/users/${encodeURIComponent(email)}`, data)
  return response.data
}

export const changePassword = async (email: string, currentPassword: string, newPassword: string) => {
  await apiClient.put(`/users/${encodeURIComponent(email)}/password`, {
    currentPassword,
    newPassword,
  })
}

export const getUserProfileImage = async (email: string): Promise<Blob> => {
  const response = await apiClient.get(`/users/${encodeURIComponent(email)}/profile-image`, {
    responseType: 'blob',
  })
  return response.data
}

export const deleteUserProfileImage = async (email: string) => {
  await apiClient.delete(`/users/${encodeURIComponent(email)}/profile-image`)
}
