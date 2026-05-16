import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Typography } from '@mui/material';
import type { components } from '../../api/models/schema';

type SubmissionDto = components["schemas"]["SubmissionDto"];

interface SubmissionCellProps {
  labNumber: number;
  submission?: SubmissionDto;
}

const SubmissionCell: React.FC<SubmissionCellProps> = ({ labNumber, submission }) => {
  const navigate = useNavigate();

  // Note: 'exists' property in SubmissionDto is boolean (or undefined)
  if (submission && submission.exists) {
    return (
      <Button 
        size="small" 
        variant="outlined" 
        onClick={() => navigate(`/admin/student/submission/${submission.submissionId}`)}
      >
        {submission.grade ?? 'Оценка'}
      </Button>
    );
  }

  return (
    <Typography variant="body2" color="text.secondary">—</Typography>
  );
};

export default SubmissionCell;
