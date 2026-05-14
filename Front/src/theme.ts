import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#1f6feb', // GitHub Blue
    },
    secondary: {
      main: '#f78166', // GitHub Orange
    },
    background: {
      default: '#0d1117',
      paper: '#161b22',
    },
    text: {
      primary: '#e6edf3',
      secondary: '#848d97',
    },
    divider: '#30363d',
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    button: {
      textTransform: 'none',
      fontWeight: 500,
    },
  },
  shape: {
    borderRadius: 6,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          border: '1px solid #30363d',
          backgroundColor: '#21262d',
          '&:hover': {
            backgroundColor: '#30363d',
            borderColor: '#8b949e',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          border: '1px solid #30363d',
        },
      },
    },
  },
});

export default theme;
