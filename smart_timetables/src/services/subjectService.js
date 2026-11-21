import apiClient from '../api/client';

export const subjectService = {
  getSubjects: async (params = {}) => {
    const response = await apiClient.get('/subjects', {
      params: { size: 50, sort: 'code,asc', ...params },
    });
    return response.data;
  },
  createSubject: async (data) => {
    const response = await apiClient.post('/subjects', data);
    return response.data;
  },
  updateSubject: async (id, data) => {
    const response = await apiClient.put(`/subjects/${id}`, data);
    return response.data;
  },
  deleteSubject: async (id) => {
    await apiClient.delete(`/subjects/${id}`);
  },
};

export default subjectService;
