import "./App.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home";
import PostJobs from "./pages/PostJobs";
import CompanySignIn from "./pages/companySignIn";
import CompanySignUp from "./pages/CompanySignUp";
import CompanyDashboard from "./pages/companyDashboard";
import ApplicantSignUp from "./pages/ApplicantSignUp";
import ApplicantSignIn from "./pages/ApplicantSignIn";
import ApplicantDashboard from "./pages/ApplicantDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import ForgotPassword from "./pages/forgotPassword";
import CompanyProfile from "./pages/CompanyProfile";
 
function App() {
  return (
    <BrowserRouter
      future={{
        v7_startTransition: true,
        v7_relativeSplatPath: true
      }}
    >
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/employer/signin" element={<CompanySignIn />} />
        <Route path="/employer/signup" element={<CompanySignUp />} />
        <Route path="/employer/dashboard" element={<CompanyDashboard />} />
        <Route path="/employer/PostJobs" element={<PostJobs />} />
        <Route path="/employer/profile" element={<CompanyProfile />} />
        <Route path="/employer/forgotpassword" element={<ForgotPassword />} />
        <Route path="/applicant/signup" element={<ApplicantSignUp />} />
        <Route path="/applicant/signin" element={<ApplicantSignIn />} />
        <Route 
          path="/applicant/dashboard" 
          element={
            <ProtectedRoute>
              <ApplicantDashboard />
            </ProtectedRoute>
          } 
        />
        {/* Redirect any unknown routes to home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
