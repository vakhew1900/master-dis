import React from 'react';
import { Breadcrumbs, Link, Typography, Box } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

const breadcrumbNameMap: { [key: string]: string } = {
  '/admin': 'Панель администратора',
  '/admin/labs': 'Лабораторные работы',
  '/admin/students': 'Студенты',
  '/admin/tasks/new': 'Новое задание',
};

export const AdminBreadcrumbs: React.FC = () => {
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter((x) => x);

  return (
    <Box sx={{ mb: 2 }}>
      <Breadcrumbs 
        separator={<NavigateNextIcon fontSize="small" />} 
        aria-label="breadcrumb"
        sx={{ color: 'text.secondary' }}
      >
        <Link 
          component={RouterLink} 
          underline="hover" 
          color="inherit" 
          to="/"
        >
          Главная
        </Link>
        {pathnames.map((value, index) => {
          const last = index === pathnames.length - 1;
          const to = `/${pathnames.slice(0, index + 1).join('/')}`;

          // Handle dynamic IDs (like /admin/labs/1)
          let name = breadcrumbNameMap[to];
          if (!name) {
             if (pathnames[index - 1] === 'labs') name = `Работа №${value}`;
             else if (pathnames[index - 1] === 'tasks') name = `Задание №${value}`;
             else name = value;
          }

          return last ? (
            <Typography color="text.primary" key={to}>
              {name}
            </Typography>
          ) : (
            <Link 
              component={RouterLink} 
              underline="hover" 
              color="inherit" 
              to={to} 
              key={to}
            >
              {name}
            </Link>
          );
        })}
      </Breadcrumbs>
    </Box>
  );
};
