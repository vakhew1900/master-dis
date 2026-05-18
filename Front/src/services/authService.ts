import { getOpenAPIDefinition } from '../api/generated/jserver';
import type { UserResponseDto, LoginRequestDto } from '../api/generated/model';

const api = getOpenAPIDefinition();

export type UserResponse = UserResponseDto;

export const authService = {
  login: async (credentials: LoginRequestDto): Promise<UserResponse> => {
    try {
      // Сгенерированный метод login теперь возвращает Login200 (который содержит токен)
      const response = await api.login(credentials);
      
      // Предполагаем, что токен лежит в поле 'token' (проверьте структуру Login200)
      const token = (response as any).token; 
      if (token) {
        localStorage.setItem('jwt_token', token);
      }
      
      return await api.getCurrentUser();
    } catch (error) {
      throw error;
    }
  },

  getCurrentUser: async (): Promise<UserResponse> => {
    return await api.getCurrentUser();
  },
  
  logout: () => {
    localStorage.removeItem('jwt_token');
  }
};
