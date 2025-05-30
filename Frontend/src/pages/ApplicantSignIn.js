import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './ApplicantSignIn.css';

const ApplicantSignIn = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [formData, setFormData] = useState({
    mobileNo: '',
    password: ''
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      const from = location.state?.from?.pathname || '/applicant/dashboard';
      navigate(from, { replace: true });
    }
  }, [navigate, location]);

  const validateForm = () => {
    console.log('Validating form with data:', formData);
    const newErrors = {};
    if (!formData.mobileNo) {
      newErrors.mobileNo = 'Mobile number is required';
    } else if (!/^[0-9]{10}$/.test(formData.mobileNo)) {
      newErrors.mobileNo = 'Please enter a valid 10-digit mobile number';
    }
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }
    console.log('Validation errors:', newErrors);
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    console.log('Handling change for:', name, 'value:', value);
    if (name === 'mobileNo' && !/^\d*$/.test(value)) {
      return;
    }
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Form submitted');
    
    if (validateForm()) {
      console.log('Form is valid, proceeding with sign in');
      try {
        const requestBody = {
          mobileNo: formData.mobileNo,
          password: formData.password
        };
        console.log('Sending request with body:', requestBody);

        const response = await fetch('http://localhost:8080/Applicant/SignIn', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': '*/*',
            'Connection': 'keep-alive'
          },
          body: JSON.stringify(requestBody)
        });

        console.log('Response received:', response.status);
        const responseText = await response.text();
        console.log('Response text:', responseText);

        if (responseText.startsWith('ey')) {
          console.log('Valid token received, storing and redirecting');
          localStorage.setItem('jwtToken', responseText);
          navigate('/applicant/dashboard', { replace: true });
        } else if (responseText === "Doesn't Exist") {
          console.log('Account not found');
          setErrors(prev => ({
            ...prev,
            mobileNo: 'No account found with this mobile number'
          }));
        } else if (responseText === "Wrong Password") {
          console.log('Wrong password');
          setErrors(prev => ({
            ...prev,
            password: 'Incorrect password'
          }));
        }
      } catch (error) {
        console.error('Sign in error:', error);
        setErrors(prev => ({
          ...prev,
          submit: 'Failed to sign in. Please try again.'
        }));
      }
    } else {
      console.log('Form validation failed');
    }
  };

  return (
    <div className="signin-container">
      <div className="signin-card">
        <div className="signin-header">
          <h1>Welcome Back!</h1>
          <p>Sign in to continue your job search</p>
        </div>

        <form className="signin-form" onSubmit={handleSubmit}>
          <div className="form-field-container">
            <div className={`form-field ${errors.mobileNo ? 'error' : ''}`}>
              <label htmlFor="mobileNo">Mobile Number</label>
              <input
                type="tel"
                id="mobileNo"
                name="mobileNo"
                value={formData.mobileNo}
                onChange={handleChange}
                placeholder="Enter your 10-digit mobile number"
                maxLength="10"
                pattern="[0-9]*"
                inputMode="numeric"
              />
              {errors.mobileNo && <div className="error-message">{errors.mobileNo}</div>}
            </div>

            <div className={`form-field ${errors.password ? 'error' : ''}`}>
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter your password"
              />
              {errors.password && <div className="error-message">{errors.password}</div>}
            </div>
          </div>

          {errors.submit && <div className="submit-error">{errors.submit}</div>}

          <div className="form-actions">
            <button 
              type="submit" 
              className="submit-btn"
            >
              Sign In
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
          </div>
        </form>
      </div>
    </div>
  );
};

export default ApplicantSignIn; 