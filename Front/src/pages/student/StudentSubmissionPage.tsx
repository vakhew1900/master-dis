import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  Paper,
  Stack,
  Divider,
  CircularProgress
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { FileField } from '../../components/common/FileField';
import ReportTypeSelector from '../../components/common/ReportTypeSelector';
import { labService } from '../../services/labService';
import { graphService } from '../../services/graphService';
import { REPORT_TYPES } from '../../api/models/constants';
import type { ReportType } from '../../api/models/constants';
import { getComparisonResultState } from '../../api/utils';
import type { StudentLabDto } from '../../api/generated/model';

const StudentSubmissionPage: React.FC = () => {
  const { labId } = useParams<{ labId: string }>();
  const navigate = useNavigate();
  const [lab, setLab] = useState<StudentLabDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [reportType, setReportType] = useState<ReportType>(REPORT_TYPES.TWO_GRAPH);


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

  useEffect(() => {
    if (labId) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      loadLabData(parseInt(labId));
    }
  }, [labId]);


  const currentGrade = lab?.task?.grade || 0;

  const handleUpload = async () => {
    const taskId = lab?.task?.id;
    
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
    const taskId = lab?.task?.id;
    if (taskId) {
      try {
        const params = { reportType } as { 
        reportType?: "TWO_GRAPH" | "MERGED_GRAPH";
        method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL";
      };

        const result = await graphService.checkSolution(taskId, params);
        
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
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>{lab.description}</Typography>
            
            {lab.task && (
              <Box sx={{ mt: 2, p: 2, bgcolor: 'action.hover', borderRadius: 1 }}>
                <Typography variant="h6">Задание №{lab.task.number}</Typography>
                <Typography variant="body1">{lab.task.description}</Typography>
              </Box>
            )}
          </Box>
          <Box sx={{ textAlign: 'right', minWidth: 100 }}>
            <Typography variant="h5" color="primary">
              {currentGrade} / {lab.maxGrade || 0}
            </Typography>
            <Typography variant="caption" color="text.secondary">Баллы</Typography>
            {lab.task?.status && (
              <Typography variant="subtitle2" sx={{ 
                mt: 1,
                color: lab.task.status === 'GRADED' ? 'success.main' : 
                       lab.task.status === 'SUBMITTED' ? 'info.main' : 'text.secondary' 
              }}>
                {lab.task.status}
              </Typography>
            )}
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
            <ReportTypeSelector 
              value={reportType}
              onChange={setReportType}
              onCheck={handleCheck}
              sx={{ mt: 2 }}
            />
          </Box>
        </Stack>
      </Paper>
    </Box>
  );
};

export default StudentSubmissionPage;
