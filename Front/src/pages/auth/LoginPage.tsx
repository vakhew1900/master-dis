import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import { InputField } from '../../components/common/InputField';
import { ROUTES } from '../../api/models/constants';
import commonStyles from '../../styles/common.module.css';
import { validateLogin, validatePassword } from '../../utils/validation';


const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [usernameError, setUsernameError] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  
  const { login } = useAuth();
  const { addError } = useNotification();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const uError = validateLogin(username);
    const pError = validatePassword(password);
    
    setUsernameError(uError);
    setPasswordError(pError);
    
    if (uError || pError) return;

    try {
      await login({ username, password });
      const from = location.state?.from?.pathname || ROUTES.HOME;
      navigate(from, { replace: true });
    } catch {
      addError('Неверный логин или пароль');
    }
  };

  return (
   <div className={`${commonStyles.detailsPanel}`} style={{ maxWidth: '400px', margin: '40px auto' }}>
      <h2>Вход</h2>
      <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        <InputField 
          label="Логин" 
          value={username} 
          onChange={(val) => {
            setUsername(val);
            if (usernameError) setUsernameError(validateLogin(val));
          }} 
          placeholder="admin или student"
          error={!!usernameError}
          helperText={usernameError}
        />
        <InputField 
          label="Пароль" 
          value={password} 
          onChange={(val) => {
            setPassword(val);
            if (passwordError) setPasswordError(validatePassword(val));
          }} 
          placeholder="Пароль" 
          type="password"
          error={!!passwordError}
          helperText={passwordError}
        />
        <button type="submit" className="nav-link" style={{ background: 'var(--nav-active)', color: 'white', cursor: 'pointer', border: 'none', padding: '10px' }}>
          Войти
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
