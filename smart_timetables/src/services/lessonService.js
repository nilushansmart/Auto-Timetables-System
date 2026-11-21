import apiClient from '../api/client';

export const lessonService = {
  getLessons: async (academicYearId = null, params = {}) => {
    const queryParams = { ...params };
    if (academicYearId) {
      queryParams.academicYearId = academicYearId;
    }
    const response = await apiClient.get('/lessons', { params: queryParams });
    return response.data;
  },
  createLesson: async (data) => {
    const response = await apiClient.post('/lessons', data);
    return response.data;
  },
  updateLesson: async (id, data) => {
    const response = await apiClient.put(`/lessons/${id}`, data);
    return response.data;
  },
  deleteLesson: async (id) => {
    await apiClient.delete(`/lessons/${id}`);
  },

  // Lesson Groups
  getLessonGroups: async (params = {}) => {
    const response = await apiClient.get('/lesson-groups', { params: { size: 50, ...params } });
    return response.data;
  },
  createLessonGroup: async (data) => {
    const response = await apiClient.post('/lesson-groups', data);
    return response.data;
  },
  deleteLessonGroup: async (id) => {
    await apiClient.delete(`/lesson-groups/${id}`);
  },
};

export default lessonService;
