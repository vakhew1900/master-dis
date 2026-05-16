import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { authService } from '../../services/authService';
import { useNotification } from '../../context/NotificationContext';
import { InputField } from '../../components/common/InputField';
import { ROUTES } from '../../api/models/constants';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const { addError } = useNotification();
  const navigate = useNavigate();
  const location = useLocation();




// ...

  const from = location.state?.from?.pathname || (username === 'admin' ? ROUTES.ADMIN : '/student');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const data = await authService.login({ username, password });

      localStorage.setItem('token', data.token);
      login(username, data.role);

      navigate(from, { replace: true });
    } catch (error) {
      addError('Ошибка входа: неверные данные');
    }
  };
  return (
    <div className="details-panel" style={{ maxWidth: '400px', margin: '40px auto' }}>
      <h2>Вход</h2>
      <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        <InputField label="Логин" value={username} onChange={setUsername} placeholder="admin или student" />
        <InputField label="Пароль" value={password} onChange={setPassword} placeholder="Пароль" type="password" />
        <button type="submit" className="nav-link" style={{ background: 'var(--nav-active)', color: 'white', cursor: 'pointer', border: 'none', padding: '10px' }}>
          Войти
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
