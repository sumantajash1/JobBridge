import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ApplicantSignUp.css';

const ApplicantSignUp = () => {
  const [formData, setFormData] = useState({
    name: '',
    dob: '',
    password: '',
    confirmPassword: '',
    mobNo: '',
    email: ''
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
    
    // Name validation
    if (!formData.name) {
      newErrors.name = 'Name is required';
    } else if (formData.name.length < 2) {
      newErrors.name = 'Name must be at least 2 characters long';
    }

    // Date of Birth validation
    if (!formData.dob) {
      newErrors.dob = 'Date of birth is required';
    } else {
      const dob = new Date(formData.dob);
      const today = new Date();
      const age = today.getFullYear() - dob.getFullYear();
      if (age < 18) {
        newErrors.dob = 'You must be at least 18 years old';
      }
    }

    // Mobile number validation
    if (!formData.mobNo) {
      newErrors.mobNo = 'Mobile number is required';
    } else if (!/^[0-9]{10}$/.test(formData.mobNo)) {
      newErrors.mobNo = 'Please enter a valid 10-digit phone number';
    }

    // Email validation
    if (!formData.email) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters long';
    }

    // Confirm password validation
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      try {
        const applicantData = {
          aName: formData.name,
          dob: formData.dob,
          mobNo: formData.mobNo,
          email: formData.email,
          password: formData.password
        };
        
        const response = await fetch('http://localhost:8080/Applicant/SignUp', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': '*/*',
            'Connection': 'keep-alive'
          },
          credentials: 'include',
          body: JSON.stringify(applicantData)
        });

        const responseText = await response.text();
        console.log('Response text:', responseText);

        if (!responseText) {
          throw new Error('Empty response from server');
        }

        if (responseText.trim() === "PhoneExists") {
          console.log('Phone number exists error');
          setErrors(prev => ({
            ...prev,
            mobNo: 'An account with this mobile number already exists'
          }));
        } else if (responseText.trim() === "EmailExists") {
          console.log('Email exists error');
          setErrors(prev => ({
            ...prev,
            email: 'An account with this email already exists'
          }));
        } else {
          // If no error cases match, redirect to dashboard
          console.log('Sign up successful, redirecting to dashboard...');
          window.location.replace('/applicant/dashboard');
        }
      } catch (error) {
        console.error('Sign up failed:', error);
        setErrors(prev => ({
          ...prev,
          submit: `Failed to create account: ${error.message}`
        }));
      }
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-card">
        <div className="signup-header">
          <h1>Create Your Account</h1>
          <p>Join our platform to find your dream job</p>
        </div>

        <form onSubmit={handleSubmit} className="signup-form">
          <div className="form-grid">
            <div className={`form-field ${errors.name ? 'error' : ''}`}>
              <label htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                placeholder="Enter your full name"
                value={formData.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
              />
              {errors.name && <div className="error-message">{errors.name}</div>}
            </div>

            <div className={`form-field ${errors.dob ? 'error' : ''}`}>
              <label htmlFor="dob">Date of Birth</label>
              <input
                id="dob"
                type="date"
                value={formData.dob}
                onChange={(e) => handleInputChange('dob', e.target.value)}
              />
              {errors.dob && <div className="error-message">{errors.dob}</div>}
            </div>

            <div className={`form-field ${errors.mobNo ? 'error' : ''}`}>
              <label htmlFor="mobNo">Mobile Number</label>
              <input
                id="mobNo"
                type="tel"
                placeholder="Enter your mobile number"
                value={formData.mobNo}
                onChange={(e) => handleInputChange('mobNo', e.target.value)}
              />
              {errors.mobNo && <div className="error-message">{errors.mobNo}</div>}
            </div>

            <div className={`form-field ${errors.email ? 'error' : ''}`}>
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                placeholder="Enter your email"
                value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)}
              />
              {errors.email && <div className="error-message">{errors.email}</div>}
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

            <div className={`form-field ${errors.confirmPassword ? 'error' : ''}`}>
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                id="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                value={formData.confirmPassword}
                onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
              />
              {errors.confirmPassword && <div className="error-message">{errors.confirmPassword}</div>}
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="submit-btn">
              Create Account
            </button>
            <p className="signin-link">
              Already have an account?{' '}
              <button
                type="button"
                className="link-btn"
                onClick={() => navigate('/applicant/signin')}
              >
                Sign In
              </button>
            </p>
            <button
              type="button"
              className="link-btn back-home"
              onClick={() => navigate('/')}
            >
              ← Back to Home
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ApplicantSignUp; 