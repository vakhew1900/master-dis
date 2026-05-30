import React from 'react';
import { TableRow, TableCell, Box } from '@mui/material';
import type { UserResponseDto, SubmissionDto } from '../../api/generated/model';
import SubmissionCell from '../common/SubmissionCell';
import { ActionsMenu } from '../common/ActionsMenu';

interface StudentRowProps {
  student: UserResponseDto;
  onDelete: (id: number) => void;
}

const StudentRow: React.FC<StudentRowProps> = ({ student, onDelete }) => {
  return (
    <TableRow>
      <TableCell>{student.username}</TableCell>
      <TableCell>{`${student.lastName || ''} ${student.firstName || ''}`.trim()}</TableCell>
      <TableCell>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          {(student.submissions || []).map((sub: SubmissionDto, index: number) => (
            <SubmissionCell key={index} submission={sub} />
          ))}
        </Box>
      </TableCell>
      <TableCell align="right">
        <ActionsMenu onDelete={() => onDelete(student.id!)} />
      </TableCell>
    </TableRow>
  );
};

export default StudentRow;
