import axios from 'axios'
// 观察到：所有业务接口前缀为 /gateway/wms，响应信封 {code:200,data,msg}
const request = axios.create({ baseURL: '/gateway/wms', timeout: 30000 })
request.interceptors.request.use(c => { const t = localStorage.getItem('token'); if (t) c.headers.Authorization = t; return c })
request.interceptors.response.use(r => {
  if (r.data && r.data.code !== 200) return Promise.reject(new Error(r.data.msg || 'error'))
  return r.data.data
})
export default request
