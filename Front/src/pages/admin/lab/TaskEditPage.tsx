import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Typography, Button, Box, Paper, TextField } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { FileField } from '../../../components/common/FileField';
import { labService } from '../../../services/labService';

const AdminTaskEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [number, setNumber] = useState('');
  const [description, setDescription] = useState('');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  
  const isNew = !id;
  const labId = location.state?.labId;

  useEffect(() => {
    if (!isNew && id) {
      loadTask(parseInt(id));
    }
  }, [isNew, id]);

  const loadTask = async (taskId: number) => {
    try {
      const task = await labService.getTaskById(taskId);
      if (task) {
        setNumber(task.number?.toString() || '');
        setDescription(task.description || '');
      }
    } catch (error) {
      console.error('Failed to load task:', error);
    }
  };

  const handleSave = async () => {
    try {
      if (isNew && labId) {
        await labService.createTask(labId, {
          number: parseInt(number),
          description,
        });
      } else if (id) {
        await labService.updateTask(parseInt(id), {
          number: parseInt(number),
          description,
        });
      }
      navigate(-1);
    } catch (error) {
      console.error('Failed to save task:', error);
    }
  };

  const handleFileChange = (file: File | null) => {
    setSelectedFile(file);
  };

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate(-1)}
        sx={{ mb: 3 }}
      >
        Назад
      </Button>

      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          {isNew ? 'Создание задания' : `Редактирование задания №${id}`}
        </Typography>
        
        <Box component="form" sx={{ display: 'flex', flexDirection: 'column', gap: 3, mt: 3 }}>
          <TextField
            label="Номер задания"
            type="number"
            value={number}
            onChange={(e) => setNumber(e.target.value)}
            fullWidth
          />
          <TextField
            label="Описание задания"
            multiline
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
          />
          
          <Box>
            <Typography variant="subtitle2" gutterBottom>
              Эталонный репозиторий (ZIP)
            </Typography>
            <FileField 
              label={selectedFile ? selectedFile.name : "Выберите ZIP-архив репозитория"} 
              onChange={handleFileChange} 
            />
          </Box>

          <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
            <Button variant="contained" color="primary" size="large" onClick={handleSave}>
              Сохранить задание
            </Button>
            <Button variant="outlined" onClick={() => navigate(-1)} size="large">
              Отмена
            </Button>
          </Box>
        </Box>
      </Paper>
    </Box>
  );
};

export default AdminTaskEditPage;
