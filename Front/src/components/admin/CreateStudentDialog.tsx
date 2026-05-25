import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';
import type { UserCreateDto } from '../../api/generated/model';

interface CreateStudentDialogProps {
  open: boolean;
  onClose: () => void;
  onSave: (student: UserCreateDto) => void;
}

export const CreateStudentDialog: React.FC<CreateStudentDialogProps> = ({ open, onClose, onSave }) => {
  const [student, setStudent] = useState<UserCreateDto>({ username: '', firstName: '', lastName: '', password: '' });

  const handleSave = () => {
    onSave(student);
    setStudent({ username: '', firstName: '', lastName: '', password: '' });
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Добавить студента</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
        <TextField label="Логин" fullWidth onChange={(e) => setStudent({...student, username: e.target.value})} />
        <TextField label="Фамилия" fullWidth onChange={(e) => setStudent({...student, lastName: e.target.value})} />
        <TextField label="Имя" fullWidth onChange={(e) => setStudent({...student, firstName: e.target.value})} />
        <TextField label="Пароль" type="password" fullWidth onChange={(e) => setStudent({...student, password: e.target.value})} />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Отмена</Button>
        <Button variant="contained" onClick={handleSave}>Добавить</Button>
      </DialogActions>
    </Dialog>
  );
};
