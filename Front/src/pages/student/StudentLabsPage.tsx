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
} from '@mui/material';
import { labService } from '../../services/labService';
import type { StudentLabDto } from '../../api/generated/model';

import StudentLabRow from '../../components/lab/StudentLabRow';

const StudentLabsPage: React.FC = () => {
  const [labs, setLabs] = useState<StudentLabDto[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const loadLabs = async () => {
    setLoading(true);
    try {
      const data = await labService.getStudentLabs();
      setLabs(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Failed to load student labs:', error);
      setLabs([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
     // eslint-disable-next-line react-hooks/set-state-in-effect
    loadLabs();
  }, []);


  const handleOpenLab = (id: number) => {
    navigate(`/student/submission/${id}`);
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
              <TableCell width={80}>Номер</TableCell>
              <TableCell>Тема и описание</TableCell>
              <TableCell width={150}>Оценка (тек./макс.)</TableCell>
              <TableCell>Статус заданий</TableCell>
              <TableCell width={120}>Действие</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {Array.isArray(labs) && labs.map((lab) => (
              <StudentLabRow 
                key={lab.id} 
                lab={lab} 
                onOpen={handleOpenLab} 
              />
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default StudentLabsPage;
