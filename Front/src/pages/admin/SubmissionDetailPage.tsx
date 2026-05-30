import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Box,
  CircularProgress,
  Paper,
  Stack
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { labService } from '../../services/labService';
import { graphService } from '../../services/graphService';
import { REPORT_TYPES } from '../../api/models/constants';
import type { ReportType } from '../../api/models/constants';
import { getComparisonResultState } from '../../api/utils';
import type { AdminSubmissionDto } from '../../api/generated/model';
import { GradeSubmissionDialog } from '../../components/admin/GradeSubmissionDialog';
import ReportTypeSelector from '../../components/common/ReportTypeSelector';

const SubmissionDetailPage: React.FC = () => {
  const { submissionId } = useParams<{ submissionId: string }>();
  const navigate = useNavigate();
  const [submission, setSubmission] = useState<AdminSubmissionDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [openGrade, setOpenGrade] = useState(false);
  const [reportType, setReportType] = useState<ReportType>(REPORT_TYPES.TWO_GRAPH);


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
}

  useEffect(() => {
    if (submissionId) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      loadSubmission(parseInt(submissionId));
    }
  }, [submissionId]);
;

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
        const params = { reportType } as { 
        reportType?: "TWO_GRAPH" | "MERGED_GRAPH";
        method?: "BRANCH" | "BRUTE_FORCE" | "DP" | "UNIQUE_LABEL";
      };

        const result = await graphService.checkSubmission(parseInt(submissionId), params);
        
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
        
        <Stack direction="row" spacing={2} sx={{ mt: 4 }} alignItems="center">
          <Button variant="contained" color="primary" onClick={() => setOpenGrade(true)} sx={{ height: 40 }}>
            Оценить
          </Button>
          <ReportTypeSelector 
            value={reportType}
            onChange={setReportType}
            onCheck={handleCheck}
          />
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
