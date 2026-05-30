import React from 'react';
import { Stack, TextField, MenuItem, Button } from '@mui/material';
import type { SxProps, Theme } from '@mui/material';
import { REPORT_TYPES } from '../../api/models/constants';
import type { ReportType } from '../../api/models/constants';

interface ReportTypeSelectorProps {
  value: ReportType;
  onChange: (value: ReportType) => void;
  onCheck: () => void;
  loading?: boolean;
  buttonText?: string;
  sx?: SxProps<Theme>;
}

const REPORT_TYPE_LABELS: Record<ReportType, string> = {
  [REPORT_TYPES.TWO_GRAPH]: 'Two Graph (Side-by-side)',
  [REPORT_TYPES.MERGED_GRAPH]: 'Merged Graph',
};

const ReportTypeSelector: React.FC<ReportTypeSelectorProps> = ({ 
  value, 
  onChange, 
  onCheck, 
  loading = false,
  buttonText = 'Проверить',
  sx 
}) => {
  return (
    <Stack direction="row" spacing={2} sx={sx} alignItems="center">
      <TextField
        select
        label="Тип отчета"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        sx={{ minWidth: 250 }}
        size="small"
      >
        {Object.entries(REPORT_TYPES).map(([key, val]) => (
          <MenuItem key={key} value={val}>
            {REPORT_TYPE_LABELS[val] || key}
          </MenuItem>
        ))}
      </TextField>
      <Button 
        variant="contained" 
        onClick={onCheck} 
        disabled={loading}
        sx={{ height: 40 }}
      >
        {loading ? 'Обработка...' : buttonText}
      </Button>
    </Stack>
  );
};

export default ReportTypeSelector;
