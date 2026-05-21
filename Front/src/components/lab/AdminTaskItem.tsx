import React from 'react';
import { ListItem, ListItemIcon, ListItemText, Paper, Button } from '@mui/material';
import AssignmentIcon from '@mui/icons-material/Assignment';
import type { Task } from '../../api/generated/model';

interface AdminTaskItemProps {
  task: Task;
  isEditing: boolean;
  onEdit: (id: number) => void;
  className?: string;
}

const AdminTaskItem: React.FC<AdminTaskItemProps> = ({ task, isEditing, onEdit, className }) => {
  return (
    <ListItem className={className} component={Paper} sx={{ mb: 1 }}>
      <ListItemIcon>
        <AssignmentIcon color="primary" />
      </ListItemIcon>
      <ListItemText
        primary={`Задание ${task.number}`}
        secondary={task.description}
      />
      {isEditing && (
        <Button 
          size="small" 
          onClick={() => onEdit(task.id!)}
        >
          Изменить
        </Button>
      )}
    </ListItem>
  );
};

export default AdminTaskItem;
