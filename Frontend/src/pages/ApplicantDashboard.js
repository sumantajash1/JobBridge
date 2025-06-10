import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ApplicantDashboard.css';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PersonIcon from '@mui/icons-material/Person';
import LogoutIcon from '@mui/icons-material/Logout';
import { Menu, MenuItem, ListItemIcon, ListItemText } from '@mui/material';

const ApplicantDashboard = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('jobs');
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  // Function to get cookie value by name
  const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
  };

  useEffect(() => {
    const token = getCookie('jwtToken');
    if (!token) {
      navigate('/applicant/signin');
      return;
    }
  }, [navigate]);

  const handleProfileClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleViewProfile = () => {
    handleClose();
    setActiveTab('profile');
  };

  const handleLogout = () => {
    handleClose();
    document.cookie = 'jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    navigate('/applicant/signin');
  };

  return (
    <div className="dashboard-container">
      {/* Top Header */}
      <header className="top-header">
        <h1 className="header-title">Job Portal</h1>
        <div className="header-actions">
          <button
            onClick={handleProfileClick}
            className="profile-button"
          >
            <AccountCircleIcon />
          </button>

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
        </div>
      </header>

      {/* Dashboard Body */}
      <div className="dashboard-body">
        {/* Sidebar */}
        <aside className="sidebar">
          <nav className="sidebar-list">
            <button
              className={`sidebar-item ${activeTab === 'jobs' ? 'active' : ''}`}
              onClick={() => setActiveTab('jobs')}
            >
              <span className="sidebar-text">Find New Jobs</span>
            </button>
            <button
              className={`sidebar-item ${activeTab === 'applications' ? 'active' : ''}`}
              onClick={() => setActiveTab('applications')}
            >
              <span className="sidebar-text">All Jobs Applied</span>
            </button>
          </nav>
        </aside>

        {/* Main Content */}
        <main className="main-content">
          {activeTab === 'jobs' && (
            <div className="content-section">
              <h2>Available Jobs</h2>
              {/* Blank page for now */}
            </div>
          )}

          {activeTab === 'applications' && (
            <div className="content-section">
              <h2>My Applications</h2>
              {/* Blank page for now */}
            </div>
          )}

          {activeTab === 'profile' && (
            <div className="content-section">
              <h2>My Profile</h2>
              {/* Blank page for now */}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default ApplicantDashboard; 