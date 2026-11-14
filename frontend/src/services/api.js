import axios from 'axios';

const rawBaseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
const baseURL = String(rawBaseURL).replace(/\/+$/, '');

const api = axios.create({
  baseURL,
  headers: {
    Accept: 'application/json',
  },
  timeout: 20000,
});

export function createCrudService(resource) {
  return {
    resource,
    list: (params = {}) => api.get(`/${resource}`, { params }),
    get: (id) => api.get(`/${resource}/${id}`),
    create: (data) => api.post(`/${resource}`, data),
    update: (id, data) => api.put(`/${resource}/${id}`, data),
    remove: (id) => api.delete(`/${resource}/${id}`),
  };
}

export default api;
