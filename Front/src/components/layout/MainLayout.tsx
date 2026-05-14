import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

const MainLayout: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header />
      <main className="container" style={{ flexGrow: 1 }}>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default MainLayout;
