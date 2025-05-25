import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './companySignIn.css';

const CompanySignIn = () => {
  const [formData, setFormData] = useState({
    gstNum: '',
    password: ''
  });
  const [isLoading, setIsLoading] = useState(false);
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
    if (!formData.gstNum) {
      newErrors.gstNum = 'GSTIN number is required';
    } else if (!/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/.test(formData.gstNum)) {
      newErrors.gstNum = 'Please enter a valid GSTIN number';
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
      setIsLoading(true);
      try {
        const response = await fetch('http://localhost:8080/Company/SignIn', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            gstNum: formData.gstNum,
            password: formData.password
          })
        });

        const responseText = await response.text();
        console.log('Response:', responseText);
        
        if (responseText === "Invalid GST Number" || 
            responseText === "Company is not found, please register" || 
            responseText === "Password is Wrong") {
          throw new Error(responseText);
        }

        // Store the JWT token from response body
        if (responseText && responseText.length > 0) {
          localStorage.setItem('companyToken', responseText);
          console.log('Token stored successfully');
          
          // Use window.location for hard navigation
          window.location.href = '/employer/dashboard';
        } else {
          throw new Error('No token received');
        }
      } catch (error) {
        console.error('Sign in failed:', error);
        setErrors(prev => ({
          ...prev,
          submit: error.message || 'Failed to sign in. Please try again.'
        }));
      } finally {
        setIsLoading(false);
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
          <div className={`form-field ${errors.gstNum ? 'error' : ''}`}>
            <label htmlFor="gstNum">GSTIN Number</label>
            <input
              id="gstNum"
              type="text"
              placeholder="Enter your GSTIN number"
              value={formData.gstNum}
              onChange={(e) => handleInputChange('gstNum', e.target.value)}
            />
            {errors.gstNum && <div className="error-message">{errors.gstNum}</div>}
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

          {errors.submit && (
            <div className="error-message" style={{ textAlign: 'center', marginBottom: '1rem' }}>
              {errors.submit}
            </div>
          )}

          <button type="submit" className="submit-btn" disabled={isLoading}>
            {isLoading ? 'Signing In...' : 'Sign In'}
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