import React from 'react';
import { 
  Box, 
  Container, 
  Typography, 
  Grid, 
  Card, 
  CardContent, 
  CardActions, 
  Button,
  Paper
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import WorkOutlineIcon from '@mui/icons-material/WorkOutline';
import HistoryIcon from '@mui/icons-material/History';
import PeopleIcon from '@mui/icons-material/People';
import './companyDashboard.css';

const CompanyDashboard = () => {
  const navigate = useNavigate();

  const dashboardOptions = [
    {
      title: 'Create New Job Post',
      description: 'Post a new job opening and attract potential candidates',
      icon: <AddCircleOutlineIcon sx={{ fontSize: 40 }} />,
      path: '/post-job',
      color: '#2196f3'
    },
    {
      title: 'Active Job Posts',
      description: 'View and manage your currently active job listings',
      icon: <WorkOutlineIcon sx={{ fontSize: 40 }} />,
      path: '/active-jobs',
      color: '#4caf50'
    },
    {
      title: 'Previous Job Posts',
      description: 'Access your historical job postings and their results',
      icon: <HistoryIcon sx={{ fontSize: 40 }} />,
      path: '/previous-jobs',
      color: '#ff9800'
    },
    {
      title: 'Selected Applicants',
      description: 'View all candidates who have been selected for positions',
      icon: <PeopleIcon sx={{ fontSize: 40 }} />,
      path: '/selected-applicants',
      color: '#9c27b0'
    }
  ];

  return (
    <Box className="dashboard-container">
      <Container maxWidth="lg">
        <Paper elevation={3} className="dashboard-paper">
          <Typography variant="h4" component="h1" gutterBottom className="dashboard-title">
            Company Dashboard
          </Typography>
          
          <Grid container spacing={4} className="dashboard-grid">
            {dashboardOptions.map((option, index) => (
              <Grid item xs={12} sm={6} md={6} key={index}>
                <Card 
                  className="dashboard-card"
                  sx={{ 
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    transition: 'transform 0.2s',
                    '&:hover': {
                      transform: 'translateY(-5px)',
                      boxShadow: 3
                    }
                  }}
                >
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Box className="card-icon" sx={{ color: option.color }}>
                      {option.icon}
                    </Box>
                    <Typography variant="h6" component="h2" gutterBottom>
                      {option.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {option.description}
                    </Typography>
                  </CardContent>
                  <CardActions>
                    <Button 
                      size="small" 
                      color="primary"
                      onClick={() => navigate(option.path)}
                      fullWidth
                    >
                      View Details
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Paper>
      </Container>
    </Box>
  );
};

export default CompanyDashboard; 