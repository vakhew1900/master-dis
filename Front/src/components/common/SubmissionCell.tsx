import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Typography, Box, Stack } from '@mui/material';
import type { SubmissionDto } from '../../api/generated/model';

interface SubmissionCellProps {
  labNumber?: number;
  submission?: SubmissionDto;
}

const SubmissionCell: React.FC<SubmissionCellProps> = ({ submission }) => {
  const navigate = useNavigate();

  if (submission && submission.exists) {
    return (
      <Stack spacing={0.5} alignItems="center">
        <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 'bold' }}>
          ЛАБ {submission.labNumber}
        </Typography>
        <Button 
          size="small" 
          variant="outlined" 
          onClick={() => navigate(`/admin/student/submission/${submission.submissionId}`)}
          sx={{ minWidth: 60 }}
        >
          {submission.grade ?? '...'}
        </Button>
      </Stack>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
       {submission?.labNumber && (
         <Typography variant="caption" color="text.disabled">
           ЛАБ {submission.labNumber}
         </Typography>
       )}
       <Typography variant="body2" color="text.disabled">—</Typography>
    </Box>
  );
};

export default SubmissionCell;
