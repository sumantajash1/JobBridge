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
