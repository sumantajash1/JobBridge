import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ApplicantSignIn.css';

const ApplicantSignIn = () => {
  const [formData, setFormData] = useState({
    mobileNo: '',
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
    
    // Mobile number validation
    if (!formData.mobileNo) {
      newErrors.mobileNo = 'Mobile number is required';
    } else if (!/^[0-9]{10}$/.test(formData.mobileNo)) {
      newErrors.mobileNo = 'Please enter a valid 10-digit mobile number';
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
        const response = await fetch('http://localhost:8080/Applicant/SignIn', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
          body: JSON.stringify({
            mobileNo: formData.mobileNo,
            password: formData.password
          })
        });
        
        const responseText = await response.text();
        
        // Get JWT token from header - try different header names
        const jwtToken = response.headers.get('jwtToken') || 
                        response.headers.get('JWT-Token') || 
                        response.headers.get('Authorization');

        if (responseText === "Doesn't Exist") {
          setErrors(prev => ({
            ...prev,
            mobileNo: 'No account found with this mobile number'
          }));
        } else if (responseText === "Wrong Password") {
          setErrors(prev => ({
            ...prev,
            password: 'Incorrect password'
          }));
        } else if (responseText) {
          // If we get a name in response, it means login was successful
          localStorage.setItem('userName', responseText);
          
          // Add a small delay before navigation to ensure state is updated
          setTimeout(() => {
            navigate('/applicant/dashboard', { replace: true });
          }, 100);
          
          return;
        } else {
          throw new Error('Invalid response from server');
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
            Sign in to find your dream job
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className={`form-field ${errors.mobileNo ? 'error' : ''}`}>
            <label htmlFor="mobileNo">Mobile Number</label>
            <input
              id="mobileNo"
              type="tel"
              placeholder="Enter your mobile number"
              value={formData.mobileNo}
              onChange={(e) => handleInputChange('mobileNo', e.target.value)}
              maxLength="10"
              pattern="[0-9]*"
              inputMode="numeric"
            />
            {errors.mobileNo && <div className="error-message">{errors.mobileNo}</div>}
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
          <p className="signup-link">
            Don't have an account?{' '}
            <button
              type="button"
              className="link-btn"
              onClick={() => navigate('/applicant/signup')}
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

export default ApplicantSignIn;
