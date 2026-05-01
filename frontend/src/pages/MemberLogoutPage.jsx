import { useEffect, useState } from "react";
import api from "../api/api";

function MemberLogoutPage() {
  const [message, setMessage] = useState("ログアウトしています...");

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
          window.location.href = "http://localhost:8080/members/memberslogin";
        }, 1200);
      }
    };

    logout();
  }, []);

  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        <h1 style={styles.title}>ログアウト</h1>
        <p style={styles.text}>{message}</p>
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    padding: "16px",
  },
  card: {
    width: "100%",
    maxWidth: "420px",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
    padding: "32px",
    textAlign: "center",
  },
  title: {
    marginBottom: "16px",
  },
  text: {
    fontSize: "16px",
  },
};

export default MemberLogoutPage;