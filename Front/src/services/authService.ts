import api from '../api/apiClient';

interface LoginResponse {
  token: string;
  role: 'STUDENT' | 'ADMIN';
}

export const authService = {
  login: async (credentials: { username: string; password: string }): Promise<LoginResponse> => {
    // Временная заглушка: считаем, что авторизация успешна всегда
    return new Promise((resolve) => {
      setTimeout(() => {
        const mockRole = credentials.username === 'admin' ? 'ADMIN' : 'STUDENT';
        resolve({
          token: 'mock-jwt-token',
          role: mockRole,
        });
      }, 500);
    });
    // Позже заменить на:
    // const response = await api.post<LoginResponse>('/auth/login', credentials);
    // return response.data;
  },
  
  logout: () => {
    localStorage.removeItem('token');
  }
};
