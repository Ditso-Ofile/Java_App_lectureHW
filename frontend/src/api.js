import axios from 'axios'

// All calls go to the Spring Boot REST API (TownApiController).
const api = axios.create({ baseURL: '/api' })

export const getCounties = () => api.get('/counties').then(r => r.data)
export const getTowns = () => api.get('/towns').then(r => r.data)
export const createTown = (town) => api.post('/towns', town).then(r => r.data)
export const updateTown = (id, town) => api.put(`/towns/${id}`, town).then(r => r.data)
export const deleteTown = (id) => api.delete(`/towns/${id}`)

/** Turns an Axios error into a readable message (uses the server's {"error": "..."} answer). */
export const errorText = (err) =>
  err?.response?.data?.error ?? err?.message ?? 'Unknown error'
