import request from '../utils/request'

// 用户管理
export const listAllUsers = () => request.get('/api/admin/users')
export const updateUserStatus = (id, status) => request.put(`/api/admin/users/${id}/status`, { status })
export const resetPassword = (id, password) => request.put(`/api/admin/users/${id}/reset-password`, { password })
export const deleteUser = (id) => request.delete(`/api/admin/users/${id}`)
// 注册审核
export const listRegisterApplications = (status) => request.get('/api/admin/register-applications', { params: { status } })
export const auditRegistration = (id, data) => request.put(`/api/admin/register-applications/${id}/audit`, data)
export const countPendingRegistrations = () => request.get('/api/admin/register-applications/count')
// 角色
export const getAllRoles = () => request.get('/api/admin/roles')
// 通讯录
export const getDirectory = (department) => request.get('/api/leader/directory', { params: { department } })
