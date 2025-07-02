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
    if (!formData.gstNum) {
      newErrors.gstNum = 'GSTIN number is required';
    } else if (!/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/.test(formData.gstNum)) {
      newErrors.gstNum = 'Please enter a valid GSTIN number';
    }
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
          credentials: 'include', // Important for cookies
          body: JSON.stringify({
            gstNum: formData.gstNum,
            password: formData.password
          })
        });

        if (!response.ok) {
          const errorText = await response.text();
          setErrors(prev => ({
            ...prev,
            submit: errorText || 'Sign in failed. Please try again.'
          }));
          return;
        }

        // Get JWT from response header if present and set as cookie
        const jwtToken = response.headers.get('jwt');
        if (jwtToken) {
          document.cookie = `jwtToken=${jwtToken}; path=/;`;
        }
        // Redirect to dashboard
        navigate('/employer/dashboard');
      } catch (error) {
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

          <button
            type="button"
            className="link-btn forgot-password"
            style={{ marginBottom: '1rem', color: '#007bff', background: 'none', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}
            onClick={() => navigate('/employer/forgotpassword')}
          >
            Forgot Password?
          </button>

          {errors.submit && (
            <div className="error-message" style={{ textAlign: 'center', marginBottom: '1rem' }}>
              {errors.submit}
            </div>
          )}

          <button type="submit" className="submit-btn" disabled={isLoading}>
            {isLoading ? 'Signing In...' : 'Sign In'}
          </button>
          <p className="signup-link">
            Don't have an account?{' '}
            <button
              type="button"
              className="link-btn"
              onClick={() => navigate('/employer/signup')}
            >
              Sign Up
            </button>
          </p>
          <button
            type="button"
            className="link-btn back-home"
            onClick={() => navigate('/')}
          >
            ← Back to Home
          </button>
        </form>
      </div>
    </div>
  );
};

export default CompanySignIn;