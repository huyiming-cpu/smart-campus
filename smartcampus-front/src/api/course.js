import request from '../utils/request'

// 学生课表
export const getStudentSchedule = (semester) => request.get('/api/student/schedule', { params: { semester } })
// 课程总览
export const getCourseOverview = () => request.get('/api/student/course-overview')
// 成绩查询
export const getMyScores = (semester) => request.get('/api/student/scores', { params: { semester } })
// 补考重修列表
export const getMyRetakes = () => request.get('/api/student/retakes')
// 补考重修申请
export const applyRetake = (data) => request.post('/api/student/retakes', data)
// 教师审批补考
export const approveRetake = (id, status) => request.put(`/api/teacher/retakes/${id}/approve`, { status })

// 教师任课
export const getTeacherSchedule = (semester) => request.get('/api/teacher/schedule', { params: { semester } })
// 班级名单
export const getClassRoster = (scheduleId) => request.get(`/api/teacher/roster/${scheduleId}`)
// 成绩录入
export const enterScore = (studentCourseId, data) => request.put(`/api/teacher/scores/${studentCourseId}`, data)

// 管理员课程管理
export const listCourses = (params) => request.get('/api/admin/courses', { params })
export const saveCourse = (data) => request.post('/api/admin/courses', data)
export const updateCourse = (id, data) => request.put(`/api/admin/courses/${id}`, data)
export const deleteCourse = (id) => request.delete(`/api/admin/courses/${id}`)
// 排课管理
export const listSchedules = (semester) => request.get('/api/admin/schedules', { params: { semester } })
export const saveSchedule = (data) => request.post('/api/admin/schedules', data)
export const deleteSchedule = (id) => request.delete(`/api/admin/schedules/${id}`)
// 选课管理
export const listStudentCourses = (params) => request.get('/api/admin/student-courses', { params })
export const enrollStudent = (data) => request.post('/api/admin/enroll', data)
export const dropStudent = (id) => request.put(`/api/admin/student-courses/${id}/drop`)
