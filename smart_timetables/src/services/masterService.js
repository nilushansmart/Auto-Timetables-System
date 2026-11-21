import apiClient from '../api/client';

export const masterService = {
  // Grades
  getGrades: async (params = {}) => {
    const response = await apiClient.get('/grades', {
      params: { size: 50, sort: 'gradeNo,asc', ...params },
    });
    return response.data;
  },
  createGrade: async (data) => {
    const response = await apiClient.post('/grades', data);
    return response.data;
  },
  updateGrade: async (id, data) => {
    const response = await apiClient.put(`/grades/${id}`, data);
    return response.data;
  },
  deleteGrade: async (id) => {
    await apiClient.delete(`/grades/${id}`);
  },

  // Streams
  getStreams: async (params = {}) => {
    const response = await apiClient.get('/streams', { params: { size: 50, ...params } });
    return response.data;
  },
  createStream: async (data) => {
    const response = await apiClient.post('/streams', data);
    return response.data;
  },

  // Classes
  getClasses: async (academicYearId = null, params = {}) => {
    const queryParams = { ...params };
    if (academicYearId) {
      queryParams.academicYearId = academicYearId;
    }
    const response = await apiClient.get('/classes', { params: queryParams });
    return response.data;
  },
  createClass: async (data) => {
    const response = await apiClient.post('/classes', data);
    return response.data;
  },
  updateClass: async (id, data) => {
    const response = await apiClient.put(`/classes/${id}`, data);
    return response.data;
  },
  deleteClass: async (id) => {
    await apiClient.delete(`/classes/${id}`);
  },

  // Rooms
  getRooms: async (params = {}) => {
    const response = await apiClient.get('/rooms', { params: { size: 50, sort: 'name,asc', ...params } });
    return response.data;
  },
  createRoom: async (data) => {
    const response = await apiClient.post('/rooms', data);
    return response.data;
  },
  updateRoom: async (id, data) => {
    const response = await apiClient.put(`/rooms/${id}`, data);
    return response.data;
  },
  deleteRoom: async (id) => {
    await apiClient.delete(`/rooms/${id}`);
  },
};

export default masterService;
