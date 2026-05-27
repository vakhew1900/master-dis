import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import { InputField } from '../../components/common/InputField';
import { ROUTES } from '../../api/models/constants';
import commonStyles from '../../styles/common.module.css';


const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const { addError } = useNotification();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login({ username, password });
      const from = (location.state as any)?.from?.pathname || ROUTES.HOME;
      navigate(from, { replace: true });
    } catch (err) {
      addError('Неверный логин или пароль');
    }
  };

  return (
   <div className={`${commonStyles.detailsPanel}`} style={{ maxWidth: '400px', margin: '40px auto' }}>
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
