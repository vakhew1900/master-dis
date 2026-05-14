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
            {/* ... */}
          </Route>
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
