import { useEffect } from "react";
import axiosClient from "../api/api";
import "./AdminLogoutPage.css";

function AdminLogoutPage() {
  useEffect(() => {
    const logout = async () => {
      try {
        await axiosClient.post("/api/admins/logout");
      } catch (error) {
        console.error("ログアウト処理に失敗しました", error);
      } finally {
        setTimeout(() => {
          window.location.href = "/admins/adminslogin";
        }, 1800);
      }
    };

    logout();
  }, []);

  return (
    <div className="admin-logout-page">
      <div className="admin-logout-card">
        <div className="admin-logout-spinner"></div>
        <h1>ログアウト中です</h1>
        <p>管理者画面からログアウトしています...</p>
      </div>
    </div>
  );
}

export default AdminLogoutPage;