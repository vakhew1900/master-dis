import api from '../api/apiClient';

interface LoginResponse {
  username: string;
  role: 'STUDENT' | 'ADMIN';
}

export const authService = {
  login: async (credentials: { username: string; password: string }): Promise<LoginResponse> => {
    // Для Basic Auth нам нужно закодировать credentials в base64
    const hash = btoa(`${credentials.username}:${credentials.password}`);
    
    // В реальном приложении мы бы отправили пробный запрос для проверки:
    // const response = await api.get('/auth/login', {
    //   headers: { Authorization: `Basic ${hash}` }
    // });
    
    // Пока эмулируем успех
    return new Promise((resolve) => {
      setTimeout(() => {
        const mockRole = credentials.username === 'admin' ? 'ADMIN' : 'STUDENT';
        localStorage.setItem('auth_hash', hash);
        resolve({
          username: credentials.username,
          role: mockRole as 'STUDENT' | 'ADMIN',
        });
      }, 500);
    });
  },
  
  logout: () => {
    localStorage.removeItem('auth_hash');
  }
};
