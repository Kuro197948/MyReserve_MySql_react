import { useEffect, useState } from "react";
import api from "../api/api";

function MemberLoginPage() {
  const [form, setForm] = useState({
    email: "",
    loginPass: "",
  });
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const checkLogin = async () => {
      try {
        const response = await api.get("/api/members/me");
        if (response.data.loggedIn) {
          window.location.href = "http://localhost:8080/members/club/home";
        }
      } catch (error) {
        console.error("ログイン状態確認エラー:", error);
      }
    };

    checkLogin();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setLoading(true);

    try {
      const response = await api.post("/api/members/login", form);
      const data = response.data;

      if (data.success) {
        window.location.href = "http://localhost:8080/members/club/home";
        return;
      }

      setErrorMessage(data.message || "ログインに失敗しました");
    } catch (error) {
      console.error("ログインエラー:", error);

      if (error.response?.data?.message) {
        setErrorMessage(error.response.data.message);
      } else {
        setErrorMessage("通信エラーが発生しました");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        <h1 style={styles.title}>会員ログイン</h1>

        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label htmlFor="email" style={styles.label}>
              メールアドレス
            </label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              style={styles.input}
              autoComplete="email"
            />
          </div>

          <div style={styles.formGroup}>
            <label htmlFor="loginPass" style={styles.label}>
              パスワード
            </label>
            <input
              id="loginPass"
              name="loginPass"
              type="password"
              value={form.loginPass}
              onChange={handleChange}
              style={styles.input}
              autoComplete="current-password"
            />
          </div>

          {errorMessage && <p style={styles.error}>{errorMessage}</p>}

          <button type="submit" style={styles.button} disabled={loading}>
            {loading ? "ログイン中..." : "ログイン"}
          </button>
        </form>
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
  },
  title: {
    textAlign: "center",
    marginBottom: "24px",
  },
  formGroup: {
    marginBottom: "16px",
  },
  label: {
    display: "block",
    marginBottom: "8px",
    fontWeight: "bold",
  },
  input: {
    width: "100%",
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "6px",
    boxSizing: "border-box",
  },
  button: {
    width: "100%",
    padding: "12px",
    border: "none",
    borderRadius: "6px",
    backgroundColor: "#0d6efd",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer",
  },
  error: {
    color: "red",
    marginBottom: "16px",
  },
};

export default MemberLoginPage;