import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import theme from './theme';
import MainLayout from './components/layout/MainLayout';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import StudentPage from './pages/StudentPage';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="admin" element={<AdminPage />} />
            <Route path="student" element={<StudentPage />} />
          </Route>
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
