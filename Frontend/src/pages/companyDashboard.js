import React, { useEffect, useState } from 'react';
import { 
  Box, 
  Typography, 
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  List,
  ListItem,
  ListItemButton,
  Drawer
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import WorkOutlineIcon from '@mui/icons-material/WorkOutline';
import HistoryIcon from '@mui/icons-material/History';
import PeopleIcon from '@mui/icons-material/People';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import JobPostingForm from './PostJobs';
import './companyDashboard.css';

const PostNewJob = () => (
  <Box>
    <JobPostingForm />
  </Box>
);

const ActiveJobs = () => (
  <Box>
    <Typography variant="h4" gutterBottom>Active Job Posts</Typography>
    <Typography>List of active job postings will be displayed here...</Typography>
  </Box>
);

const PreviousJobs = () => (
  <Box>
    <Typography variant="h4" gutterBottom>Previous Job Posts</Typography>
    <Typography>Historical job postings will be shown here...</Typography>
  </Box>
);

const SelectedApplicants = () => (
  <Box>
    <Typography variant="h4" gutterBottom>Selected Applicants</Typography>
    <Typography>List of selected candidates will appear here...</Typography>
  </Box>
);

const Dashboard = () => (
  <Box>
    <Typography variant="h4" gutterBottom>Welcome to Company Dashboard</Typography>
    <Typography>Select an option from the sidebar to get started.</Typography>
  </Box>
);

const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

const CompanyDashboard = () => {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedSection, setSelectedSection] = useState('dashboard');
  const open = Boolean(anchorEl);

  useEffect(() => {
    // Check if user is authenticated
    const token = getCookie('jwtToken');
    if (!token) {
      console.log('No authentication token found, redirecting to signin');
      navigate('/employer/signin', { replace: true });
      return;
    }

    // Verify token with backend
    const verifyToken = async () => {
      try {
        const response = await fetch('http://localhost:8080/Company/verifyCompanyToken', {
          method: 'GET',
          credentials: 'include',
          headers: {
            'Accept': '*/*',
            'Connection': 'keep-alive',
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.status === 403) {
          console.log('Method is forbidden, redirecting to signin');
          document.cookie = 'jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
          navigate('/employer/signin', { replace: true });
          return;
        }

        const responseText = await response.text();
        if (responseText !== "companyTokenIsValid") {
          console.log('Token verification failed, redirecting to signin');
          document.cookie = 'jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
          navigate('/employer/signin', { replace: true });
          return;
        }
      } catch (error) {
        console.error('Error verifying token:', error);
        document.cookie = 'jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        navigate('/employer/signin', { replace: true });
      }
    };

    verifyToken();
  }, [navigate]);

  const sidebarOptions = [
    {
      id: 'post-job',
      title: 'Post a New Job',
      icon: <AddCircleOutlineIcon />,
      component: PostNewJob
    },
    {
      id: 'active-jobs',
      title: 'Active Job Posts',
      icon: <WorkOutlineIcon />,
      component: ActiveJobs
    },
    {
      id: 'previous-jobs',
      title: 'Previous Job Posts',
      icon: <HistoryIcon />,
      component: PreviousJobs
    },
    {
      id: 'selected-applicants',
      title: 'Selected Applicants',
      icon: <PeopleIcon />,
      component: SelectedApplicants
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
    // Clear both the JWT token and company name
    document.cookie = 'companyToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    localStorage.removeItem('companyName');
    navigate('/employer/signin', { replace: true });
  };

  const handleSectionChange = (sectionId) => {
    setSelectedSection(sectionId);
  };

  const renderMainContent = () => {
    const selectedOption = sidebarOptions.find(option => option.id === selectedSection);
    if (selectedOption) {
      const Component = selectedOption.component;
      return <Component />;
    }
    return <Dashboard />;
  };

  return (
    <Box className="dashboard-container">
      {/* Top Header Bar */}
      <Box className="top-header">
        <Typography className="header-title">Company Dashboard</Typography>
        
        <Box className="header-actions">
          <IconButton
            onClick={handleProfileClick}
            className="profile-button"
          >
            <AccountCircleIcon />
          </IconButton>

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

      <Box className="dashboard-body">
        {/* Sidebar */}
        <Box className="sidebar">
          <List className="sidebar-list">
            {sidebarOptions.map((option) => (
              <ListItem key={option.id} disablePadding>
                <ListItemButton
                  className={`sidebar-item ${selectedSection === option.id ? 'active' : ''}`}
                  onClick={() => handleSectionChange(option.id)}
                >
                  <ListItemIcon className="sidebar-icon">
                    {option.icon}
                  </ListItemIcon>
                  <ListItemText 
                    primary={option.title}
                    className="sidebar-text"
                  />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Box>

        {/* Main Content Area */}
        <Box className="main-content">
          {renderMainContent()}
        </Box>
      </Box>
    </Box>
  );
};

export default CompanyDashboard;