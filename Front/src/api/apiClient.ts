import axios from 'axios';
// Мы не можем импортировать NotificationContext напрямую здесь, 
// поэтому будем использовать коллбэк-обработчик или кастомное событие,
// но для простоты здесь сделаем прямой вызов через window (не идеально, но для прототипа подойдет)

const api = axios.create({
  baseURL: '/api',
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Глобальный обработчик ошибок
    const message = error.response?.data?.message || error.message || 'Произошла ошибка';
    window.dispatchEvent(new CustomEvent('global-error', { detail: message }));
    return Promise.reject(error);
  }
);

export default api;
