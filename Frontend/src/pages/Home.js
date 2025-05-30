import React from "react";
import { Typography, Box, Container, Grid, Paper } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import './Home.css';

const Home = () => {
  const navigate = useNavigate();

  const handleHireTalentClick = (e) => {
    e.preventDefault();
    const companyToken = localStorage.getItem('companyToken');
    if (companyToken) {
      navigate('/employer/dashboard');
    } else {
      navigate('/employer/signin');
    }
  };

  return (
    <Box className="home-container">
      <Container maxWidth="md">
        <Paper className="home-paper">
          <Typography
            variant="h2"
            align="center"
            gutterBottom
            className="home-title"
          >
            Get Hired or Hire People for Free!
          </Typography>

          <Typography
            variant="h6"
            align="center"
            color="text.secondary"
            className="home-subtitle"
          >
            Connect with top talent or find your dream job on our platform
          </Typography>

          <Grid container spacing={4} justifyContent="center">
            <Grid item xs={12} sm={6} md={5} textAlign="center">
              <Link to="/employer/signin" className="home-button primary" onClick={handleHireTalentClick}>
                Hire Talent
              </Link>
            </Grid>
            <Grid item xs={12} sm={6} md={5} textAlign="center">
              <Link to="/applicant/signin" className="home-button secondary">
                Find Jobs
              </Link>
            </Grid>
          </Grid>

          <Box className="home-footer">
            <Typography variant="body1" color="text.secondary">
              Join thousands of companies and professionals already using our platform
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Home;
