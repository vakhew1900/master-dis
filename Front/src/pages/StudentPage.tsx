import React from 'react';
import { Typography, Paper, List, ListItem, ListItemText, Divider, Button } from '@mui/material';

const StudentPage: React.FC = () => {
  return (
    <div style={{ textAlign: 'left' }}>
      <Typography variant="h4" gutterBottom component="h2">
        My Assignments
      </Typography>
      <Paper elevation={2} component="div">
        <List>
          <ListItem>
            <ListItemText
              primary="Lab 1: Git Basics"
              secondary="Due: 2026-05-20"
            />
            <Button variant="outlined">View Details</Button>
          </ListItem>
          <Divider />
          <ListItem>
            <ListItemText
              primary="Lab 2: Branching and Merging"
              secondary="Due: 2026-06-01"
            />
            <Button variant="outlined">View Details</Button>
          </ListItem>
        </List>
      </Paper>
    </div>
  );
};

export default StudentPage;
