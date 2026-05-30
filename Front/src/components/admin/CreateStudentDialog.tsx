import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';
import type { UserCreateDto } from '../../api/generated/model';
import { validateLogin, validatePassword } from '../../utils/validation';

interface CreateStudentDialogProps {
  open: boolean;
  onClose: () => void;
  onSave: (student: UserCreateDto) => void;
}

export const CreateStudentDialog: React.FC<CreateStudentDialogProps> = ({ open, onClose, onSave }) => {
  const [student, setStudent] = useState<UserCreateDto>({ username: '', firstName: '', lastName: '', password: '' });
  const [errors, setErrors] = useState<Record<string, string | null>>({});

  const handleSave = () => {
    const usernameError = validateLogin(student.username!);
    const passwordError = validatePassword(student.password || '');
    
    if (usernameError || passwordError) {
      setErrors({
        username: usernameError,
        password: passwordError
      });
      return;
    }

    onSave(student);
    setStudent({ username: '', firstName: '', lastName: '', password: '' });
    setErrors({});
  };

  const handleClose = () => {
    setStudent({ username: '', firstName: '', lastName: '', password: '' });
    setErrors({});
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Добавить студента</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
        <TextField 
          label="Логин" 
          fullWidth 
          value={student.username}
          error={!!errors.username}
          helperText={errors.username}
          onChange={(e) => {
            const val = e.target.value;
            setStudent({...student, username: val});
            if (errors.username) setErrors({...errors, username: validateLogin(val)});
          }} 
        />
        <TextField 
          label="Фамилия" 
          fullWidth 
          value={student.lastName}
          onChange={(e) => setStudent({...student, lastName: e.target.value})} 
        />
        <TextField 
          label="Имя" 
          fullWidth 
          value={student.firstName}
          onChange={(e) => setStudent({...student, firstName: e.target.value})} 
        />
        <TextField 
          label="Пароль" 
          type="password" 
          fullWidth 
          value={student.password}
          error={!!errors.password}
          helperText={errors.password}
          onChange={(e) => {
            const val = e.target.value;
            setStudent({...student, password: val});
            if (errors.password) setErrors({...errors, password: validatePassword(val)});
          }} 
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Отмена</Button>
        <Button variant="contained" onClick={handleSave}>Добавить</Button>
      </DialogActions>
    </Dialog>
  );
};
