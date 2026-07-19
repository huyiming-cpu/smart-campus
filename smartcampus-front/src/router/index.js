import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

// Common
const Login = () => import('../views/common/Login.vue')
const Profile = () => import('../views/common/Profile.vue')
const ComingSoon = () => import('../views/common/ComingSoon.vue')
const AiChat = () => import('../views/common/AiChat.vue')
const NoticeList = () => import('../views/common/NoticeList.vue')
const NewsList = () => import('../views/common/NewsList.vue')
const StudentNotice = () => import('../views/student/StudentNotice.vue')
const StudentStudy = () => import('../views/student/StudentStudy.vue')
const StudentExamCenter = () => import('../views/student/StudentExamCenter.vue')
const StudentWallet = () => import('../views/student/StudentWallet.vue')

// Student
const StudentHome = () => import('../views/student/Student.vue')
const StudentSchedule = () => import('../views/student/StudentSchedule.vue')
const StudentScores = () => import('../views/student/StudentScores.vue')
const StudentExams = () => import('../views/student/StudentExams.vue')
const StudentEvaluation = () => import('../views/student/StudentEvaluation.vue')
const StudentRetake = () => import('../views/student/StudentRetake.vue')
const StudentLevelExam = () => import('../views/student/StudentLevelExam.vue')
const StudentCard = () => import('../views/student/StudentCard.vue')
const StudentPayments = () => import('../views/student/StudentPayments.vue')
const StudentApplications = () => import('../views/student/StudentApplications.vue')
const StudentCompetitions = () => import('../views/student/StudentCompetitions.vue')
const StudentConsultation = () => import('../views/student/StudentConsultation.vue')
const StudentPractice = () => import('../views/student/StudentPractice.vue')
const StudentLabs = () => import('../views/student/StudentLabs.vue')
const StudentGraduation = () => import('../views/student/StudentGraduation.vue')

// Teacher
const TeacherHome = () => import('../views/teacher/Teacher.vue')
const TeacherWorkbench = () => import('../views/teacher/TeacherWorkbench.vue')
const TeacherWorkPlans = () => import('../views/teacher/TeacherWorkPlans.vue')
const TeacherEnrollment = () => import('../views/teacher/TeacherEnrollment.vue')
const TeacherCard = () => import('../views/teacher/TeacherCard.vue')
const TeacherDirectory = () => import('../views/teacher/TeacherDirectory.vue')
const TeacherSchedule = () => import('../views/teacher/TeacherSchedule.vue')
const TeacherScores = () => import('../views/teacher/TeacherScores.vue')
const TeacherEvaluations = () => import('../views/teacher/TeacherEvaluations.vue')
const TeacherResearch = () => import('../views/teacher/TeacherResearch.vue')
const TeacherGraduation = () => import('../views/teacher/TeacherGraduation.vue')
const TeacherDocuments = () => import('../views/teacher/TeacherDocuments.vue')
const TeacherExams = () => import('../views/teacher/TeacherExams.vue')
const TeacherClassRoster = () => import('../views/teacher/TeacherClassRoster.vue')
const TeacherQuick = () => import('../views/teacher/TeacherQuick.vue')
const TeacherApplications = () => import('../views/teacher/TeacherApplications.vue')

// Leader
const LeaderHome = () => import('../views/leader/Leader.vue')
const LeaderDirectory = () => import('../views/leader/LeaderDirectory.vue')
const LeaderWorkPlans = () => import('../views/leader/LeaderWorkPlans.vue')
const LeaderDocuments = () => import('../views/leader/LeaderDocuments.vue')
const LeaderResearch = () => import('../views/leader/LeaderResearch.vue')
const LeaderApplications = () => import('../views/leader/LeaderApplications.vue')
const LeaderStats = () => import('../views/leader/LeaderStats.vue')

// Admin
const AdminHome = () => import('../views/admin/Admin.vue')
const AdminUsers = () => import('../views/admin/AdminUsers.vue')
const AdminRegisterAudit = () => import('../views/admin/AdminRegisterAudit.vue')
const AdminRoles = () => import('../views/admin/AdminRoles.vue')
const AdminAnnouncements = () => import('../views/admin/AdminAnnouncements.vue')
const AdminNews = () => import('../views/admin/AdminNews.vue')
const AdminCourses = () => import('../views/admin/AdminCourses.vue')
const AdminSchedules = () => import('../views/admin/AdminSchedules.vue')
const AdminEnrollment = () => import('../views/admin/AdminEnrollment.vue')
const AdminExams = () => import('../views/admin/AdminExams.vue')
const AdminCards = () => import('../views/admin/AdminCards.vue')
const AdminPayments = () => import('../views/admin/AdminPayments.vue')
const AdminAssets = () => import('../views/admin/AdminAssets.vue')
const AdminLabs = () => import('../views/admin/AdminLabs.vue')

