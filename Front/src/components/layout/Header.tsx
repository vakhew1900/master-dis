import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import styles from './Header.module.css';

const Header: React.FC = () => {
  return (
    <header className={styles.mainHeader}>
      <div className={styles.headerContent}>
        <Link to="/" className={styles.logo}>GIT COMPARISON REPORT</Link>
        <nav className={styles.navBar}>
          <NavLink to="/" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`} end>Главная</NavLink>
          <NavLink to="/comparison" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Сравнение графов</NavLink>
          <NavLink to="/student" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Студент</NavLink>
          <NavLink to="/admin" className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}>Админ панель</NavLink>
        </nav>
      </div>
    </header>
  );
};

export default Header;
