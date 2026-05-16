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
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AssignmentIcon from '@mui/icons-material/Assignment';
import AddIcon from '@mui/icons-material/Add';
import { labService } from '../../services/labService';
import type { LaboratoryWork } from '../../services/labService';
import styles from './AdminLabDetailPage.module.css';

const AdminLabDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [lab, setLab] = useState<LaboratoryWork | null>(null);
  const [loading, setLoading] = useState(true);
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
      } else {
        navigate('/admin/labs');
      }
    } catch (error) {
      console.error('Failed to load lab details:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (!lab) return null;

  return (
    <div className={styles.container}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/admin/labs')}
        className={styles.backButton}
      >
        Назад к списку
      </Button>

      <Paper className={styles.header} sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>
          Лаб. работа №{lab.number}: {lab.topic}
        </Typography>
        <Typography variant="body1" className={styles.description}>
          {lab.description}
        </Typography>
      </Paper>

      <div className={styles.section}>
        <div className={styles.sectionHeader}>
          <Typography variant="h5">Список заданий</Typography>
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddIcon />}
            onClick={() => navigate('/admin/tasks/new', { state: { labId: lab.id } })}
          >
            Добавить задание
          </Button>
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
                <Button 
                  size="small" 
                  onClick={() => navigate(`/admin/tasks/${task.id}/edit`)}
                >
                  Изменить
                </Button>
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
