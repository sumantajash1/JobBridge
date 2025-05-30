import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CompanySignUp.css';

const CompanySignUp = () => {
  const [formData, setFormData] = useState({
    gstin: '',
    companyName: '',
    contactNumber: '',
    email: '',
    password: '',
    confirmPassword: '',
    establishedYear: ''
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

    // Company name validation
    if (!formData.companyName) {
      newErrors.companyName = 'Company name is required';
    }

    // Contact number validation
    if (!formData.contactNumber) {
      newErrors.contactNumber = 'Contact number is required';
    } else if (!/^[0-9]{10}$/.test(formData.contactNumber)) {
      newErrors.contactNumber = 'Please enter a valid 10-digit phone number';
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

    // Year established validation
    if (!formData.establishedYear) {
      newErrors.establishedYear = 'Year established is required';
    } else {
      const year = parseInt(formData.establishedYear);
      const currentYear = new Date().getFullYear();
      if (year < 1900 || year > currentYear) {
        newErrors.establishedYear = `Year must be between 1900 and ${currentYear}`;
      }
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
    console.log('Form submitted');
    console.log('Form data:', formData);
    
    if (validateForm()) {
      console.log('Form validation passed');
      try {
        // Prepare data in the format matching the Company class
        const companyData = {
          gstNum: formData.gstin,
          cName: formData.companyName,
          cEmail: formData.email,
          cContactNumber: formData.contactNumber,
          cPassword: formData.password,
          estd: formData.establishedYear
        };
        
        console.log('Sending data to backend:', companyData);

        const response = await fetch('http://localhost:8080/Company/SignUp', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          mode: 'cors',
          body: JSON.stringify(companyData)
        });

        const responseText = await response.text();
        console.log('Response:', responseText);

        if (responseText === "Invalid GST Number") {
          setErrors(prev => ({
            ...prev,
            gstin: 'Invalid GST Number'
          }));
          return;
        }

        if (responseText === "Company Already Exists") {
          setErrors(prev => ({
            ...prev,
            gstin: 'Company with this GST number already exists'
          }));
          return;
        }

        // If we get here, signup was successful and we received a JWT token
        if (responseText && responseText.length > 0) {
          console.log('Signup successful, storing JWT token');
          localStorage.setItem('companyToken', responseText);
          
          // Use window.location for hard navigation
          window.location.href = '/employer/dashboard';
        } else {
          throw new Error('No token received');
        }
      } catch (error) {
        console.error('Sign up failed:', error);
        setErrors(prev => ({
          ...prev,
          submit: `Failed to create account: ${error.message}`
        }));
      }
    } else {
      console.log('Form validation failed');
      console.log('Validation errors:', errors);
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-card">
        <div className="signup-header">
          <h1>Create Your Company Account</h1>
          <p>Join our platform to connect with top talent</p>
        </div>

        <form onSubmit={handleSubmit} className="signup-form">
          <div className="form-grid">
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

            <div className={`form-field ${errors.companyName ? 'error' : ''}`}>
              <label htmlFor="companyName">Company Name</label>
              <input
                id="companyName"
                type="text"
                placeholder="Enter your company name"
                value={formData.companyName}
                onChange={(e) => handleInputChange('companyName', e.target.value)}
              />
              {errors.companyName && <div className="error-message">{errors.companyName}</div>}
            </div>

            <div className={`form-field ${errors.contactNumber ? 'error' : ''}`}>
              <label htmlFor="contactNumber">Company Contact Number</label>
              <input
                id="contactNumber"
                type="tel"
                placeholder="Enter company contact number"
                value={formData.contactNumber}
                onChange={(e) => handleInputChange('contactNumber', e.target.value)}
              />
              {errors.contactNumber && <div className="error-message">{errors.contactNumber}</div>}
            </div>

            <div className={`form-field ${errors.email ? 'error' : ''}`}>
              <label htmlFor="email">Company Email</label>
              <input
                id="email"
                type="email"
                placeholder="Enter your company email"
                value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)}
              />
              {errors.email && <div className="error-message">{errors.email}</div>}
            </div>

            <div className={`form-field ${errors.establishedYear ? 'error' : ''}`}>
              <label htmlFor="establishedYear">Year Established</label>
              <input
                id="establishedYear"
                type="number"
                placeholder="Enter year of establishment"
                value={formData.establishedYear}
                onChange={(e) => handleInputChange('establishedYear', e.target.value)}
              />
              {errors.establishedYear && <div className="error-message">{errors.establishedYear}</div>}
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
                onClick={() => navigate('/employer/signin')}
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

export default CompanySignUp;