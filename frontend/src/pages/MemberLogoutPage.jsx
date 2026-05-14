import { useEffect, useState } from "react";
import api from "../api/api";
import "./MemberLogoutPage.css";

function MemberLogoutPage() {
  const [message, setMessage] = useState("会員画面からログアウトしています...");

  useEffect(() => {
    const logout = async () => {
      try {
        await api.post("/api/members/logout");
        setMessage("ログアウトしました。ログイン画面へ移動します...");
      } catch (error) {
        console.error("ログアウトエラー:", error);
        setMessage("ログアウト処理で問題が発生しました。ログイン画面へ移動します...");
      } finally {
        setTimeout(() => {
          window.location.replace("http://localhost:8080/members/memberslogin");
        }, 800);
      }
    };

    logout();
  }, []);

  return (
    <div className="member-logout-page">
      <div className="member-logout-card">
        <div className="member-logout-spinner"></div>
        <h1>ログアウト中です</h1>
        <p>{message}</p>
      </div>
    </div>
  );
}

export default MemberLogoutPage;