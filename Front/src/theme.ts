import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#2f81f7', // GitHub Blue for focus/links
    },
    secondary: {
      main: '#f78166', // GitHub Orange
    },
    background: {
      default: '#0d1117', // Deep dark
      paper: '#161b22',   // Card background
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
          color: '#e6edf3',
          '&:hover': {
            backgroundColor: '#30363d',
            borderColor: '#8b949e',
          },
        },
        containedPrimary: {
          backgroundColor: '#238636', // GitHub Green for primary actions
          borderColor: 'rgba(240, 246, 252, 0.1)',
          '&:hover': {
            backgroundColor: '#2ea043',
          },
        },
        outlinedPrimary: {
          color: '#58a6ff',
          borderColor: '#30363d',
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
          backgroundColor: '#161b22',
          border: '1px solid #30363d',
          boxShadow: 'none',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: '1px solid #30363d',
        },
        head: {
          fontWeight: 600,
          color: '#848d97',
        },
      },
    },
  },
});

export default theme;
