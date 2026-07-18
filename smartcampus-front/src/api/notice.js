import request from '../utils/request'

// 公告列表（按角色）
export const listAnnouncements = () => request.get('/api/notice/announcements')
// 公告详情
export const getAnnouncement = (id) => request.get(`/api/notice/announcements/${id}`)
// 管理员：查所有公告
export const listAllAnnouncements = (keyword) => request.get('/api/admin/announcements', { params: { keyword } })
// 管理员：创建公告
export const createAnnouncement = (data) => request.post('/api/admin/announcements', data)
// 管理员：更新公告
export const updateAnnouncement = (id, data) => request.put(`/api/admin/announcements/${id}`, data)
// 管理员：删除公告
export const deleteAnnouncement = (id) => request.delete(`/api/admin/announcements/${id}`)
// 管理员：置顶切换
export const toggleTop = (id) => request.put(`/api/admin/announcements/${id}/top`)

// 新闻列表（支持分类筛选）
export const listNews = (category) => request.get('/api/notice/news', { params: { category } })
// 新闻详情
export const getNews = (id) => request.get(`/api/notice/news/${id}`)
// 管理员：创建新闻
export const createNews = (data) => request.post('/api/admin/news', data)
// 管理员：更新新闻
export const updateNews = (id, data) => request.put(`/api/admin/news/${id}`, data)
// 管理员：删除新闻
export const deleteNews = (id) => request.delete(`/api/admin/news/${id}`)
