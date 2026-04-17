import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import MemberLoginPage from "./pages/MemberLoginPage";
import MemberLogoutPage from "./pages/MemberLogoutPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/members/memberslogin" replace />} />
        <Route path="/members/memberslogin" element={<MemberLoginPage />} />
        <Route path="/members/logout" element={<MemberLogoutPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;