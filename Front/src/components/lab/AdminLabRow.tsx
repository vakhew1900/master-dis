import React from 'react';
import { TableRow, TableCell, IconButton } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import type { LaboratoryWork } from '../../api/generated/model';

interface AdminLabRowProps {
  lab: LaboratoryWork;
  onClick: (id: number) => void;
  onMenuOpen: (event: React.MouseEvent<HTMLButtonElement>, id: number) => void;
  className?: string;
  actionButtonClassName?: string;
  descriptionCellClassName?: string;
}

const AdminLabRow: React.FC<AdminLabRowProps> = ({
  lab,
  onClick,
  onMenuOpen,
  className,
  actionButtonClassName,
  descriptionCellClassName
}) => {
  return (
    <TableRow
      key={lab.id}
      className={className}
      onClick={() => onClick(lab.id!)}
      hover
      sx={{ cursor: 'pointer' }}
    >
      <TableCell component="th" scope="row">
        {lab.number}
      </TableCell>
      <TableCell>{lab.topic}</TableCell>
      <TableCell className={descriptionCellClassName}>
        {lab.description}
      </TableCell>
      <TableCell align="right">{lab.maxGrade || 0}</TableCell>
      <TableCell align="right">{lab.tasks?.length || 0}</TableCell>
      <TableCell align="right" onClick={(e) => e.stopPropagation()}>
        <IconButton
          className={actionButtonClassName}
          onClick={(e) => onMenuOpen(e, lab.id!)}
        >
          <MoreVertIcon />
        </IconButton>
      </TableCell>
    </TableRow>
  );
};

export default AdminLabRow;
