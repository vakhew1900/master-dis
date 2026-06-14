import React from 'react';
import { Chip, Tooltip, Box } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';

interface SubmissionStatusProps {
  isUploaded: boolean;
}

const SubmissionStatus: React.FC<SubmissionStatusProps> = ({ isUploaded }) => {
  return (
    <Box sx={{ display: 'inline-flex', verticalAlign: 'middle' }}>
      <Tooltip title={isUploaded ? "Решение загружено и готово к проверке" : "Решение еще не загружено"}>
        <Chip
          icon={isUploaded ? <CheckCircleIcon fontSize="small" /> : <ErrorIcon fontSize="small" />}
          label={isUploaded ? "Решение загружено" : "Решение не загружено"}
          color={isUploaded ? "success" : "warning"}
          variant="outlined"
          size="small"
          sx={{ height: 24, '& .MuiChip-label': { px: 1, fontSize: '0.75rem' } }}
        />
      </Tooltip>
    </Box>
  );
};

export default SubmissionStatus;
