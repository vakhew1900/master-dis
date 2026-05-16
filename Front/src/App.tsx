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
import { USER_ROLES } from './api/models/constants';
import AdminLabsPage from './pages/admin/lab/LabsPage';
import LabCreatePage from './pages/admin/lab/LabCreatePage';
import AdminLabDetailPage from './pages/admin/lab/LabDetailPage';
import AdminTaskEditPage from './pages/admin/lab/TaskEditPage';
import StudentsPage from './pages/admin/StudentsPage';
import SubmissionDetailPage from './pages/admin/SubmissionDetailPage';
import StudentLabsPage from './pages/student/StudentLabsPage';
import StudentSubmissionPage from './pages/student/StudentSubmissionPage';

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
                <ProtectedRoute roles={[USER_ROLES.STUDENT, USER_ROLES.ADMIN]}>
                  <ComparisonPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="comparison-result" 
              element={
                <ProtectedRoute roles={[USER_ROLES.STUDENT, USER_ROLES.ADMIN]}>
                  <ComparisonResultPage />
                </ProtectedRoute>
              } 
            />
            
            <Route path="admin">
              <Route index element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminPage />
                </ProtectedRoute>
              } />
              <Route path="labs" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminLabsPage />
                </ProtectedRoute>
              } />
              <Route path="labs/new" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <LabCreatePage />
                </ProtectedRoute>
              } />
              <Route path="labs/:id" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminLabDetailPage />
                </ProtectedRoute>
              } />
              <Route path="tasks/new" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } />
              <Route path="tasks/:id/edit" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <AdminTaskEditPage />
                </ProtectedRoute>
              } />
              <Route path="students" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <StudentsPage />
                </ProtectedRoute>
              } />
              <Route path="student/submission/:submissionId" element={
                <ProtectedRoute roles={[USER_ROLES.ADMIN]}>
                  <SubmissionDetailPage />
                </ProtectedRoute>
              } />
            </Route>

            <Route path="student">
              <Route index element={
                <ProtectedRoute roles={[USER_ROLES.STUDENT]}>
                  <StudentLabsPage />
                </ProtectedRoute>
              } />
              <Route path="submission/:labId" element={
                <ProtectedRoute roles={[USER_ROLES.STUDENT]}>
                  <StudentSubmissionPage />
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
