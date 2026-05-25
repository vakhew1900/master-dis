import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  CircularProgress,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  TextField,
  Stack,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AssignmentIcon from '@mui/icons-material/Assignment';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import { labService } from '../../../services/labService';
import type { AdminLabDto, UserResponseDto } from '../../../api/generated/model';
import styles from './LabDetailPage.module.css';

import AdminTaskItem from '../../../components/lab/AdminTaskItem';

const AdminLabDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [lab, setLab] = useState<AdminLabDto | null>(null);
  const [editLab, setEditLab] = useState<AdminLabDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [openAssign, setOpenAssign] = useState(false);
  const [students, setStudents] = useState<UserResponseDto[]>([]);
  const [selectedStudent, setSelectedStudent] = useState('');
  const [selectedTask, setSelectedTask] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      loadLab(parseInt(id));
      loadStudents();
    }
  }, [id]);

  const loadLab = async (labId: number) => {
    setLoading(true);
    try {
      const data = await labService.getLabById(labId);
      if (data) {
        setLab(data);
        setEditLab(data);
      } else {
        navigate('/admin/labs');
      }
    } catch (error) {
      console.error('Failed to load lab details:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadStudents = async () => {
    const data = await labService.getAllStudents();
    setStudents(data);
  };

  const handleSave = async () => {
    if (editLab && id) {
      try {
        const updated = await labService.updateLab(parseInt(id), editLab);
        setLab(updated);
        setEditLab(updated);
        setIsEditing(false);
      } catch (error) {
        console.error('Failed to update lab:', error);
      }
    }
  };

  const cancelEdit = () => {
    setEditLab(lab);
    setIsEditing(false);
  };

  const handleAssign = async () => {
    if (selectedTask && selectedStudent) {
      await labService.assignTask(parseInt(selectedTask), parseInt(selectedStudent));
      setOpenAssign(false);
    }
  };

  const handleEditTask = (taskId: number) => {
    navigate(`/admin/tasks/${taskId}/edit`);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!lab || !editLab) return null;

  return (
    <div className={styles.container}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/admin/labs')}
        className={styles.backButton}
        sx={{ mb: 2 }}
      >
        Назад к списку
      </Button>

      <Paper className={styles.header} sx={{ p: 3 }}>
        {isEditing ? (
          <Stack spacing={2}>
            <TextField 
              label="Номер" type="number" fullWidth 
              value={editLab.number} 
              onChange={(e) => setEditLab({...editLab, number: parseInt(e.target.value)})}
            />
            <TextField 
              label="Тема" fullWidth 
              value={editLab.topic} 
              onChange={(e) => setEditLab({...editLab, topic: e.target.value})}
            />
            <TextField 
              label="Максимальная оценка" type="number" fullWidth 
              value={editLab.maxGrade} 
              onChange={(e) => setEditLab({...editLab, maxGrade: parseInt(e.target.value)})}
            />
            <TextField 
              label="Описание" multiline rows={4} fullWidth 
              value={editLab.description} 
              onChange={(e) => setEditLab({...editLab, description: e.target.value})}
            />
            <Stack direction="row" spacing={2}>
              <Button startIcon={<SaveIcon />} variant="contained" onClick={handleSave}>Сохранить</Button>
              <Button startIcon={<CancelIcon />} variant="outlined" onClick={cancelEdit}>Отмена</Button>
            </Stack>
          </Stack>
        ) : (
          <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Box>
                <Typography variant="h4" gutterBottom>
                  Лаб. работа №{lab.number}: {lab.topic}
                </Typography>
                <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                  Максимальная оценка: {lab.maxGrade || 0}
                </Typography>
              </Box>
              <Stack direction="row" spacing={1}>
                <Button startIcon={<PersonAddIcon />} onClick={() => setOpenAssign(true)}>Назначить</Button>
                <Button startIcon={<EditIcon />} onClick={() => setIsEditing(true)}>Редактировать</Button>
              </Stack>
            </Box>
            <Typography variant="body1" className={styles.description}>
              {lab.description}
            </Typography>
          </Box>
        )}
      </Paper>

      <Dialog open={openAssign} onClose={() => setOpenAssign(false)}>
        <DialogTitle>Назначить задание студенту</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField
            select
            fullWidth
            label="Задание"
            value={selectedTask}
            onChange={(e) => setSelectedTask(e.target.value)}
          >
            {lab.tasks?.map((t) => (
              <MenuItem key={t.id} value={t.id}>Задание {t.number}</MenuItem>
            ))}
          </TextField>
          <TextField
            select
            fullWidth
            label="Студент"
            value={selectedStudent}
            onChange={(e) => setSelectedStudent(e.target.value)}
          >
            {students.map((s) => (
              <MenuItem key={s.id} value={s.id}>{s.lastName} {s.firstName}</MenuItem>
            ))}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAssign(false)}>Отмена</Button>
          <Button onClick={handleAssign} variant="contained">Назначить</Button>
        </DialogActions>
      </Dialog>

      <div className={styles.section}>
        <div className={styles.sectionHeader}>
          <Typography variant="h5">Список заданий</Typography>
          {isEditing && (
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={() => navigate('/admin/tasks/new', { state: { labId: lab.id } })}
            >
              Добавить задание
            </Button>
          )}
        </div>
        <Divider sx={{ mb: 2 }} />
        
        <List>
          {lab.tasks && lab.tasks.length > 0 ? (
            lab.tasks.map((task) => (
              <AdminTaskItem
                key={task.id}
                task={task}
                isEditing={isEditing}
                onEdit={handleEditTask}
                className={styles.taskItem}
              />
            ))
          ) : (
            <Typography color="text.secondary">В этой лабораторной работе пока нет заданий.</Typography>
          )}
        </List>
      </div>
    </div>
  );
};

export default AdminLabDetailPage;
