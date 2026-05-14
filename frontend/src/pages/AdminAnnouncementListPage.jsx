import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import client from "../api/client";
import "./AdminAnnouncementListPage.css";

function formatJapaneseDate(value) {
  if (!value) return "";

  const datePart = value.split("T")[0];
  const [year, month, day] = datePart.split("-");

  if (!year || !month || !day) {
    return value;
  }

  return `${year}年${month}月${day}日`;
}

function AdminAnnouncementListPage() {
  const navigate = useNavigate();

  const [newsList, setNewsList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusMessage, setStatusMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const fetchNewsList = async () => {
    try {
      setLoading(true);
      setErrorMessage("");

      const response = await client.get("/admin/announcements");
      setNewsList(response.data);
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("新着情報一覧の取得中にエラーが発生しました。");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNewsList();
  }, []);

  const handleDelete = async (id) => {
    const confirmed = window.confirm("本当に削除しますか？");

    if (!confirmed) {
      return;
    }

    try {
      setStatusMessage("");
      setErrorMessage("");

      await client.delete(`/admin/announcements/${id}`);

      setNewsList((prevList) =>
        prevList.filter((news) => news.id !== id)
      );

      setStatusMessage("お知らせを削除しました。");
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("お知らせの削除中にエラーが発生しました。");
    }
  };

  if (loading) {
    return (
      <main className="admin-news-list-page">
        <div className="admin-news-list-card">
          <p className="admin-news-list-loading">読み込み中...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-news-list-page">
      <div className="admin-news-list-card">
        <div className="admin-news-list-header">
          <div>
            <p className="admin-news-list-label">Admin Announcements</p>
            <h1 className="admin-news-list-title">新着情報一覧</h1>
          </div>
        </div>

        {statusMessage && (
          <div className="admin-news-list-alert success">
            {statusMessage}
          </div>
        )}

        {errorMessage && (
          <div className="admin-news-list-alert error">
            {errorMessage}
          </div>
        )}

        {newsList.length === 0 ? (
          <div className="admin-news-list-empty">
            現在、登録されている新着情報はありません。
          </div>
        ) : (
          <div className="admin-news-list-table-wrap">
            <table className="admin-news-list-table">
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
                {newsList.map((news) => (
                  <tr key={news.id}>
                    <td>{news.id}</td>
                    <td>{news.author}</td>
                    <td className="admin-news-list-title-cell">
                      {news.title}
                    </td>
                    <td>{formatJapaneseDate(news.postDate)}</td>
                    <td>
                      <div className="admin-news-list-row-actions">
                        <Link
                          to={`/admin/announcements/${news.id}`}
                          className="admin-news-list-small-button detail"
                        >
                          詳細を見る
                        </Link>

                        <button
                          type="button"
                          className="admin-news-list-small-button delete"
                          onClick={() => handleDelete(news.id)}
                        >
                          削除
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div className="admin-list-bottom-actions">
          <Link
            to="/admin/announcements/new"
            className="admin-list-action-button primary"
          >
            新着情報の追加
          </Link>

          <Link
            to="/admin/home"
            className="admin-list-action-button secondary"
          >
            管理ホームへ戻る
          </Link>

          <Link
            to="/admin/logout"
            className="admin-list-action-button logout"
          >
            ログアウト
          </Link>
        </div>
      </div>
    </main>
  );
}

export default AdminAnnouncementListPage;