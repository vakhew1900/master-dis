import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';

interface GradeSubmissionDialogProps {
  open: boolean;
  onClose: () => void;
  onSave: (grade: number, feedback: string) => void;
  initialGrade?: number;
  initialFeedback?: string;
}

export const GradeSubmissionDialog: React.FC<GradeSubmissionDialogProps> = ({ 
  open, 
  onClose, 
  onSave, 
  initialGrade = 0, 
  initialFeedback = '' 
}) => {
  const [grade, setGrade] = useState(initialGrade.toString());
  const [feedback, setFeedback] = useState(initialFeedback);

  const handleSave = () => {
    onSave(parseFloat(grade), feedback);
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Выставить оценку</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
        <TextField 
          label="Оценка" type="number" fullWidth 
          value={grade} 
          onChange={(e) => setGrade(e.target.value)} 
        />
        <TextField 
          label="Отзыв" multiline rows={4} fullWidth 
          value={feedback} 
          onChange={(e) => setFeedback(e.target.value)} 
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Отмена</Button>
        <Button onClick={handleSave} variant="contained">Сохранить</Button>
      </DialogActions>
    </Dialog>
  );
};
