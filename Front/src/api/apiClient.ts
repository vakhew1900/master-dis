import axios, { type AxiosRequestConfig, type AxiosResponse, AxiosError } from 'axios';

export const axiosInstance = axios.create({
  baseURL: '', // Пути в схеме уже начинаются с /api, поэтому здесь оставляем пусто
});

// Добавляем интерцептор для авторизации
axiosInstance.interceptors.request.use((config) => {
  const authHash = localStorage.getItem('auth_hash');
  if (authHash) {
    config.headers.Authorization = `Basic ${authHash}`;
  }
  return config;
});

// Обработка ошибок
axiosInstance.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
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
