import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  FormControlLabel,
  Switch,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Checkbox,
  FormGroup,
} from '@mui/material';
import './PostJobs.css';

const roleTypes = [
  'Software Engineer',
  'Hardware Engineer',
  'Business Development',
  'Human Resources',
  'Data Scientist',
  'Product Manager',
  'UI/UX Designer',
  'DevOps Engineer',
  'Quality Assurance',
  'Project Manager'
];

const PostJobs = () => {
  const [isFullTime, setIsFullTime] = useState(true);
  const [formData, setFormData] = useState({
    roleName: '',
    roleType: '',
    skills: '',
    technologies: '',
    isPaid: false,
    salary: '',
    description: '',
    hasDeadline: false,
    deadline: '',
    duration: ''
  });

  const handleChange = (e) => {
    const { name, value, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'isPaid' || name === 'hasDeadline' ? checked : value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(formData);
    // Add your API call here to submit the job posting
  };

  return (
    <Container className="post-jobs-container">
      <Paper className="post-jobs-paper">
        <Typography variant="h4" className="post-jobs-title">
          Post a {isFullTime ? 'Full Time' : 'Internship'} Position
        </Typography>

        <Box className="job-type-switch">
          <FormControlLabel
            control={
              <Switch
                checked={isFullTime}
                onChange={(e) => setIsFullTime(e.target.checked)}
                color="primary"
              />
            }
            label={isFullTime ? "Full Time" : "Internship"}
          />
        </Box>

        <form onSubmit={handleSubmit} className="post-jobs-form">
          <TextField
            fullWidth
            label="Name of the Role"
            name="roleName"
            value={formData.roleName}
            onChange={handleChange}
            required
            className="form-field"
          />

          <FormControl fullWidth className="form-field">
            <InputLabel>Type of Role</InputLabel>
            <Select
              name="roleType"
              value={formData.roleType}
              onChange={handleChange}
              required
            >
              {roleTypes.map((role) => (
                <MenuItem key={role} value={role}>
                  {role}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label="Skills Required"
            name="skills"
            value={formData.skills}
            onChange={handleChange}
            required
            multiline
            rows={2}
            className="form-field"
          />

          <TextField
            fullWidth
            label="Technology Required"
            name="technologies"
            value={formData.technologies}
            onChange={handleChange}
            required
            multiline
            rows={2}
            className="form-field"
          />

          {!isFullTime && (
            <FormGroup className="form-field">
              <FormControlLabel
                control={
                  <Checkbox
                    name="isPaid"
                    checked={formData.isPaid}
                    onChange={handleChange}
                  />
                }
                label="Is this a paid position?"
              />
            </FormGroup>
          )}

          {formData.isPaid && !isFullTime && (
            <TextField
              fullWidth
              label="Stipend"
              name="salary"
              value={formData.salary}
              onChange={handleChange}
              required
              type="number"
              className="form-field"
            />
          )}

          {isFullTime && (
            <TextField
              fullWidth
              label="Salary (Per year)"
              name="salary"
              value={formData.salary}
              onChange={handleChange}
              required
              type="number"
              className="form-field"
            />
          )}

          {!isFullTime && (
            <TextField
              fullWidth
              label="Duration of Internship"
              name="duration"
              value={formData.duration}
              onChange={handleChange}
              required
              className="form-field"
            />
          )}

          <TextField
            fullWidth
            label="Job Description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            required
            multiline
            rows={4}
            className="form-field"
          />

          <FormGroup className="form-field">
            <FormControlLabel
              control={
                <Checkbox
                  name="hasDeadline"
                  checked={formData.hasDeadline}
                  onChange={handleChange}
                />
              }
              label="Set Application Deadline"
            />
          </FormGroup>

          {formData.hasDeadline && (
            <TextField
              fullWidth
              label="Application Deadline"
              name="deadline"
              type="date"
              value={formData.deadline}
              onChange={handleChange}
              required
              className="form-field"
              InputLabelProps={{
                shrink: true,
              }}
            />
          )}

          <Button
            type="submit"
            variant="contained"
            color="primary"
            className="submit-button"
          >
            Post Job
          </Button>
        </form>
      </Paper>
    </Container>
  );
};

export default PostJobs; 