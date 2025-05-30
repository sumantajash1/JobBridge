import { Navigate, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const location = useLocation();
  const token = localStorage.getItem('jwtToken');
  
  if (!token) {
    // Redirect to sign in if no token is present
    // Save the attempted URL to redirect back after login
    return <Navigate to="/applicant/signin" state={{ from: location }} replace />;
  }

  return children;
};

export default ProtectedRoute; 