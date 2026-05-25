import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  Paper,
  Stack,
  TextField,
  MenuItem,
  Divider,
  CircularProgress
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { FileField } from '../../components/common/FileField';
import { labService } from '../../services/labService';
import { graphService } from '../../services/graphService';
import { REPORT_TYPES } from '../../api/models/constants';
import { getComparisonResultState } from '../../api/utils';
import type { StudentLabDto } from '../../api/generated/model';

const StudentSubmissionPage: React.FC = () => {
  const { labId } = useParams<{ labId: string }>();
  const navigate = useNavigate();
  const [lab, setLab] = useState<StudentLabDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [reportType, setReportType] = useState<string>(REPORT_TYPES.TWO_GRAPH);

  useEffect(() => {
    if (labId) {
      loadLabData(parseInt(labId));
    }
  }, [labId]);

  const loadLabData = async (id: number) => {
    setLoading(true);
    try {
      const data = await labService.getStudentLabById(id);
      if (data) {
        setLab(data);
      }
    } catch (error) {
      console.error('Failed to load lab:', error);
    } finally {
      setLoading(false);
    }
  };

  const currentGrade = (lab?.tasks || []).reduce((sum, t) => sum + (t.grade || 0), 0);

  const handleUpload = async () => {
    // If there are multiple tasks, we might need a selector. 
    // For now, assume uploading to the first task if not specified.
    const taskId = lab?.tasks?.[0]?.id;
    
    if (selectedFile && taskId) {
      try {
        await labService.uploadSolution(taskId, selectedFile);
        alert('Решение загружено');
        if (labId) loadLabData(parseInt(labId)); // Refresh data
      } catch (error) {
        console.error('Upload failed:', error);
      }
    } else {
      alert('Не выбрано задание или файл');
    }
  };

  const handleCheck = async () => {
    const taskId = lab?.tasks?.[0]?.id;
    if (taskId) {
      try {
        const result = await graphService.checkSolution(taskId, { reportType: reportType as any });
        
        navigate('/comparison-result', { 
          state: getComparisonResultState(result)
        });
      } catch (error) {
        console.error('Check failed:', error);
      }
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}><CircularProgress /></Box>;
  if (!lab) return <Typography>Лабораторная работа не найдена</Typography>;

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
        Назад к списку
      </Button>

      <Paper sx={{ p: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Box>
            <Typography variant="h4" gutterBottom>Лабораторная работа №{lab.number}: {lab.topic}</Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>{lab.description}</Typography>
          </Box>
          <Box sx={{ textAlign: 'right' }}>
            <Typography variant="h5" color="primary">
              {currentGrade} / {lab.maxGrade || 0}
            </Typography>
            <Typography variant="caption" color="text.secondary">Баллы</Typography>
          </Box>
        </Box>
        
        <Stack spacing={3} sx={{ mt: 3 }}>
          <Box>
            <Typography variant="h6">Загрузить решение</Typography>
            <FileField 
              label={selectedFile ? selectedFile.name : "Выберите ZIP-архив"} 
              onChange={(f) => setSelectedFile(f)} 
            />
            <Button variant="contained" onClick={handleUpload} sx={{ mt: 1 }} disabled={!selectedFile}>
              Загрузить
            </Button>
          </Box>

          <Divider />

          <Box>
            <Typography variant="h6">Проверка решения</Typography>
            <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
              <TextField
                select
                label="Тип отчета"
                value={reportType}
                onChange={(e) => setReportType(e.target.value)}
                sx={{ minWidth: 200 }}
              >
                {Object.entries(REPORT_TYPES).map(([key, value]) => (
                  <MenuItem key={key} value={value}>{key}</MenuItem>
                ))}
              </TextField>
              <Button variant="contained" onClick={handleCheck}>
                Проверить
              </Button>
            </Stack>
          </Box>
        </Stack>
      </Paper>
    </Box>
  );
};

export default StudentSubmissionPage;
