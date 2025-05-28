import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import PostJobs from "./pages/PostJobs";
import CompanySignIn from "./pages/companySignIn";
import CompanySignUp from "./pages/CompanySignUp";
import CompanyDashboard from "./pages/companyDashboard";
import ApplicantSignUp from "./pages/ApplicantSignUp";

//random comment
 
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/employer/signin" element={<CompanySignIn />} />
        <Route path="/employer/signup" element={<CompanySignUp />} />
        <Route path="/employer/dashboard" element={<CompanyDashboard />} />
        <Route path="/employer/PostJobs" element={<PostJobs />} />
        <Route path="/applicant/signup" element={<ApplicantSignUp />} />
        {/* <Route path="/employee/feed" element={<Feed />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
