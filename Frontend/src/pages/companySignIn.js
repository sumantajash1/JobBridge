import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './companySignIn.css';

const CompanySignIn = () => {
  const [formData, setFormData] = useState({
    gstin: '',
    password: ''
  });

  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    // GSTIN validation
    if (!formData.gstin) {
      newErrors.gstin = 'GSTIN number is required';
    } else if (!/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/.test(formData.gstin)) {
      newErrors.gstin = 'Please enter a valid GSTIN number';
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        // Here you would typically handle authentication with your backend
        // For now, we'll just navigate to the dashboard
        navigate('/employer/dashboard');
      } catch (error) {
        console.error('Sign in failed:', error);
        // Handle sign-in error here
      }
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h1 className="auth-title">Welcome Back</h1>
          <p className="auth-subtitle">
            Sign in to post jobs and find the perfect candidates
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className={`form-field ${errors.gstin ? 'error' : ''}`}>
            <label htmlFor="gstin">GSTIN Number</label>
            <input
              id="gstin"
              type="text"
              placeholder="Enter your GSTIN number"
              value={formData.gstin}
              onChange={(e) => handleInputChange('gstin', e.target.value)}
            />
            {errors.gstin && <div className="error-message">{errors.gstin}</div>}
          </div>

          <div className={`form-field ${errors.password ? 'error' : ''}`}>
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={(e) => handleInputChange('password', e.target.value)}
            />
            {errors.password && <div className="error-message">{errors.password}</div>}
          </div>

          <button type="submit" className="submit-btn">
            Sign In
          </button>
        </form>

        <div className="auth-footer">
          <p>Don't have an account? <button onClick={() => navigate('/employer/signup')} className="link-btn">Sign Up</button></p>
        </div>
      </div>
    </div>
  );
};

export default CompanySignIn; 