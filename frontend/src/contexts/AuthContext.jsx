import { useEffect, useState } from "react";
import client from "../api/client";
import { AuthContext } from "./authContext";

export function AuthProvider({ children }) {
  const [member, setMember] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchMe = async () => {
    try {
      const response = await client.get("/api/member/me");
      setMember({
        memberId: response.data.memberId,
        memberName: response.data.memberName,
      });
    } catch {
      setMember(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMe();
  }, []);

  const login = async (email, password) => {
    const response = await client.post("/api/member/login", {
      email,
      password,
    });

    setMember({
      memberId: response.data.memberId,
      memberName: response.data.memberName,
    });

    return response;
  };

  const logout = async () => {
    await client.post("/api/member/logout");
    setMember(null);
  };

  return (
    <AuthContext.Provider
      value={{
        member,
        loading,
        login,
        logout,
        fetchMe,
        loggedIn: member !== null,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}