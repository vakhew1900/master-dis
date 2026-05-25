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
import { graphService } from '../../services/graphService';
import { REPORT_TYPES } from '../../api/models/constants';
import { getComparisonResultState } from '../../api/utils';
import type { AdminSubmissionDto } from '../../api/generated/model';
import { GradeSubmissionDialog } from '../../components/admin/GradeSubmissionDialog';

const SubmissionDetailPage: React.FC = () => {
  const { submissionId } = useParams<{ submissionId: string }>();
  const navigate = useNavigate();
  const [submission, setSubmission] = useState<AdminSubmissionDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [openGrade, setOpenGrade] = useState(false);
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
      setSubmission(data as any);
    } catch (error) {
      console.error('Failed to load submission:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGrade = async (grade: number, feedback: string) => {
    if (submissionId) {
      try {
        await labService.gradeSubmission(parseInt(submissionId), grade, feedback);
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
        const result = await graphService.checkSubmission(parseInt(submissionId), { reportType: reportType as any });
        
        navigate('/comparison-result', { 
          state: getComparisonResultState(result)
        });
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
        <Typography variant="h4" gutterBottom>Решение: {submission.taskDescription}</Typography>
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
          <Button variant="contained" onClick={handleCheck}>
            Проверить (через {reportType})
          </Button>
        </Stack>
      </Paper>


      <GradeSubmissionDialog 
        open={openGrade} 
        onClose={() => setOpenGrade(false)} 
        onSave={handleGrade}
        initialGrade={submission.grade ?? 0}
        initialFeedback={submission.feedback ?? ''}
      />
    </Box>
  );
};

export default SubmissionDetailPage;
