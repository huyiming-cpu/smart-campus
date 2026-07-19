import request from '../utils/request'

// 仪表盘
export const getDashboard = () => request.get('/api/admin/dashboard')
// 日志
export const listLoginLogs = (params) => request.get('/api/admin/logs/login', { params })
// 资产
export const listAssets = (department) => request.get('/api/admin/assets', { params: { department } })
export const saveAsset = (data) => request.post('/api/admin/assets', data)
export const deleteAsset = (id) => request.delete(`/api/admin/assets/${id}`)
export const getAssetStats = () => request.get('/api/leader/assets/stats')
// 实验室
export const listLabs = (params) => request.get('/api/admin/labs', { params })
export const saveLab = (data) => request.post('/api/admin/labs', data)
export const deleteLab = (id) => request.delete(`/api/admin/labs/${id}`)
// 领导统计
export const getEnrollmentStats = () => request.get('/api/leader/statistics/enrollment')
export const getPaymentStats = () => request.get('/api/leader/statistics/payment')
export const getStudentCountStats = () => request.get('/api/leader/statistics/student-count')
