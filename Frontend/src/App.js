import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import PostJobs from "./pages/PostJobs";

//random comment
 
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/employer/post-job" element={<PostJobs />} />
        {/* <Route path="/employee/feed" element={<Feed />} /> */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
