import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  CircularProgress,
  Button
} from '@mui/material';
import { labService } from '../../services/labService';
import type { components } from '../../api/models/schema';

type LaboratoryWorkDto = components["schemas"]["LaboratoryWorkDto"];

const StudentLabsPage: React.FC = () => {
  const [labs, setLabs] = useState<LaboratoryWorkDto[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadLabs();
  }, []);

  const loadLabs = async () => {
    setLoading(true);
    try {
      const data = await labService.getStudentLabs();
      setLabs(data);
    } catch (error) {
      console.error('Failed to load student labs:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>Мои лабораторные работы</Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Номер</TableCell>
              <TableCell>Тема</TableCell>
              <TableCell>Макс. оценка</TableCell>
              <TableCell>Статус</TableCell>
              <TableCell>Действие</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {labs.map((lab) => (
              <TableRow key={lab.id}>
                <TableCell>{lab.number}</TableCell>
                <TableCell>{lab.topic}</TableCell>
                <TableCell>{lab.maxGrade}</TableCell>
                <TableCell>
                  {lab.tasks?.map(task => (
                    <Typography key={task.id} variant="body2">{task.status}</Typography>
                  ))}
                </TableCell>
                <TableCell>
                  <Button variant="contained" onClick={() => navigate(`/student/submission/${lab.id}`)}>
                    Открыть
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default StudentLabsPage;
