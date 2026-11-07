import request from '@/utils/request'

export const getSearchSuggestions = (keyword, role) =>
  request.get('/api/search/suggest', { params: { keyword, role } })
