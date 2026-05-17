import React from 'react';
import { Breadcrumbs as MUIBreadcrumbs, Link, Typography, Box } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

// Карта статических путей
const breadcrumbNameMap: { [key: string]: string } = {
  '/admin': 'Панель администратора',
  '/admin/labs': 'Лабораторные работы',
  '/admin/students': 'Студенты',
  '/admin/tasks': 'Задания',
  '/admin/tasks/new': 'Новое задание',
  '/student': 'Личный кабинет студента',
  '/comparison': 'Сравнение графов',
  '/comparison-result': 'Результат сравнения',
  '/login': 'Вход',
};

export const Breadcrumbs: React.FC = () => {
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter((x) => x);

  // Не показываем крошки на главной странице
  if (pathnames.length === 0) {
    return null;
  }

  return (
    <Box sx={{ mb: 2, mt: -1}}>
      <MUIBreadcrumbs 
        separator={<NavigateNextIcon fontSize="small" />} 
        aria-label="breadcrumb"
        sx={{ color: 'text.secondary', fontSize: '0.875rem' }}
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

          // Логика определения имени для динамических путей
          let name = breadcrumbNameMap[to];
          if (!name) {
             const prevPart = pathnames[index - 1];
             if (prevPart === 'labs') name = `Работа №${value}`;
             else if (prevPart === 'tasks') name = `Задание №${value}`;
             else if (prevPart === 'students') name = `Студент №${value}`;
             else name = value.charAt(0).toUpperCase() + value.slice(1);
          }

          return last ? (
            <Typography color="text.primary" key={to} sx={{ fontSize: '0.875rem' }}>
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
      </MUIBreadcrumbs>
    </Box>
  );
};
