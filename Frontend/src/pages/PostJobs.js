import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './PostJobs.css';

const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

const JobPostingForm = () => {
  const navigate = useNavigate();
  const [jobType, setJobType] = useState('Full Time');
  const [coreSkills, setCoreSkills] = useState([]);
  const [newSkill, setNewSkill] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState({
    jobTitle: '',
    salaryRange: '',
    yearsOfExperience: '',
    WorkType: 'On-site',
    location: '',
    jobDescription: '',
    requirements: '',
    benefitsAndPerks: '',
    deadline: '',
    maxOpenings: '',
  });

  useEffect(() => {
    // Check if user is authenticated
    const token = getCookie('jwtToken');
    if (!token) {
      navigate('/employer/signin', { replace: true });
    }
  }, [navigate]);

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const addSkill = () => {
    if (newSkill.trim() && !coreSkills.includes(newSkill.trim())) {
      setCoreSkills([...coreSkills, newSkill.trim()]);
      setNewSkill('');
    }
  };

  const removeSkill = (skillToRemove) => {
    setCoreSkills(coreSkills.filter(skill => skill !== skillToRemove));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const token = getCookie('jwtToken');
      if (!token) {
        throw new Error('No authentication token found');
      }
      
      const companyName = localStorage.getItem('companyName');
      if (!companyName) {
        throw new Error('Company name not found.');
      }

      const payload = {
        companyName,
        jobType,
        jobTitle: formData.jobTitle,
        salaryRange: formData.salaryRange,
        yearsOfExperience: formData.yearsOfExperience,
        WorkType: formData.WorkType,
        location: formData.location,
        jobDescription: formData.jobDescription,
        requrements: formData.requirements.split('\n').filter(line => line.trim() !== ''),
        benefitsAndPerks: formData.benefitsAndPerks,
        coreSkills,
        activeStatus: true,
        deadline: formData.deadline,
        maxOpenings: parseInt(formData.maxOpenings, 10),
      };

      const response = await fetch('http://localhost:8080/Company/postJob', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Failed to post job' }));
        throw new Error(errorData.message || 'Failed to post job');
      }

      alert('Job Posted Successfully!');
      navigate('/employer/dashboard');
    } catch (error) {
      console.error('Error posting job:', error);
      alert(error.message || 'Failed to post job. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const showLocationField = formData.WorkType === 'On-site' || formData.WorkType === 'Hybrid';

  return (
    <div className="job-posting-card">
      <div className="card-content">
        <div className="header">
          <h1 className="title">Post a New Job</h1>
          <p className="subtitle">
            Find the perfect candidate for your team. Create a compelling job listing that attracts top talent.
          </p>
        </div>

        <div className="job-type-toggle">
          <div className="tabs">
            <button 
              className={`tab ${jobType === 'Full Time' ? 'active' : ''}`}
              onClick={() => setJobType('Full Time')}
            >
              Full-time
            </button>
            <button 
              className={`tab ${jobType === 'Internship' ? 'active' : ''}`}
              onClick={() => setJobType('Internship')}
            >
              Internship
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="form">
          <div className="section basic-info">
            <h3 className="section-title">Basic Information</h3>
            
            <div className="form-grid">
              <div className="form-field">
                <label htmlFor="title">Job Title *</label>
                <input 
                  id="title" 
                  placeholder="e.g. Senior Software Engineer" 
                  value={formData.jobTitle} 
                  onChange={e => handleInputChange('jobTitle', e.target.value)} 
                  required 
                />
              </div>

              <div className="form-field">
                <label htmlFor="salary">Salary Range</label>
                <input 
                  id="salary" 
                  placeholder="e.g. ₹10,00,000 - ₹15,00,000" 
                  value={formData.salaryRange} 
                  onChange={e => handleInputChange('salaryRange', e.target.value)} 
                />
              </div>
              
              <div className="form-field">
                <label htmlFor="experience">Years of Experience</label>
                <input
                  type="text"
                  id="experience"
                  placeholder="e.g. 5"
                  value={formData.yearsOfExperience}
                  onChange={e => handleInputChange('yearsOfExperience', e.target.value)}
                />
              </div>
              
              <div className="form-field">
                <label htmlFor="workType">Work Type *</label>
                <select 
                  value={formData.WorkType} 
                  onChange={e => handleInputChange('WorkType', e.target.value)}
                  required
                >
                  <option value="On-site">On-site</option>
                  <option value="Remote">Remote</option>
                  <option value="Hybrid">Hybrid</option>
                </select>
              </div>

              {showLocationField && (
                <div className="form-field">
                  <label htmlFor="location">Location *</label>
                  <input 
                    id="location" 
                    placeholder="e.g. Bangalore, India" 
                    value={formData.location} 
                    onChange={e => handleInputChange('location', e.target.value)} 
                    required 
                  />
                </div>
              )}

              <div className="form-field">
                <label htmlFor="deadline">Application Deadline *</label>
                <input 
                  type="date"
                  id="deadline"
                  value={formData.deadline} 
                  onChange={e => handleInputChange('deadline', e.target.value)} 
                  required 
                />
              </div>

              <div className="form-field">
                <label htmlFor="maxOpenings">Max Openings *</label>
                <input 
                  type="number"
                  id="maxOpenings"
                  placeholder="e.g. 2"
                  value={formData.maxOpenings} 
                  onChange={e => handleInputChange('maxOpenings', e.target.value)} 
                  required 
                />
              </div>
            </div>
          </div>

          <div className="section job-details">
            <h3 className="section-title">Job Details</h3>
            
            <div className="form-field">
              <label htmlFor="description">Job Description *</label>
              <textarea 
                id="description" 
                placeholder="Describe the role, responsibilities, and what makes this position exciting..." 
                value={formData.jobDescription} 
                onChange={e => handleInputChange('jobDescription', e.target.value)} 
                required 
              />
            </div>
            
            <div className="form-field">
              <label htmlFor="requirements">Requirements</label>
              <textarea 
                id="requirements" 
                placeholder="List the key requirements, one per line..." 
                value={formData.requirements} 
                onChange={e => handleInputChange('requirements', e.target.value)} 
              />
            </div>
            
            <div className="form-field">
              <label htmlFor="benefits">Benefits & Perks</label>
              <textarea 
                id="benefits" 
                placeholder="Highlight the benefits, perks, and what makes your company great to work for..." 
                value={formData.benefitsAndPerks} 
                onChange={e => handleInputChange('benefitsAndPerks', e.target.value)} 
              />
            </div>
          </div>

          <div className="section skills">
            <h3 className="section-title">Core Skills</h3>
            <div className="form-field">
              <label htmlFor="skills">Add Skills</label>
              <div className="skills-input">
                <input 
                  id="skills"
                  value={newSkill} 
                  onChange={e => setNewSkill(e.target.value)}
                  placeholder="e.g. Java"
                />
                <button type="button" onClick={addSkill}>Add</button>
              </div>
              <ul className="skills-list">
                {coreSkills.map((skill, index) => (
                  <li key={index}>
                    {skill}
                    <button type="button" onClick={() => removeSkill(skill)}>×</button>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          <div className="submit-section">
            <button type="submit" disabled={isLoading} className="submit-btn">
              {isLoading ? 'Publishing...' : 'Post Job'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default JobPostingForm;
