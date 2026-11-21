import apiClient from '../api/client';

export const teacherService = {
  getTeachers: async (params = {}) => {
    const response = await apiClient.get('/teachers', {
      params: { size: 50, sort: 'fullName,asc', ...params },
    });
    return response.data;
  },
  getTeacherById: async (id) => {
    const response = await apiClient.get(`/teachers/${id}`);
    return response.data;
  },
  createTeacher: async (data) => {
    const response = await apiClient.post('/teachers', data);
    return response.data;
  },
  updateTeacher: async (id, data) => {
    const response = await apiClient.put(`/teachers/${id}`, data);
    return response.data;
  },
  deleteTeacher: async (id) => {
    await apiClient.delete(`/teachers/${id}`);
  },

  // Subjects qualifications
  assignSubject: async (teacherId, subjectId, mediumId) => {
    const response = await apiClient.post(`/teachers/${teacherId}/subjects`, {
      subjectId,
      mediumId,
    });
    return response.data;
  },
  removeSubject: async (teacherId, subjectId, mediumId) => {
    await apiClient.delete(`/teachers/${teacherId}/subjects/${subjectId}/mediums/${mediumId}`);
  },

  // Availability
  setAvailability: async (teacherId, timeSlotId, kind) => {
    const response = await apiClient.post(`/teachers/${teacherId}/availability`, {
      timeSlotId,
      kind,
    });
    return response.data;
  },
  removeAvailability: async (teacherId, timeSlotId) => {
    await apiClient.delete(`/teachers/${teacherId}/availability/${timeSlotId}`);
  },
};

export default teacherService;
