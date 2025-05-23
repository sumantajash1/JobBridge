import React from "react";
import { Typography, Button, Box, Container, Grid, Paper } from "@mui/material";
import { Link } from "react-router-dom";
import { styled } from "@mui/material/styles";

const StyledButton = styled(Button)(({ theme }) => ({
  padding: "12px 30px",
  borderRadius: "30px",
  fontWeight: "bold",
  fontSize: "1.1rem",
  transition: "transform 0.3s ease",
  "&:hover": {
    transform: "translateY(-3px)",
    boxShadow: "0 10px 20px rgba(0,0,0,0.1)",
  }
}));

const StyledLink = styled(Link)({
  textDecoration: "none",
  color: "inherit",
  display: "block",
  width: "100%",
});

const Home = () => {
  return (
    <Box
      sx={{
        backgroundColor: "#f0f4ff",
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <Container maxWidth="md">
        <Paper
          elevation={10}
          sx={{
            padding: { xs: 4, md: 8 },
            borderRadius: 4,
            backgroundColor: "rgba(255, 255, 255, 0.9)",
            backdropFilter: "blur(10px)"
          }}
        >
          <Typography
            variant="h2"
            align="center"
            gutterBottom
            sx={{
              fontWeight: "bold",
              color: "#1e3a8a",
              textShadow: "1px 1px 2px rgba(0,0,0,0.1)",
              mb: 4
            }}
          >
            Get Hired or Hire People for Free!
          </Typography>

          <Typography
            variant="h6"
            align="center"
            color="text.secondary"
            sx={{ mb: 6 }}
          >
            Connect with top talent or find your dream job on our platform
          </Typography>

          <Grid container spacing={4} justifyContent="center">
            <Grid item xs={12} sm={6} md={5} textAlign="center">
              <StyledButton
                variant="contained"
                color="primary"
                fullWidth
              >
                <StyledLink to="/employer/dashboard">
                  Hire Talent
                </StyledLink>
              </StyledButton>
            </Grid>
            <Grid item xs={12} sm={6} md={5} textAlign="center">
              <StyledButton
                variant="outlined"
                color="primary"
                fullWidth
              >
                <StyledLink to="/employee/feed">
                  Find Jobs
                </StyledLink>
              </StyledButton>
            </Grid>
          </Grid>

          <Box sx={{ mt: 6, textAlign: "center" }}>
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