const rolePathMap = {
  STUDENT: '/student', TEACHER: '/teacher',
  LEADER: '/leader', ADMIN: '/admin'
}

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/common/ai', component: AiChat },

  // ==================== 学生端 ====================
  { path: '/student', component: StudentHome, meta: { role: 'STUDENT' },
    children: [
      { path: 'profile', component: Profile },
      { path: 'notice', component: StudentNotice },
      { path: 'news', component: NewsList },
      { path: 'study', component: StudentStudy },
      { path: 'exams', component: StudentExamCenter },
      { path: 'wallet', component: StudentWallet },
      { path: 'applications', component: StudentApplications },
      { path: 'competitions', component: StudentCompetitions },
      { path: 'consultation', component: StudentConsultation },
      { path: 'practice', component: StudentPractice },
      { path: 'scores', component: StudentScores },
      { path: 'schedule', component: StudentSchedule },
      { path: 'labs', component: StudentLabs },
      { path: 'graduation', component: StudentGraduation },
      { path: 'quick', component: StudentApplications },
    ] },

  // ==================== 教师端 ====================
  { path: '/teacher', component: TeacherHome, meta: { role: 'TEACHER' },
    children: [
      { path: 'profile', component: Profile },
      { path: 'announcement', component: NoticeList },
      { path: 'news', component: NewsList },
      { path: 'workbench', component: TeacherWorkbench },
      { path: 'work-plans', component: TeacherWorkPlans },
      { path: 'enrollment', component: TeacherEnrollment },
      { path: 'card', component: TeacherCard },
      { path: 'directory', component: TeacherDirectory },
      { path: 'schedule', component: TeacherSchedule },
      { path: 'scores', component: TeacherScores },
      { path: 'evaluation', component: TeacherEvaluations },
      { path: 'research', component: TeacherResearch },
      { path: 'graduation', component: TeacherGraduation },
      { path: 'documents', component: TeacherDocuments },
      { path: 'exams', component: TeacherExams },
      { path: 'students', component: TeacherClassRoster },
      { path: 'awards', component: TeacherApplications },
      { path: 'quick', component: TeacherQuick },
    ] },

  // ==================== 领导端 ====================
  { path: '/leader', component: LeaderHome, meta: { role: 'LEADER' },
    children: [
      { path: 'profile', component: Profile },
      { path: 'announcement', component: NoticeList },
      { path: 'news', component: NewsList },
      { path: 'directory', component: LeaderDirectory },
      { path: 'work-plans', component: LeaderWorkPlans },
      { path: 'documents', component: LeaderDocuments },
      { path: 'research', component: LeaderResearch },
      { path: 'applications', component: LeaderApplications },
      { path: 'stats', component: LeaderStats },
    ] },

  // ==================== 管理员端 ====================
  { path: '/admin', component: AdminHome, meta: { role: 'ADMIN' },
    children: [
      { path: 'profile', component: Profile },
      { path: 'announcements', component: AdminAnnouncements },
      { path: 'news', component: AdminNews },
      { path: 'users', component: AdminUsers },
      { path: 'register-audit', component: AdminRegisterAudit },
      { path: 'roles', component: AdminRoles },
      { path: 'courses', component: AdminCourses },
      { path: 'schedules', component: AdminSchedules },
      { path: 'enrollment', component: AdminEnrollment },
      { path: 'exams', component: AdminExams },
      { path: 'cards', component: AdminCards },
      { path: 'payments', component: AdminPayments },
      { path: 'assets', component: AdminAssets },
      { path: 'labs', component: AdminLabs },
    ] },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  if (!token) return to.path === '/login' ? next() : next('/login')
  if (to.path === '/login') return next(rolePathMap[userStore.role] || '/student')
  if (to.path.startsWith('/common/')) return next()
  const allowed = rolePathMap[userStore.role]
  if (allowed && !to.path.startsWith(allowed)) return next(allowed)
  next()
})

export { rolePathMap }
export default router
