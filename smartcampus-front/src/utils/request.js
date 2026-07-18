import axios from 'axios'
const service = axios.create({
  baseURL: '',
  timeout: 10000
})
// 请求拦截自动携带token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if(token) config.headers.Authorization = token
  return config
})
// 响应拦截统一处理401未登录、报错
service.interceptors.response.use(res=>res.data, err=>{
  if(err.response?.status === 401){
    localStorage.clear()
    location.href = '/login'
  }
  return Promise.reject(err)
})
export default service