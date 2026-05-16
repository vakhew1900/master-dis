import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import theme from './theme';
import MainLayout from './components/layout/MainLayout';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import StudentPage from './pages/StudentPage';
import LoginPage from './pages/auth/LoginPage';
import ComparisonPage from './pages/ComparisonPage';
import ComparisonResultPage from './pages/ComparisonResultPage';
import { ProtectedRoute } from './components/routing/ProtectedRoute';
import AdminLabsPage from './pages/admin/AdminLabsPage';
import AdminLabDetailPage from './pages/admin/AdminLabDetailPage';
import AdminTaskEditPage from './pages/admin/AdminTaskEditPage';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route 
              path="comparison" 
              element={
                <ProtectedRoute role="STUDENT">
                  <ComparisonPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="comparison-result" 
              element={
                <ProtectedRoute role="STUDENT">
                  <ComparisonResultPage />
                </ProtectedRoute>
              } 
            />
            
            {/* Admin Routes */}
            <Route 
              path="admin" 
              element={
                <ProtectedRoute role="ADMIN">
                  <AdminPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="admin/labs" 
              element={
                <ProtectedRoute role="ADMIN">
                  <AdminLabsPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="admin/labs/:id" 
              element={
                <ProtectedRoute role="ADMIN">
                  <AdminLabDetailPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="admin/tasks/new" 
              element={
                <ProtectedRoute role="ADMIN">
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="admin/tasks/:id/edit" 
              element={
                <ProtectedRoute role="ADMIN">
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } 
            />
          </Route>
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
