import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  Grid, 
  Card, 
  CardContent, 
  CardActions, 
  Button,
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import WorkOutlineIcon from '@mui/icons-material/WorkOutline';
import HistoryIcon from '@mui/icons-material/History';
import PeopleIcon from '@mui/icons-material/People';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import './companyDashboard.css';

const CompanyDashboard = () => {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  useEffect(() => {
    // Check if user is authenticated
    const token = localStorage.getItem('companyToken');
    if (!token) {
      navigate('/employer/signin', { replace: true });
    }
  }, [navigate]);

  const dashboardOptions = [
    {
      title: 'Post a new Job',
      description: 'Post a new job opening and attract potential candidates',
      icon: <AddCircleOutlineIcon sx={{ fontSize: 24 }} />,
      path: '/PostJobs',
      color: '#3b82f6',
      buttonText: 'Create New Job'
    },
    {
      title: 'Active Job Posts',
      description: 'View and manage your currently active job listings',
      icon: <WorkOutlineIcon sx={{ fontSize: 24 }} />,
      path: '/active-jobs',
      color: '#3b82f6'
    },
    {
      title: 'Previous Job Posts',
      description: 'Access your historical job postings and their results',
      icon: <HistoryIcon sx={{ fontSize: 24 }} />,
      path: '/previous-jobs',
      color: '#3b82f6'
    },
    {
      title: 'Selected Applicants',
      description: 'View all candidates who have been selected for positions',
      icon: <PeopleIcon sx={{ fontSize: 24 }} />,
      path: '/selected-applicants',
      color: '#3b82f6'
    }
  ];

  const handleProfileClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleViewProfile = () => {
    handleClose();
    navigate('/company-profile');
  };

  const handleLogout = () => {
    handleClose();
    localStorage.removeItem('companyToken');
    navigate('/employer/signin', { replace: true });
  };

  return (
    <Box className="dashboard-container">
      {/* Main Content */}
      <Box className="dashboard-main">
        {/* Header */}
        <Box className="dashboard-header">
          <Typography className="header-title">Company Dashboard</Typography>

          <Box className="header-actions">
            {/* Profile Button */}
            <IconButton
              onClick={handleProfileClick}
              className="profile-button"
            >
              <AccountCircleIcon />
            </IconButton>

            {/* Profile Menu */}
            <Menu
              anchorEl={anchorEl}
              open={open}
              onClose={handleClose}
              className="profile-menu"
            >
              <MenuItem onClick={handleViewProfile} className="profile-menu-item">
                <ListItemIcon>
                  <PersonIcon fontSize="small" />
                </ListItemIcon>
                <ListItemText>View Profile</ListItemText>
              </MenuItem>
              <MenuItem onClick={handleLogout} className="profile-menu-item">
                <ListItemIcon>
                  <LogoutIcon fontSize="small" />
                </ListItemIcon>
                <ListItemText>Logout</ListItemText>
              </MenuItem>
            </Menu>
          </Box>
        </Box>

        {/* Dashboard Content */}
        <Box className="dashboard-grid">
          {dashboardOptions.map((option, index) => (
            <Card key={index} className="dashboard-card">
              <CardContent>
                <Box className="card-header">
                  <Typography className="card-title">{option.title}</Typography>
                  <Box className="card-icon" sx={{ color: option.color }}>
                    {option.icon}
                  </Box>
                </Box>
                <Typography className="card-description">
                  {option.description}
                </Typography>
              </CardContent>
              <CardActions className="card-actions">
                <Button
                  className="dashboard-button"
                  onClick={() => navigate(option.path)}
                  startIcon={option.icon}
                >
                  {option.buttonText || 'View Details'}
                </Button>
              </CardActions>
            </Card>
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export default CompanyDashboard;
