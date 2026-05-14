import React from 'react';
import { Typography, Grid, Card, CardContent, CardActions, Button } from '@mui/material';

const AdminPage: React.FC = () => {
  return (
    <div style={{ textAlign: 'left' }}>
      <Typography variant="h4" gutterBottom component="h2">
        Admin Dashboard
      </Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h3">Labs</Typography>
              <Typography variant="body2" color="text.secondary" component="p">
                Manage laboratory works.
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small">View Labs</Button>
            </CardActions>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h3">Tasks</Typography>
              <Typography variant="body2" color="text.secondary" component="p">
                Manage student tasks and assignments.
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small">View Tasks</Button>
            </CardActions>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h3">Students</Typography>
              <Typography variant="body2" color="text.secondary" component="p">
                Monitor student progress and submissions.
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small">View Students</Button>
            </CardActions>
          </Card>
        </Grid>
      </Grid>
    </div>
  );
};

export default AdminPage;
