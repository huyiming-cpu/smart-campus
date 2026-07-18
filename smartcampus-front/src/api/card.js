import request from '../utils/request'

// 学生一卡通
export const getMyCard = () => request.get('/api/student/card')
// 学生缴费查询
export const getMyPayments = () => request.get('/api/student/payments')
// 管理员一卡通管理
export const listAllCards = () => request.get('/api/admin/cards')
export const addTransaction = (data) => request.post('/api/admin/cards/transaction', data)
export const syncCardData = () => request.post('/api/admin/cards/sync')
// 管理员缴费管理
export const listAllPayments = (params) => request.get('/api/admin/payments', { params })
export const savePayment = (data) => request.post('/api/admin/payments', data)
export const deletePayment = (id) => request.delete(`/api/admin/payments/${id}`)
