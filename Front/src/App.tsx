import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import theme from './theme';
import MainLayout from './components/layout/MainLayout';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import LoginPage from './pages/auth/LoginPage';
import ComparisonPage from './pages/ComparisonPage';
import ComparisonResultPage from './pages/ComparisonResultPage';
import { ProtectedRoute } from './components/routing/ProtectedRoute';
import { USER_ROLES, ROUTES } from './api/models/constants';
import AdminLabsPage from './pages/admin/lab/LabsPage';
import LabCreatePage from './pages/admin/lab/LabCreatePage';
import AdminLabDetailPage from './pages/admin/lab/LabDetailPage';
import AdminTaskEditPage from './pages/admin/lab/TaskEditPage';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Routes>
          <Route path={ROUTES.HOME} element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path={ROUTES.LOGIN.substring(1)} element={<LoginPage />} />
            <Route 
              path={ROUTES.COMPARISON.substring(1)} 
              element={
                <ProtectedRoute roles={[USER_ROLES.STUDENT, USER_ROLES.ADMIN]}>
                  <ComparisonPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path={ROUTES.COMPARISON_RESULT.substring(1)} 
              element={
                <ProtectedRoute roles={[USER_ROLES.STUDENT, USER_ROLES.ADMIN]}>
                  <ComparisonResultPage />
                </ProtectedRoute>
              } 
            />
            
            <Route path={ROUTES.ADMIN.ROOT}>
              <Route index element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminPage />
                </ProtectedRoute>
              } />
              <Route path={ROUTES.ADMIN.LABS.ROOT} element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminLabsPage />
                </ProtectedRoute>
              } />
              <Route path={ROUTES.ADMIN.LABS.NEW} element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <LabCreatePage />
                </ProtectedRoute>
              } />
              <Route path={ROUTES.ADMIN.LABS.DETAIL(':id')} element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminLabDetailPage />
                </ProtectedRoute>
              } />
              <Route path={ROUTES.ADMIN.TASKS.NEW} element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } />
              <Route path={ROUTES.ADMIN.TASKS.EDIT(':id')} element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } />
            </Route>
          </Route>
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
