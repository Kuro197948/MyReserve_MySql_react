import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

function MemberHomePage() {
  const navigate = useNavigate();

  const [memberName, setMemberName] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const response = await api.get("/api/members/me");
        const data = response.data;

        if (!data.loggedIn) {
          navigate("/members/memberslogin");
          return;
        }

        setMemberName(data.memberName);
      } catch (error) {
        console.error(error);
        navigate("/members/memberslogin");
      } finally {
        setLoading(false);
      }
    };

    fetchMe();
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await api.post("/api/members/logout");
      navigate("/members/memberslogin");
    } catch (error) {
      console.error(error);
      alert("ログアウトに失敗しました");
    }
  };

  if (loading) {
    return <p style={{ padding: "24px" }}>読み込み中...</p>;
  }

  return (
    <div style={{ maxWidth: "600px", margin: "40px auto" }}>
      <h1>会員ホーム</h1>
      <p>{memberName} さん、ようこそ</p>

      <div style={{ marginTop: "24px" }}>
        <button onClick={handleLogout} style={{ padding: "8px 16px" }}>
          ログアウト
        </button>
      </div>
    </div>
  );
}

export default MemberHomePage;