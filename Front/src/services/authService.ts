import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { UserResponseDto, LoginRequestDto } from '../api/generated/model';

const api = getOpenAPIDefinition();

export type UserResponse = UserResponseDto;

export const authService = {
  login: async (credentials: LoginRequestDto): Promise<UserResponse> => {
    const hash = btoa(`${credentials.username}:${credentials.password}`);

    try {
      // Передаем заголовок явно в опциях запроса
      const response = await api.login(credentials, {
        headers: { Authorization: `Basic ${hash}` }
      });
      localStorage.setItem('auth_hash', hash);
      return response;
    } catch (error) {
      throw error;
    }
  },

  getCurrentUser: async (): Promise<UserResponse> => {
    return await api.getCurrentUser();
  },
  
  logout: () => {
    localStorage.removeItem('auth_hash');
  }
};
