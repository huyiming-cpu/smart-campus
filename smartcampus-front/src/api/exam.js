import request from '../utils/request'

// 考试安排
export const getExamArrangements = (semester) => request.get('/api/student/exams', { params: { semester } })
export const getSupervisorExams = (semester) => request.get('/api/teacher/exams', { params: { semester } })
// 评教
export const getMyEvaluations = (semester) => request.get('/api/student/evaluations', { params: { semester } })
export const submitEvaluation = (data) => request.post('/api/student/evaluations', data)
export const getTeacherEvaluations = (semester) => request.get('/api/teacher/evaluations', { params: { semester } })
export const getEvaluationStats = () => request.get('/api/leader/evaluation-stats')
// 等级考试
export const listLevelExams = (status) => request.get('/api/student/level-exams', { params: { status } })
export const registerLevelExam = (examId) => request.post('/api/student/level-exams/register', { examId })
export const getMyExamRegistrations = () => request.get('/api/student/level-exams/my')
// 管理员
export const saveExamArrangement = (data) => request.post('/api/admin/exams', data)
export const updateExamArrangement = (id, data) => request.put(`/api/admin/exams/${id}`, data)
export const deleteExamArrangement = (id) => request.delete(`/api/admin/exams/${id}`)
export const saveLevelExam = (data) => request.post('/api/admin/level-exams', data)
export const deleteLevelExam = (id) => request.delete(`/api/admin/level-exams/${id}`)
