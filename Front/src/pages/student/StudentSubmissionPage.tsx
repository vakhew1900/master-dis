import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  Paper,
  Stack,
  TextField,
  MenuItem
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { FileField } from '../../components/common/FileField';
import { labService } from '../../services/labService';
import { graphService } from '../../services/graphService';
import { REPORT_TYPES } from '../../api/models/constants';
import type { LaboratoryWork } from '../../api/generated/model';

const StudentSubmissionPage: React.FC = () => {
  const { labId } = useParams<{ labId: string }>();
  const navigate = useNavigate();
  const [lab, setLab] = useState<LaboratoryWork | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [reportType, setReportType] = useState<string>(REPORT_TYPES.TWO_GRAPH);

  useEffect(() => {
    if (labId) {
      loadLabData(parseInt(labId));
    }
  }, [labId]);

  const loadLabData = async (id: number) => {
    const data = await labService.getLabById(id);
    setLab(data);
  };

  const handleUpload = async () => {
    if (selectedFile && labId) {
      try {
        await labService.uploadSolution(parseInt(labId), selectedFile);
        alert('Решение загружено');
      } catch (error) {
        console.error('Upload failed:', error);
      }
    }
  };

  const handleCheck = async () => {
    if (labId) {
      try {
        const result = await graphService.checkSolution(parseInt(labId), { reportType: reportType as any });
        if (result.type === 'MergedGraphComparisonResultDto') {
          navigate('/comparison-result', { state: { result, type: 'merged' } });
        } else if (result.type === 'TwoGraphComparisonResultDto') {
          navigate('/comparison-result', { state: { result, type: 'two' } });
        }
      } catch (error) {
        console.error('Check failed:', error);
      }
    }
  };

  if (!lab) return <Typography>Загрузка...</Typography>;

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
        Назад к списку
      </Button>

      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>Лабораторная работа №{lab.number}: {lab.topic}</Typography>
        <Typography variant="body1" sx={{ mb: 3 }}>{lab.description}</Typography>
        
        <Stack spacing={3} sx={{ mt: 3 }}>
          <Box>
            <Typography variant="h6">Загрузить решение</Typography>
            <FileField 
              label={selectedFile ? selectedFile.name : "Выберите ZIP-архив"} 
              onChange={(f) => setSelectedFile(f)} 
            />
            <Button variant="contained" onClick={handleUpload} sx={{ mt: 1 }}>Загрузить</Button>
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
              <Button variant="outlined" color="secondary" onClick={handleCheck}>
                Проверить
              </Button>
            </Stack>
          </Box>
        </Stack>
      </Paper>
    </Box>
  );
};

// Simple Divider since I didn't import it in this file
const Divider = () => <Box sx={{ my: 2, borderBottom: '1px solid #ddd' }} />;

export default StudentSubmissionPage;
