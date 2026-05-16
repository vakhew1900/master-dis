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
  CircularProgress
} from '@mui/material';
import { labService } from '../../services/labService';
import type { components } from '../../api/models/schema';
import SubmissionCell from '../../components/common/SubmissionCell';

type UserDto = components["schemas"]["UserDto"];
type LaboratoryWork = components["schemas"]["LaboratoryWork"];
type SubmissionDto = components["schemas"]["SubmissionDto"];

const StudentsPage: React.FC = () => {
  const [students, setStudents] = useState<UserDto[]>([]);
  const [allLabs, setAllLabs] = useState<LaboratoryWork[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [studentsList, labsList] = await Promise.all([
        labService.getAllStudents(),
        labService.getAllLabs()
      ]);
      setStudents(studentsList);
      setAllLabs(labsList);
    } catch (error) {
      console.error('Failed to load students/labs:', error);
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
      <Typography variant="h4" gutterBottom>Список студентов</Typography>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 800 }}>
          <TableHead>
            <TableRow>
              <TableCell>Логин</TableCell>
              <TableCell>ФИО</TableCell>
              {allLabs.map(lab => (
                <TableCell key={lab.id} align="center">ЛР {lab.number}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {students.map((student) => {
              const submissions = (student.submissions as SubmissionDto[]) || [];
              
              return (
                <TableRow key={student.id}>
                  <TableCell>{student.username}</TableCell>
                  <TableCell>{`${student.lastName || ''} ${student.firstName || ''} ${student.middleName || ''}`.trim()}</TableCell>
                  {allLabs.map(lab => {
                    const submission = submissions.find((sub) => sub.labNumber === lab.number);
                    return (
                      <TableCell key={lab.id} align="center">
                        <SubmissionCell labNumber={lab.number!} submission={submission} />
                      </TableCell>
                    );
                  })}
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default StudentsPage;
