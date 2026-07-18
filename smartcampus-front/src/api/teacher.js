import request from '../utils/request'

// 科研管理
export const listMyResearch = () => request.get('/api/teacher/research')
export const saveResearch = (data) => request.post('/api/teacher/research', data)
export const updateResearch = (id, data) => request.put(`/api/teacher/research/${id}`, data)
export const deleteResearch = (id) => request.delete(`/api/teacher/research/${id}`)
// 领导查看科研总览
export const listAllResearch = (status) => request.get('/api/leader/research', { params: { status } })

// 毕设管理
export const listMyDesigns = () => request.get('/api/teacher/graduation-design')
export const getMyDesign = () => request.get('/api/student/graduation-design')
export const updateDesign = (id, data) => request.put(`/api/teacher/graduation-design/${id}`, data)
