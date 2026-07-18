import request from '../utils/request'

export const chat = (question, sessionId) => request.post('/api/ai/chat', { question, sessionId: sessionId || 'default' })
export const getChatHistory = () => request.get('/api/ai/history')
// 管理员知识库
export const listKnowledge = () => request.get('/api/admin/ai/knowledge')
export const saveKnowledge = (data) => request.post('/api/admin/ai/knowledge', data)
export const deleteKnowledge = (id) => request.delete(`/api/admin/ai/knowledge/${id}`)
