import apiClient from '../api/client';

export const timetableService = {
  // Versions
  getVersions: async (termId = null, params = {}) => {
    const queryParams = { ...params };
    if (termId) {
      queryParams.termId = termId;
    }
    const response = await apiClient.get('/timetable-versions', { params: queryParams });
    return response.data;
  },
  getVersionById: async (id) => {
    const response = await apiClient.get(`/timetable-versions/${id}`);
    return response.data;
  },
  createVersion: async (data) => {
    const response = await apiClient.post('/timetable-versions', data);
    return response.data;
  },

  // View Entries
  getEntriesByClass: async (versionId, classGroupId) => {
    const response = await apiClient.get(
      `/timetable-versions/${versionId}/entries/by-class/${classGroupId}`
    );
    return response.data;
  },
  getEntriesByTeacher: async (versionId, teacherId) => {
    const response = await apiClient.get(
      `/timetable-versions/${versionId}/entries/by-teacher/${teacherId}`
    );
    return response.data;
  },
  getEntriesByRoom: async (versionId, roomId) => {
    const response = await apiClient.get(
      `/timetable-versions/${versionId}/entries/by-room/${roomId}`
    );
    return response.data;
  },

  // Entry CRUD & Move
  createEntry: async (versionId, data) => {
    const response = await apiClient.post(
      `/timetable-versions/${versionId}/entries`,
      data
    );
    return response.data;
  },
  updateEntry: async (versionId, entryId, data) => {
    const response = await apiClient.put(
      `/timetable-versions/${versionId}/entries/${entryId}`,
      data
    );
    return response.data;
  },
  deleteEntry: async (versionId, entryId) => {
    await apiClient.delete(`/timetable-versions/${versionId}/entries/${entryId}`);
  },
};

export default timetableService;
