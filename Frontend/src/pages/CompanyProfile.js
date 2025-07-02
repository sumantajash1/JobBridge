import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CompanyProfile.css';

const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

const CompanyProfile = () => {
  const [companyName, setCompanyName] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = getCookie('jwtToken');
    if (!token) {
      navigate('/employer/signin', { replace: true });
      return;
    }
    fetch('http://localhost:8080/Company/verifyCompanyToken', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      credentials: 'include',
      body: JSON.stringify({ payload: token }),
    })
      .then(res => {
        if (!res.ok) throw new Error('Unauthorized');
        return res.text(); // plain string response
      })
      .then(name => {
        setCompanyName(name || 'Not available');
        localStorage.setItem('companyName', name || 'Not available'); // Save to localStorage
        setLoading(false);
      })
      .catch(() => {
        document.cookie = 'jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        navigate('/employer/signin', { replace: true });
      });
  }, [navigate]);

  if (loading) return <div className="company-profile-card">Loading...</div>;

  return (
    <div className="company-profile-card">
      <h2>Company Profile</h2>
      <div className="profile-field">
        <span className="profile-label">Company Name:</span>
        <span className="profile-value">{companyName}</span>
      </div>
    </div>
  );
};

export default CompanyProfile;