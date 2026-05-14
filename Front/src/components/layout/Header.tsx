import React from 'react';
import { NavLink, Link } from 'react-router-dom';

const Header: React.FC = () => {
  return (
    <header className="main-header">
      <div className="header-content">
        <Link to="/" className="logo">GIT COMPARISON REPORT</Link>
        <nav className="nav-bar">
          <NavLink to="/" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'} end>Главная</NavLink>
          <NavLink to="/comparison" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Сравнение графов</NavLink>
          <NavLink to="/student" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Студент</NavLink>
          <NavLink to="/admin" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>Админ панель</NavLink>
        </nav>
      </div>
    </header>
  );
};

export default Header;
