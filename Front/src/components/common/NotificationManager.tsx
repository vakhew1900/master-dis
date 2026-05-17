import React from 'react';
import { Snackbar, Alert } from '@mui/material';
import type { AlertColor } from '@mui/material';

interface NotificationState {
  open: boolean;
  message: string;
  severity: AlertColor;
}

export const useNotification = () => {
  const [state, setState] = React.useState<NotificationState>({
    open: false,
    message: '',
    severity: 'success',
  });

  const showNotification = (message: string, severity: AlertColor = 'info') => {
    setState({ open: true, message, severity });
  };

  const hideNotification = () => {
    setState({ ...state, open: false });
  };

  const NotificationComponent = (
    <Snackbar 
      open={state.open} 
      autoHideDuration={4000} 
      onClose={hideNotification}
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
    >
      <Alert onClose={hideNotification} severity={state.severity} sx={{ width: '100%' }}>
        {state.message}
      </Alert>
    </Snackbar>
  );

  return { showNotification, NotificationComponent };
};
