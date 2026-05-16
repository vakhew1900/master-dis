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
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AssignmentIcon from '@mui/icons-material/Assignment';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import { labService } from '../../../services/labService';
import type { LaboratoryWork } from '../../../services/labService';
import styles from './LabDetailPage.module.css';

const AdminLabDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [lab, setLab] = useState<LaboratoryWork | null>(null);
  const [editLab, setEditLab] = useState<LaboratoryWork | null>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      loadLab(parseInt(id));
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
              <Typography variant="h4" gutterBottom>
                Лаб. работа №{lab.number}: {lab.topic}
              </Typography>
              <Button startIcon={<EditIcon />} onClick={() => setIsEditing(true)}>Редактировать</Button>
            </Box>
            <Typography variant="body1" className={styles.description}>
              {lab.description}
            </Typography>
          </Box>
        )}
      </Paper>

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
              <ListItem key={task.id} className={styles.taskItem} component={Paper} sx={{ mb: 1 }}>
                <ListItemIcon>
                  <AssignmentIcon color="primary" />
                </ListItemIcon>
                <ListItemText
                  primary={`Задание ${task.number}`}
                  secondary={task.description}
                />
                {isEditing && (
                  <Button 
                    size="small" 
                    onClick={() => navigate(`/admin/tasks/${task.id}/edit`)}
                  >
                    Изменить
                  </Button>
                )}
              </ListItem>
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
