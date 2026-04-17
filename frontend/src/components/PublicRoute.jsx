import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function PublicRoute({ children }) {
  const { loading, loggedIn } = useAuth();

  if (loading) {
    return <p>確認中...</p>;
  }

  if (loggedIn) {
    return <Navigate to="/members/club/home" replace />;
  }

  return children;
}