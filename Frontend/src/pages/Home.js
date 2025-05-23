import React from "react";
import { Typography, Button, Box, Container, Grid, Paper } from "@mui/material";
import { Link } from "react-router-dom";
import './Home.css';

const Home = () => {
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
              <Button
                variant="contained"
                color="primary"
                fullWidth
                className="home-button"
              >
                <Link to="/employer/post-job" className="home-link">
                  Hire Talent
                </Link>
              </Button>
            </Grid>
            <Grid item xs={12} sm={6} md={5} textAlign="center">
              <Button
                variant="outlined"
                color="primary"
                fullWidth
                className="home-button"
              >
                <Link to="/employee/feed" className="home-link">
                  Find Jobs
                </Link>
              </Button>
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
