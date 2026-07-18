import request from '../utils/request'

export function getProfile() {
  return request.get('/api/user/profile')
}

export function updateProfile(data) {
  return request.put('/api/user/profile', data)
}
export function changePassword(oldPassword, newPassword) {
  return request.put('/api/user/change-password', { oldPassword, newPassword })
}
