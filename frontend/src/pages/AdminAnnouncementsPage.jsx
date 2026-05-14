import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

function AdminAnnouncementsPage() {
  const navigate = useNavigate();

  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAnnouncements = async () => {
    try {
      const response = await api.get("/api/admin/announcements");
      setAnnouncements(response.data);
    } catch (error) {
      console.error("お知らせ一覧取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert("お知らせ一覧の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnnouncements();
  }, []);

  const handleDelete = async (id) => {
    const confirmed = window.confirm("このお知らせを削除しますか？");

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(`/api/admin/announcements/${id}`);
      await fetchAnnouncements();
    } catch (error) {
      console.error("お知らせ削除エラー:", error);
      console.error("status:", error.response?.status);
      console.error("data:", error.response?.data);

      const status = error.response?.status;

      if (status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert(`削除に失敗しました。status: ${status}`);
      }
    }
  };

  const formatDate = (dateText) => {
    if (!dateText) {
      return "";
    }

    return String(dateText).replaceAll("-", "/");
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-container">
          <h1 className="admin-title">新着情報一覧</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <div className="admin-container">
        <h1 className="admin-title">新着情報一覧</h1>

        <div className="admin-actions">
          <button
            className="admin-btn admin-btn-primary"
            onClick={() => {
              window.location.href = "http://localhost:8080/admins/club/save";
            }}
          >
            新着情報の追加
          </button>

          <button
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              window.location.href = "http://localhost:8080/admins/club/home";
            }}
          >
            管理ホームへ戻る
          </button>

          <button
            className="admin-btn admin-btn-secondary"
            onClick={() => navigate("/admin/logout")}
          >
            ログアウト
          </button>
        </div>

        <div className="admin-table-card">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>投稿者</th>
                <th>タイトル</th>
                <th>掲載日</th>
                <th>操作</th>
              </tr>
            </thead>

            <tbody>
              {announcements.map((announcement) => (
                <tr key={announcement.id}>
                  <td>{announcement.id}</td>
                  <td>{announcement.author}</td>
                  <td>{announcement.title}</td>
                  <td>{formatDate(announcement.postDate)}</td>
                  <td>
                    <div className="admin-row-actions">
                      <button
                        className="admin-btn admin-btn-primary"
                        onClick={() => {
                          window.location.href = `http://localhost:8080/admins/club/detail/${announcement.id}`;
                        }}
                      >
                        詳細を見る
                      </button>

                      <button
                        className="admin-btn admin-btn-danger"
                        onClick={() => handleDelete(announcement.id)}
                      >
                        削除
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {announcements.length === 0 && (
                <tr>
                  <td colSpan="5">お知らせはまだ登録されていません。</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default AdminAnnouncementsPage;