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
  const [otp, setOtp] = useState('');
  const [otpSentMsg, setOtpSentMsg] = useState('');
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [isMobileVerified, setIsMobileVerified] = useState(false);
  const [otpError, setOtpError] = useState('');
  const [otpAttempted, setOtpAttempted] = useState(false);
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

  const handleSendOtp = async () => {
    setOtpSentMsg('');
    setOtpError('');
    let mobileNumber = formData.contactNumber;
    if (mobileNumber.startsWith('+91')) {
      mobileNumber = mobileNumber.slice(3);
    }
    if (!/^[0-9]{10}$/.test(mobileNumber)) {
      setOtpError('Please enter a valid 10-digit phone number');
      return;
    }
    try {
      const response = await fetch(`http://localhost:8080/Company/generateOtpByMobNo/${mobileNumber}`, {
        method: 'GET',
      });
      if (response.status === 200) {
        setIsOtpSent(true);
        setOtpSentMsg('OTP sent to your mobile number.');
      } else if (response.status === 400) {
        setOtpError("OTP couldn't be generated. Please try again.");
      } else {
        setOtpError('Failed to send OTP. Try again.');
      }
    } catch (err) {
      setOtpError('Network error. Please try again.');
    }
  };

  const handleVerifyOtp = async () => {
    setOtpError('');
    setOtpAttempted(false);
    // Remove '+91' if present and get the 10-digit number
    let mobileNumber = formData.contactNumber;
    if (mobileNumber.startsWith('+91')) {
      mobileNumber = mobileNumber.slice(3);
    }
    if (!otp) {
      setOtpError('Please enter the OTP.');
      return;
    }
    try {
      const response = await fetch('http://localhost:8080/Company/verifyOtp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mobNo: mobileNumber, code: otp }),
      });
      setOtpAttempted(true);
      if (response.status === 200) {
        setIsMobileVerified(true);
        setOtpSentMsg('Mobile number verified successfully!');
        setOtpError('');
      } else {
        setIsMobileVerified(false);
        setOtpError('OTP verification failed. Please try again.');
      }
    } catch (err) {
      setIsMobileVerified(false);
      setOtpError('Network error. Please try again.');
      setOtpAttempted(true);
    }
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
          companyName: formData.companyName,
          companyEmail: formData.email,
          companyContactNum: formData.contactNumber,
          companyPassword: formData.password,
          estd: formData.establishedYear
        };
        
        console.log('Sending data to backend:', companyData);

        const response = await fetch('http://localhost:8080/Company/SignUp', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          mode: 'cors',
          credentials: 'include',
          body: JSON.stringify(companyData)
        });

        const responseText = await response.text();
        console.log('Response:', responseText);

        // Handle different error cases
        if (responseText === "InvalidGST") {
          setErrors(prev => ({
            ...prev,
            gstin: 'Invalid GST Number format'
          }));
        } else if (responseText === "gstExists") {
          setErrors(prev => ({
            ...prev,
            gstin: 'A company with this GST number already exists'
          }));
        } else if (responseText === "contactNumberExists") {
          setErrors(prev => ({
            ...prev,
            contactNumber: 'A company with this contact number already exists'
          }));
        } else if (responseText === "emailExists") {
          setErrors(prev => ({
            ...prev,
            email: 'A company with this email already exists'
          }));
        } else if (responseText === "nameExists") {
          setErrors(prev => ({
            ...prev,
            companyName: 'A company with this name already exists'
          }));
        } else if (responseText && responseText.length > 0) {
          // If we get here, signup was successful and we received the company name
          console.log('Signup successful, storing company name');
          localStorage.setItem('companyName', responseText);
          // Use navigate for SPA navigation
          navigate('/employer/dashboard');
        } else {
          throw new Error('No response received from server');
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

            <div className={`form-field ${errors.contactNumber ? 'error' : ''}`} style={{ position: 'relative' }}>
              <label htmlFor="contactNumber">Company Contact Number</label>
              <input
                id="contactNumber"
                type="tel"
                placeholder="Enter company contact number"
                value={formData.contactNumber}
                onChange={(e) => {
                  handleInputChange('contactNumber', e.target.value);
                  setIsMobileVerified(false);
                  setIsOtpSent(false);
                  setOtp('');
                  setOtpSentMsg('');
                  setOtpError('');
                }}
                disabled={isMobileVerified}
              />
              <button
                type="button"
                className="custom-verify-btn"
                style={{ marginLeft: 8, marginTop: 8 }}
                onClick={handleSendOtp}
                disabled={isMobileVerified || (() => { let n = formData.contactNumber; if(n.startsWith('+91')) n = n.slice(3); return !/^[0-9]{10}$/.test(n); })()}
              >
                {isMobileVerified ? 'Verified' : 'Verify Mobile Number'}
              </button>
              {errors.contactNumber && <div className="error-message">{errors.contactNumber}</div>}
              {otpSentMsg && <div className="success-message">{otpSentMsg}</div>}
              {otpError && <div className="error-message">{otpError}</div>}
              {isOtpSent && !isMobileVerified && (
                <div style={{ marginTop: 8 }}>
                  <input
                    type="text"
                    placeholder="Enter OTP"
                    value={otp}
                    onChange={e => setOtp(e.target.value)}
                    maxLength={6}
                    className="otp-input"
                  />
                  <button
                    type="button"
                    className="custom-verify-btn"
                    style={{ marginLeft: 8 }}
                    onClick={handleVerifyOtp}
                  >
                    Verify OTP
                  </button>
                </div>
              )}
              {isMobileVerified && (
                <div className="verified-status" style={{ color: 'green', marginTop: 8, fontWeight: 600 }}>
                  ✔ Mobile number verified
                </div>
              )}
              {!isMobileVerified && otpError && otpAttempted && (
                <div className="verified-status" style={{ color: 'red', marginTop: 8, fontWeight: 600 }}>
                  ✖ Mobile number not verified
                </div>
              )}
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
            <button type="submit" className="submit-btn" disabled={!isMobileVerified}>
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