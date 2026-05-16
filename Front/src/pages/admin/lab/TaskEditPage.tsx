import React, { useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Typography, Button, Box, Paper, TextField } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { FileField } from '../../../components/common/FileField';

const AdminTaskEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  
  const isNew = !id;
  const labId = location.state?.labId;

  const handleFileChange = (file: File | null) => {
    setSelectedFile(file);
  };

  return (
    <Box p={3} maxWidth={800} mx="auto">
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
        <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
          {isNew ? `Добавление задания в лабораторную работу №${labId}` : 'Измените данные задания ниже.'}
        </Typography>

        <Box component="form" sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          <TextField
            label="Номер задания"
            type="number"
            defaultValue={1}
            fullWidth
          />
          <TextField
            label="Описание задания"
            multiline
            rows={4}
            fullWidth
            placeholder="Опишите требования к заданию..."
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

          <Box mt={2} display="flex" gap={2}>
            <Button variant="contained" color="primary" size="large">
              Сохранить задание
            </Button>
            <Button variant="outlined" onClick={() => navigate(-1)} size="large" sx={{ backgroundColor: 'transparent' }}>
              Отмена
            </Button>
          </Box>
        </Box>
      </Paper>

      <Box mt={4}>
        <Typography variant="h6" color="warning.main">
          Примечание: Это страница-заглушка.
        </Typography>
        <Typography variant="body2">
          В реальной версии здесь будет происходить загрузка файла на сервер и валидация данных.
        </Typography>
      </Box>
    </Box>
  );
};

export default AdminTaskEditPage;
