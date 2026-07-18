import request from '../utils/request'

// 学生申请(奖学金/助学金/贷款/困难生)
export const listMyApplications = (type) => request.get('/api/student/applications', { params: { type } })
export const submitApplication = (data) => request.post('/api/student/applications', data)
// 教师审核
export const listPendingApplications = () => request.get('/api/teacher/applications/pending')
export const reviewApplication = (id, data) => request.put(`/api/teacher/applications/${id}/review`, data)

// 竞赛报名
export const listMyCompetitions = () => request.get('/api/student/competitions')
export const registerCompetition = (data) => request.post('/api/student/competitions', data)

// 心理咨询
export const listMyConsultations = () => request.get('/api/student/consultations')
export const bookConsultation = (data) => request.post('/api/student/consultations', data)

// 实践项目
export const listMyPractices = () => request.get('/api/student/practices')
export const registerPractice = (data) => request.post('/api/student/practices', data)
