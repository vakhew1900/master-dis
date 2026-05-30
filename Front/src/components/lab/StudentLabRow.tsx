import React from 'react';
import { TableRow, TableCell, Typography, Button } from '@mui/material';
import type { StudentLabDto } from '../../api/generated/model';

interface StudentLabRowProps {
  lab: StudentLabDto;
  onOpen: (id: number) => void;
}

const StudentLabRow: React.FC<StudentLabRowProps> = ({ lab, onOpen }) => {
  const task = lab.task;
  const currentGrade = task?.grade || 0;
  const maxGrade = lab.maxGrade || 0;

  return (
    <TableRow key={lab.id}>
      <TableCell>{lab.number}</TableCell>
      <TableCell>
        <Typography variant="body1">{lab.topic}</Typography>
        <Typography variant="caption" color="text.secondary">{lab.description}</Typography>
      </TableCell>
      <TableCell>
        {currentGrade} / {maxGrade}
      </TableCell>
      <TableCell>
        {task ? (
          <Typography variant="body2" sx={{ 
            color: task.status === 'GRADED' ? 'success.main' : 
                   task.status === 'SUBMITTED' ? 'info.main' : 'text.secondary' 
          }}>
            Задание {task.number}: {task.status}
          </Typography>
        ) : (
          <Typography variant="body2" color="text.secondary">Нет задания</Typography>
        )}
      </TableCell>
      <TableCell>
        <Button 
          variant="contained" 
          onClick={() => onOpen(lab.id!)}
          disabled={!task}
        >
          Открыть
        </Button>
      </TableCell>
    </TableRow>
  );
};

export default StudentLabRow;
