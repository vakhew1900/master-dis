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
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { labService } from '../../services/labService';
import type { components } from '../../api/models/schema';
import SubmissionCell from '../../components/common/SubmissionCell';
import { ActionsMenu } from '../../components/common/ActionsMenu';
import { useNotification } from '../../components/common/NotificationManager';

type UserResponseDto = components["schemas"]["UserResponseDto"];
type UserCreateDto = components["schemas"]["UserCreateDto"];

const StudentsPage: React.FC = () => {
  const [students, setStudents] = useState<UserResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [openCreate, setOpenCreate] = useState(false);
  const [openDelete, setOpenDelete] = useState<number | null>(null);
  const [newStudent, setNewStudent] = useState<UserCreateDto>({ username: '', firstName: '', lastName: '', password: '' });
  const { showNotification, NotificationComponent } = useNotification();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await labService.getAllStudents();
      setStudents(data);
    } catch (error) {
      console.error('Failed to load students:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateStudent = async () => {
    try {
      await labService.createStudent(newStudent);
      setOpenCreate(false);
      setNewStudent({ username: '', firstName: '', lastName: '', password: '' });
      loadData();
      showNotification('Студент успешно создан', 'success');
    } catch (error) {
      showNotification('Ошибка создания студента', 'error');
    }
  };

  const handleDelete = async () => {
    if (openDelete) {
      try {
        await labService.deleteStudent(openDelete);
        setStudents(students.filter(s => s.id !== openDelete));
        showNotification('Студент успешно удален', 'success');
      } catch (error) {
        showNotification('Ошибка при удалении', 'error');
      }
      setOpenDelete(null);
    }
  };

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>;
  }

  return (
    <Box sx={{ p: 3 }}>
      {NotificationComponent}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Typography variant="h4">Список студентов</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setOpenCreate(true)}>
          Добавить студента
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Логин</TableCell>
              <TableCell>ФИО</TableCell>
              <TableCell>Работы</TableCell>
              <TableCell align="right">Действия</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {students.map((student) => (
              <TableRow key={student.id}>
                <TableCell>{student.username}</TableCell>
                <TableCell>{`${student.lastName || ''} ${student.firstName || ''}`.trim()}</TableCell>
                <TableCell>
                  {(student.submissions || []).map((sub: any, index: number) => (
                    <SubmissionCell key={index} labNumber={sub.labNumber} submission={sub} />
                  ))}
                </TableCell>
                <TableCell align="right">
                  <ActionsMenu onDelete={() => setOpenDelete(student.id!)} />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={!!openDelete} onClose={() => setOpenDelete(null)}>
        <DialogTitle>Подтверждение</DialogTitle>
        <DialogContent>Вы уверены, что хотите удалить этого студента?</DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDelete(null)}>Отмена</Button>
          <Button onClick={handleDelete} color="error" variant="contained">Удалить</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} fullWidth maxWidth="sm">
        <DialogTitle>Добавить студента</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <TextField label="Логин" fullWidth onChange={(e) => setNewStudent({...newStudent, username: e.target.value})} />
          <TextField label="Фамилия" fullWidth onChange={(e) => setNewStudent({...newStudent, lastName: e.target.value})} />
          <TextField label="Имя" fullWidth onChange={(e) => setNewStudent({...newStudent, firstName: e.target.value})} />
          <TextField label="Пароль" type="password" fullWidth onChange={(e) => setNewStudent({...newStudent, password: e.target.value})} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCreate(false)}>Отмена</Button>
          <Button variant="contained" onClick={handleCreateStudent}>Добавить</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default StudentsPage;
