import React from 'react';
import { TableRow, TableCell, Typography, Button } from '@mui/material';
import type { LaboratoryWorkDto } from '../../api/generated/model';

interface StudentLabRowProps {
  lab: LaboratoryWorkDto;
  onOpen: (id: number) => void;
}

const StudentLabRow: React.FC<StudentLabRowProps> = ({ lab, onOpen }) => {
  const currentGrade = (lab.tasks || []).reduce((sum, task) => sum + (task.grade || 0), 0);
  const maxGrade = lab.maxGrade || 0;

  return (
    <TableRow key={lab.id}>
      <TableCell>{lab.number}</TableCell>
      <TableCell>{lab.topic}</TableCell>
      <TableCell>
        {currentGrade} / {maxGrade}
      </TableCell>
      <TableCell>
        {lab.tasks?.map(task => (
          <Typography key={task.id} variant="body2" sx={{ 
            color: task.status === 'GRADED' ? 'success.main' : 
                   task.status === 'SUBMITTED' ? 'info.main' : 'text.secondary' 
          }}>
            Задание {task.number}: {task.status}
          </Typography>
        ))}
      </TableCell>
      <TableCell>
        <Button variant="contained" onClick={() => onOpen(lab.id!)}>
          Открыть
        </Button>
      </TableCell>
    </TableRow>
  );
};

export default StudentLabRow;
