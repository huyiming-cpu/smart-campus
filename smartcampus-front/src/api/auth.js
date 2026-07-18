import request from '../utils/request'

export function login(formData) {
  return request.post('/api/auth/login', formData)
}

export function register(formData) {
  return request.post('/api/auth/register', formData)
}