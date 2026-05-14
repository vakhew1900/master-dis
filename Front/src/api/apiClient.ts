import createClient from "openapi-fetch";
import type { paths } from "./models/schema";

const client = createClient<paths>({ 
    baseUrl: "/api",
});

// Мидлвар для авторизации и обработки ошибок
client.use({
  onRequest({ request }) {
    const authHash = localStorage.getItem('auth_hash');
    if (authHash) {
      request.headers.set("Authorization", `Basic ${authHash}`);
    }
    return request;
  },
  onResponse({ response }) {
    if (!response.ok) {
        // Глобальный обработчик ошибок
        response.json().then(data => {
            const message = data.message || "Ошибка сервера";
            window.dispatchEvent(new CustomEvent('global-error', { detail: message }));
        }).catch(() => {
            window.dispatchEvent(new CustomEvent('global-error', { detail: "Неизвестная ошибка" }));
        });
    }
    return response;
  }
});

export default client;
