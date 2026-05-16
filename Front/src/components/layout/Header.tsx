import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { USER_ROLES } from '../../api/models/constants';
import styles from './Header.module.css';

const Header: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();

  return (
    <header className={styles.mainHeader}>
      <div className={styles.headerContent}>
        <Link to="/" className={styles.logo}>ОТЧЕТ О СРАВНЕНИИ GIT</Link>
        <nav className={styles.navBar}>
          <NavLink to="/" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`} end>Главная</NavLink>
          
          {isAuthenticated && (
            <NavLink to="/comparison" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Сравнение графов</NavLink>
          )}
          
          {isAuthenticated && user?.role === USER_ROLES.STUDENT && (
            <NavLink to="/student" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Студент</NavLink>
          )}
          
          {isAuthenticated && user?.role === USER_ROLES.ADMIN && (
            <NavLink to="/admin" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Админ панель</NavLink>
          )}

          {!isAuthenticated ? (
            <NavLink to="/login" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Вход</NavLink>
          ) : (
            <button onClick={logout} className={styles.logoutBtn}>Выход</button>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
