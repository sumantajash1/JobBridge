import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import PostJobs from "./pages/PostJobs";
import CompanySignIn from "./pages/companySignIn";
import CompanySignUp from "./pages/CompanySignUp";

//random comment
 
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/employer/signin" element={<CompanySignIn />} />
        <Route path="/employer/signup" element={<CompanySignUp />} />
        <Route path="/employer/post-job" element={<PostJobs />} />
        {/* <Route path="/employee/feed" element={<Feed />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
