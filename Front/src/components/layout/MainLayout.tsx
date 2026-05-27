import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';
import { Breadcrumbs } from '../common/Breadcrumbs';
import styles from './MainLayout.module.css';

const MainLayout: React.FC = () => {
  return (
    <div className={styles.layoutContainer}>
      <Header />
      <main className={styles.mainContent}>
        <Breadcrumbs />
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
