import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Paper, Typography, TextField, Button } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { labService } from '../../../services/labService';

const LabCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const [topic, setTopic] = useState('');
  const [number, setNumber] = useState('');
  const [description, setDescription] = useState('');

  const handleSave = async () => {
    try {
      await labService.createLab({
        topic,
        number: parseInt(number),
        description
      });
      navigate('/admin/labs');
    } catch (error) {
      console.error('Failed to create lab:', error);
    }
  };

  return (
    <Box p={3} maxWidth={800} mx="auto">
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/admin/labs')} sx={{ mb: 3 }}>
        Назад
      </Button>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>Создание лабораторной работы</Typography>
        <Box display="flex" flexDirection="column" gap={3} mt={3}>
          <TextField label="Номер" type="number" value={number} onChange={(e) => setNumber(e.target.value)} fullWidth />
          <TextField label="Тема" value={topic} onChange={(e) => setTopic(e.target.value)} fullWidth />
          <TextField label="Описание" multiline rows={4} value={description} onChange={(e) => setDescription(e.target.value)} fullWidth />
          <Button variant="contained" onClick={handleSave} size="large">Создать</Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default LabCreatePage;
