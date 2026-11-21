import apiClient from '../api/client';

export const timeSlotService = {
  getTimeSlots: async (params = {}) => {
    const response = await apiClient.get('/time-slots', {
      params: { size: 100, sort: 'dayOfWeek,asc&sort=periodNo,asc', ...params },
    });
    return response.data;
  },
  createTimeSlot: async (data) => {
    const response = await apiClient.post('/time-slots', data);
    return response.data;
  },
  updateTimeSlot: async (id, data) => {
    const response = await apiClient.put(`/time-slots/${id}`, data);
    return response.data;
  },
  deleteTimeSlot: async (id) => {
    await apiClient.delete(`/time-slots/${id}`);
  },
};

export default timeSlotService;
