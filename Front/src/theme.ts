import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#4b6eaf', // --nav-active
    },
    background: {
      default: '#1e1e1e', // --bg-color
      paper: '#2b2b2b',   // --panel-bg
    },
    text: {
      primary: '#a9b7c6', // --text-main
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundColor: '#2b2b2b', // --panel-bg
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          backgroundColor: '#323232', // --card-bg
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          color: '#ffffff', // --text-header
          fontWeight: 600,
        },
        root: {
          color: '#a9b7c6', // --text-main
          borderBottom: '1px solid #3f3f3f', // --border-color
        }
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
  },
});

export default theme;
