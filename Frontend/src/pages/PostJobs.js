import React, { useState } from 'react';
import './PostJobs.css';

const JobPostingForm = () => {
  const [jobType, setJobType] = useState('full-time');
  const [skills, setSkills] = useState([]);
  const [newSkill, setNewSkill] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isPaidInternship, setIsPaidInternship] = useState('');
  const [formData, setFormData] = useState({
    title: '',
    company: '',
    location: '',
    salary: '',
    stipend: '',
    experience: '',
    description: '',
    requirements: '',
    benefits: '',
    contactEmail: '',
    contactPhone: '',
    website: '',
    department: '',
    workType: 'office'
  });

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const addSkill = () => {
    if (newSkill.trim() && !skills.includes(newSkill.trim())) {
      setSkills([...skills, newSkill.trim()]);
      setNewSkill('');
    }
  };

  const removeSkill = (skillToRemove) => {
    setSkills(skills.filter(skill => skill !== skillToRemove));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    
    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      alert('Job Posted Successfully!');
    }, 2000);
  };

  const showLocationField = formData.workType === 'office' || formData.workType === 'hybrid';
  const isInternship = jobType === 'internship';
  const showStipendField = isInternship && isPaidInternship === 'paid';

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
              className={`tab ${jobType === 'full-time' ? 'active' : ''}`}
              onClick={() => setJobType('full-time')}
            >
              Full-time
            </button>
            <button 
              className={`tab ${jobType === 'internship' ? 'active' : ''}`}
              onClick={() => setJobType('internship')}
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
                  value={formData.title} 
                  onChange={e => handleInputChange('title', e.target.value)} 
                  required 
                />
              </div>

              {isInternship ? (
                <>
                  <div className="form-field" data-field="internship-type">
                    <label htmlFor="internshipType">Internship Type *</label>
                    <select 
                      value={isPaidInternship} 
                      onChange={e => setIsPaidInternship(e.target.value)}
                    >
                      <option value="">Select internship type</option>
                      <option value="paid">Paid</option>
                      <option value="unpaid">Unpaid</option>
                    </select>
                  </div>

                  {showStipendField && (
                    <div className="form-field" data-field="stipend">
                      <label htmlFor="stipend">Stipend Amount *</label>
                      <input 
                        id="stipend" 
                        placeholder="e.g. $1,500 - $2,500 per month" 
                        value={formData.stipend} 
                        onChange={e => handleInputChange('stipend', e.target.value)} 
                        required
                      />
                    </div>
                  )}
                </>
              ) : (
                <div className="form-field">
                  <label htmlFor="salary">Salary Range</label>
                  <input 
                    id="salary" 
                    placeholder="e.g. $80,000 - $120,000" 
                    value={formData.salary} 
                    onChange={e => handleInputChange('salary', e.target.value)} 
                  />
                </div>
              )}
              
              <div className="form-field">
                <label htmlFor="experience">Experience Level</label>
                <select 
                  value={formData.experience} 
                  onChange={e => handleInputChange('experience', e.target.value)}
                >
                  <option value="">Select experience level</option>
                  <option value="entry">Entry Level (0-2 years)</option>
                  <option value="mid">Mid Level (2-5 years)</option>
                  <option value="senior">Senior Level (5-8 years)</option>
                  <option value="lead">Lead Level (8+ years)</option>
                </select>
              </div>
              
              <div className="form-field">
                <label htmlFor="workType">Work Type *</label>
                <select 
                  value={formData.workType} 
                  onChange={e => handleInputChange('workType', e.target.value)}
                  required
                >
                  <option value="">Select work type</option>
                  <option value="office">On-site</option>
                  <option value="remote">Remote</option>
                  <option value="hybrid">Hybrid</option>
                </select>
              </div>

              {showLocationField && (
                <div className="form-field">
                  <label htmlFor="location">Location *</label>
                  <input 
                    id="location" 
                    placeholder="e.g. New York, NY" 
                    value={formData.location} 
                    onChange={e => handleInputChange('location', e.target.value)} 
                    required 
                  />
                </div>
              )}
            </div>
          </div>

          <div className="section job-details">
            <h3 className="section-title">Job Details</h3>
            
            <div className="form-field">
              <label htmlFor="description">Job Description *</label>
              <textarea 
                id="description" 
                placeholder="Describe the role, responsibilities, and what makes this position exciting..." 
                value={formData.description} 
                onChange={e => handleInputChange('description', e.target.value)} 
                required 
              />
            </div>
            
            <div className="form-field">
              <label htmlFor="requirements">Requirements</label>
              <textarea 
                id="requirements" 
                placeholder="List the key requirements, qualifications, and must-have skills..." 
                value={formData.requirements} 
                onChange={e => handleInputChange('requirements', e.target.value)} 
              />
            </div>
            
            <div className="form-field">
              <label htmlFor="benefits">Benefits & Perks</label>
              <textarea 
                id="benefits" 
                placeholder="Highlight the benefits, perks, and what makes your company great to work for..." 
                value={formData.benefits} 
                onChange={e => handleInputChange('benefits', e.target.value)} 
              />
            </div>
          </div>

          <div className="section skills">
            <h3 className="section-title">Required Skills</h3>
            
            <div className="skills-input">
              <input 
                placeholder="Add a skill (e.g. React, Python, Marketing)" 
                value={newSkill} 
                onChange={e => setNewSkill(e.target.value)} 
                onKeyPress={e => e.key === 'Enter' && (e.preventDefault(), addSkill())} 
              />
              <button type="button" onClick={addSkill} className="add-skill-btn">
                Add
              </button>
            </div>
            
            {skills.length > 0 && (
              <div className="skills-list">
                {skills.map((skill, index) => (
                  <span key={index} className="skill-badge">
                    {skill}
                    <button 
                      type="button" 
                      onClick={() => removeSkill(skill)} 
                      className="remove-skill"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            )}
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
