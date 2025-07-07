import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './forgotPassword.css';

const ForgotPassword = () => {
  const [gstNum, setGstNum] = useState('');
  const [error, setError] = useState('');
  const [otpSentMsg, setOtpSentMsg] = useState('');
  const [showOtpInput, setShowOtpInput] = useState(false);
  const [otp, setOtp] = useState('');
  const [isOtpVerified, setIsOtpVerified] = useState(false);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setOtpSentMsg('');
    setShowOtpInput(false);
    try {
      const response = await fetch(`http://localhost:8080/Company/generateOtp/${gstNum}`, {
        method: 'GET',
      });
      if (!response.ok) {
        const text = await response.text();
        if (text === 'UserNotExist') {
          setError("GSTIN doesn't exist or user isn't signed up.");
        } else {
          setError('Something went wrong. Please try again.');
        }
        return;
      }
      const mobNO = await response.text();
      console.log(mobNO);
      localStorage.setItem('mobileNumber', mobNO);
      setOtpSentMsg('OTP sent to registered mobile number: ' + mobNO);
      setShowOtpInput(true);
    } catch (err) {
      setError('Network error. Please try again.');
    }
  };

  const handleOtpChange = (e) => {
    setOtp(e.target.value);
  };

  const handleOtpVerify = async () => {
    const mobileNum = localStorage.getItem('mobileNumber');
    if (!mobileNum || !otp) {
      setError('Please enter the OTP.');
      return;
    }
    setError('');
    try {
      const response = await fetch('http://localhost:8080/Company/verifyOtp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          mobileNum,
          otp,
        }),
      });
      if (!response.ok) {
        setError('Invalid OTP or verification failed.');
        return;
      }
      const result = await response.text();
      setOtpSentMsg('OTP verified successfully');
      setIsOtpVerified(true); // Disable the button
    } catch (err) {
      setError('Network error. Please try again.');
    }
  };

  const handlePasswordReset = async () => {
    if (!newPassword || !confirmPassword) {
      setError('Please fill in both password fields.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    const mobileNum = localStorage.getItem('mobileNumber');
    try {
      const response = await fetch('http://localhost:8080/Company/resetPassword', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mobNo : mobileNum , code : newPassword}),
      });
      if (!response.ok) {
        setError('Password reset failed.');
        return;
      }
      setOtpSentMsg('Password reset successful!');
      // Redirect to sign-in page after successful reset
      setTimeout(() => {
        navigate('/employer/signin');
      }, 1500); // Optional: short delay to show success message
    } catch (err) {
      setError('Network error. Please try again.');
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-card">
        <h2>Forgot Password?</h2>
        <p>Please enter your registered GSTIN number to reset your password.</p>
        <form onSubmit={handleSubmit} className="forgot-password-form">
          <input
            type="text"
            placeholder="Enter GSTIN number"
            value={gstNum}
            onChange={e => setGstNum(e.target.value)}
            className="forgot-password-input"
            disabled={showOtpInput}
          />
          <button type="submit" className="forgot-password-submit-btn" disabled={showOtpInput}>
            Submit
          </button>
        </form>
        {error && <div className="forgot-password-error">{error}</div>}
        {otpSentMsg && <div className="forgot-password-otp-msg">{otpSentMsg}</div>}
        {showOtpInput && (
          <div className="otp-section">
            <input
              type="text"
              placeholder="Enter OTP"
              value={otp}
              onChange={handleOtpChange}
              className="forgot-password-input"
              maxLength={6}
              disabled={isOtpVerified}
            />
            <button
              type="button"
              className="forgot-password-submit-btn"
              style={{ marginTop: '1rem' }}
              onClick={handleOtpVerify}
              disabled={isOtpVerified}
            >
              Verify OTP
            </button>
            {isOtpVerified && (
              <div className="password-reset-section" style={{ marginTop: '2rem' }}>
                <input
                  type="password"
                  placeholder="New Password"
                  value={newPassword}
                  onChange={e => setNewPassword(e.target.value)}
                  className="forgot-password-input"
                  style={{ marginBottom: '1rem' }}
                />
                <input
                  type="password"
                  placeholder="Confirm New Password"
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  className="forgot-password-input"
                />
                <button onClick={handlePasswordReset} className="forgot-password-submit-btn">
                  Reset Password
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ForgotPassword; 