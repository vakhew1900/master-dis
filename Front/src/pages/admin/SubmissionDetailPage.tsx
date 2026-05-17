import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  CircularProgress,
  Paper,
  TextField,
  Stack,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { labService } from '../../services/labService';
import { REPORT_TYPES } from '../../api/models/constants';
import type { components } from 'та../../api/models/schema';

type SubmissionDto = components["schemas"]["StudentSubmission"];

const SubmissionDetailPage: React.FC = () => {
  const { submissionId } = useParams<{ submissionId: string }>();
  const navigate = useNavigate();
  const [submission, setSubmission] = useState<SubmissionDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [openGrade, setOpenGrade] = useState(false);
  const [grade, setGrade] = useState('');
  const [feedback, setFeedback] = useState('');
  const [reportType, setReportType] = useState<string>(REPORT_TYPES.TWO_GRAPH);

  useEffect(() => {
    if (submissionId) {
      loadSubmission(parseInt(submissionId));
    }
  }, [submissionId]);

  const loadSubmission = async (id: number) => {
    setLoading(true);
    try {
      const data = await labService.getSubmissionById(id);
      setSubmission(data);
    } catch (error) {
      console.error('Failed to load submission:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGrade = async () => {
    if (submissionId) {
      try {
        await labService.gradeSubmission(parseInt(submissionId), parseFloat(grade), feedback);
        setOpenGrade(false);
        loadSubmission(parseInt(submissionId));
      } catch (error) {
        console.error('Failed to grade submission:', error);
      }
    }
  };

  const handleCheck = async () => {
    if (submissionId) {
      try {
        const result = await labService.checkSubmission(parseInt(submissionId), { reportType });
        
        if (result.type === 'MergedGraphComparisonResultDto') {
          navigate('/comparison-result', { state: { result, type: 'merged' } });
        } else if (result.type === 'TwoGraphComparisonResultDto') {
          navigate('/comparison-result', { state: { result, type: 'two' } });
        }
      } catch (error) {
        console.error('Failed to check submission:', error);
      }
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!submission) return <Typography>Решение не найдено</Typography>;

  return (
    <Box sx={{ p: 3 }}>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mb: 3 }}>
        Назад к студентам
      </Button>

      <Paper sx={{ p: 4 }}>
        <Typography variant="h4">Решение: {submission.task?.description}</Typography>
        <Typography variant="subtitle1" sx={{ mt: 2 }}>Студент: {submission.student?.lastName} {submission.student?.firstName}</Typography>
        <Typography variant="body1" sx={{ mt: 2 }}>Оценка: {submission.grade ?? 'Не оценено'}</Typography>
        <Typography variant="body2" sx={{ mt: 1 }}>Отзыв: {submission.feedback || 'Нет отзыва'}</Typography>
        
        <Stack direction="row" spacing={2} sx={{ mt: 4 }}>
          <Button variant="contained" color="primary" onClick={() => setOpenGrade(true)}>
            Оценить
          </Button>
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
            Проверить (через {reportType})
          </Button>
        </Stack>
      </Paper>

      <Dialog open={openGrade} onClose={() => setOpenGrade(false)}>
        <DialogTitle>Выставить оценку</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField 
            label="Оценка" type="number" fullWidth 
            value={grade} 
            onChange={(e) => setGrade(e.target.value)} 
          />
          <TextField 
            label="Отзыв" multiline rows={4} fullWidth 
            value={feedback} 
            onChange={(e) => setFeedback(e.target.value)} 
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenGrade(false)}>Отмена</Button>
          <Button onClick={handleGrade} variant="contained">Сохранить</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default SubmissionDetailPage;
