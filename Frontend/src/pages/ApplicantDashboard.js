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
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [profile, setProfile] = useState({
    name: '',
    email: '',
    mobileNumber: '',
    skills: [],
    experience: '',
    education: ''
  });
  const [anchorEl, setAnchorEl] = useState(null);

  const open = Boolean(anchorEl);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    console.log('Dashboard - Checking token:', token);
    if (!token) {
      console.log('Dashboard - No token found, redirecting to sign in');
      navigate('/applicant/signin');
      return;
    }
    console.log('Dashboard - Token found, fetching user data');
    fetchUserData();
  }, [navigate]);

  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      console.log('Dashboard - Fetching data with token:', token);
      const profileResponse = await fetch('http://localhost:8080/applicant/profile', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log('Dashboard - Profile response status:', profileResponse.status);
      const profileData = await profileResponse.json();
      console.log('Dashboard - Profile data:', profileData);
      setProfile(profileData);
      const jobsResponse = await fetch('http://localhost:8080/jobs', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log('Dashboard - Jobs response status:', jobsResponse.status);
      const jobsData = await jobsResponse.json();
      console.log('Dashboard - Jobs data:', jobsData);
      setJobs(jobsData);
      const applicationsResponse = await fetch('http://localhost:8080/applicant/applications', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      console.log('Dashboard - Applications response status:', applicationsResponse.status);
      const applicationsData = await applicationsResponse.json();
      console.log('Dashboard - Applications data:', applicationsData);
      setApplications(applicationsData);
    } catch (error) {
      console.error('Dashboard - Error fetching user data:', error);
    }
  };

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
    localStorage.removeItem('jwtToken');
    navigate('/applicant/signin');
  };

  const handleApplyJob = async (jobId) => {
    try {
      const response = await fetch('http://localhost:8080/applicant/apply', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
        body: JSON.stringify({ jobId })
      });

      if (response.ok) {
        fetchUserData();
      }
    } catch (error) {
      console.error('Error applying for job:', error);
    }
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
              <div className="jobs-grid">
                {jobs.map((job) => (
                  <div key={job.id} className="job-card">
                    <h3>{job.title}</h3>
                    <p className="company">{job.company}</p>
                    <p className="location">{job.location}</p>
                    <p className="salary">{job.salary}</p>
                    <div className="skills">
                      {job.skills.map((skill, index) => (
                        <span key={index} className="skill-badge">
                          {skill}
                        </span>
                      ))}
                    </div>
                    <button
                      className="apply-btn"
                      onClick={() => handleApplyJob(job.id)}
                      disabled={applications.some(app => app.jobId === job.id)}
                    >
                      {applications.some(app => app.jobId === job.id) ? 'Applied' : 'Apply Now'}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'applications' && (
            <div className="content-section">
              <h2>My Applications</h2>
              <div className="applications-list">
                {applications.map((application) => (
                  <div key={application.id} className="application-card">
                    <h3>{application.jobTitle}</h3>
                    <p className="company">{application.company}</p>
                    <p className="status">Status: {application.status}</p>
                    <p className="applied-date">Applied on: {new Date(application.appliedDate).toLocaleDateString()}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'profile' && (
            <div className="content-section">
              <h2>My Profile</h2>
              <div className="profile-card">
                <div className="profile-section">
                  <h3>Personal Information</h3>
                  <p><strong>Name:</strong> {profile.name}</p>
                  <p><strong>Email:</strong> {profile.email}</p>
                  <p><strong>Mobile:</strong> {profile.mobileNumber}</p>
                </div>
                <div className="profile-section">
                  <h3>Skills</h3>
                  <div className="skills">
                    {profile.skills.map((skill, index) => (
                      <span key={index} className="skill-badge">
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>
                <div className="profile-section">
                  <h3>Experience</h3>
                  <p>{profile.experience}</p>
                </div>
                <div className="profile-section">
                  <h3>Education</h3>
                  <p>{profile.education}</p>
                </div>
                <button className="edit-profile-btn">Edit Profile</button>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default ApplicantDashboard; 