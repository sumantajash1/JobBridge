import React, { useState } from 'react';
import './forgotPassword.css';

const ForgotPassword = () => {
  const [gstNum, setGstNum] = useState('');
  const [error, setError] = useState('');
  const [otpSentMsg, setOtpSentMsg] = useState('');
  const [showOtpInput, setShowOtpInput] = useState(false);
  const [otp, setOtp] = useState('');

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
      const msg = await response.text();
      setOtpSentMsg(msg);
      setShowOtpInput(true);
    } catch (err) {
      setError('Network error. Please try again.');
    }
  };

  const handleOtpChange = (e) => {
    setOtp(e.target.value);
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
            />
            <button
              type="button"
              className="forgot-password-submit-btn"
              style={{ marginTop: '1rem' }}
              // onClick={handleOtpVerify} // To be implemented
            >
              Verify OTP
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ForgotPassword; 