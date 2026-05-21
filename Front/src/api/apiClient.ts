import axios, { type AxiosRequestConfig, type AxiosResponse, AxiosError } from 'axios';

export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
});

// Добавляем интерцептор для авторизации
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    // Проверка "протухания" токена на фронте (опционально, на основе константы)
    const loginTime = localStorage.getItem('login_time');
    const expirationHours = Number(import.meta.env.VITE_JWT_EXPIRATION_HOURS) || 5;
    
    if (loginTime && (Date.now() - Number(loginTime) > expirationHours * 60 * 60 * 1000)) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('login_time');
      window.location.href = '/login';
      return Promise.reject(new Error("Token expired by local limit"));
    }

    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Обработка ошибок
axiosInstance.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('login_time');
      // Можно не делать редирект здесь, если AuthContext обработает это, 
      // но для надежности принудительно уходим на логин
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }

    const data = error.response?.data as any;
    const message = data?.message || "Ошибка сервера";
    window.dispatchEvent(new CustomEvent('global-error', { detail: message }));
    return Promise.reject(error);
  }
);

// Мутатор для Orval
export const customInstance = <T>(
  config: AxiosRequestConfig,
  options?: AxiosRequestConfig,
): Promise<T> => {
  return axiosInstance({
    ...config,
    ...options,
  }).then((response: AxiosResponse<T>) => response.data);
};

export default customInstance;
