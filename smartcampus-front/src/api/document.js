import request from '../utils/request'

// 教师公文
export const listMyDocuments = () => request.get('/api/teacher/documents/my')
export const submitDocument = (data) => request.post('/api/teacher/documents', data)
export const deleteDocument = (id) => request.delete(`/api/teacher/documents/${id}`)

// 领导公文审批
export const listPendingDocuments = () => request.get('/api/leader/documents/pending')
export const listApprovedDocuments = () => request.get('/api/leader/documents/approved')
export const approveDocument = (id, data) => request.put(`/api/leader/documents/${id}/approve`, data)

// 工作计划
export const listWorkPlans = (params) => request.get('/api/leader/work-plans', { params })
export const createWorkPlan = (data) => request.post('/api/leader/work-plans', data)
export const updateWorkPlan = (id, data) => request.put(`/api/leader/work-plans/${id}`, data)
export const deleteWorkPlan = (id) => request.delete(`/api/leader/work-plans/${id}`)
