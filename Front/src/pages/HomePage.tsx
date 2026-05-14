import React from 'react';
import { Typography, Box, Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const HomePage: React.FC = () => {
  return (
    <Box sx={{ py: 4, textAlign: 'center' }} component="div">
      <Typography variant="h2" gutterBottom sx={{ fontWeight: 'bold' }} component="h1">
        Welcome to JServer
      </Typography>
      <Typography variant="h5" color="text.secondary" sx={{ mb: 2 }} component="p">
        Experimental comparison of methods for finding the Maximum Common Transitive Subgraph.
      </Typography>
      <Box sx={{ mt: 4, display: 'flex', justifyContent: 'center', gap: 2 }} component="div">
        <Button variant="contained" size="large" component={RouterLink} to="/student">
          I am a Student
        </Button>
        <Button variant="outlined" size="large" component={RouterLink} to="/admin">
          I am an Admin
        </Button>
      </Box>
    </Box>
  );
};

export default HomePage;
