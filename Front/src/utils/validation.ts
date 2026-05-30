import { z } from 'zod';

/**
 * Регулярное выражение для логина: только латинские буквы, цифры и нижнее подчеркивание.
 */
export const LOGIN_REGEX = /^[a-zA-Z0-9_]+$/;

/**
 * Регулярное выражение для пароля: латинские буквы, цифры, нижнее подчеркивание 
 * и специальные символы (!@.#$%^&*()+\-=[\]{};':"\\|,.<>/?).
 */
export const PASSWORD_REGEX = /^[a-zA-Z0-9_!@.#$%^&*()+\-=[\]{};':"\\|,.<>/?\s]+$/;

export const loginSchema = z.string()
  .min(1, 'Логин не может быть пустым')
  .regex(LOGIN_REGEX, 'Логин может содержать только латинские буквы, цифры и нижнее подчеркивание');

export const passwordSchema = z.string()
  .min(1, 'Пароль не может быть пустым')
  .min(3, 'Пароль должен быть не менее 6 символов')
  .regex(PASSWORD_REGEX, 'Пароль содержит недопустимые символы');

export const validateLogin = (login: string): string | null => {
  const result = loginSchema.safeParse(login);
  if (!result.success) {
    return result.error.message;
  }
  return null;
};

export const validatePassword = (password: string): string | null => {
  const result = passwordSchema.safeParse(password);
  if (!result.success) {
    return result.error.message
  }
  return null;
};
