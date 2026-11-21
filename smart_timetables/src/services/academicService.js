import apiClient from '../api/client';

export const academicService = {
  // Academic Years
  getAcademicYears: async (params = {}) => {
    const response = await apiClient.get('/academic-years', { params });
    return response.data;
  },
  getActiveAcademicYear: async () => {
    const response = await apiClient.get('/academic-years/active');
    return response.data;
  },
  createAcademicYear: async (data) => {
    const response = await apiClient.post('/academic-years', data);
    return response.data;
  },
  updateAcademicYear: async (id, data) => {
    const response = await apiClient.put(`/academic-years/${id}`, data);
    return response.data;
  },
  deleteAcademicYear: async (id) => {
    await apiClient.delete(`/academic-years/${id}`);
  },

  // Terms
  getTerms: async (params = {}) => {
    const response = await apiClient.get('/terms', { params });
    return response.data;
  },
  getTermsByAcademicYear: async (academicYearId) => {
    const response = await apiClient.get('/terms', { params: { size: 100 } });
    if (response.data?.content) {
      return response.data.content.filter((t) => t.academicYearId === academicYearId);
    }
    return [];
  },
  createTerm: async (data) => {
    const response = await apiClient.post('/terms', data);
    return response.data;
  },
  updateTerm: async (id, data) => {
    const response = await apiClient.put(`/terms/${id}`, data);
    return response.data;
  },
  deleteTerm: async (id) => {
    await apiClient.delete(`/terms/${id}`);
  },
};

export default academicService;
