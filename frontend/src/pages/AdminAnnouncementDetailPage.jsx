import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import client from "../api/client";
import "./AdminAnnouncementDetailPage.css";

const API_ORIGIN = "http://localhost:8080";

function formatJapaneseDate(value) {
  if (!value) return "";

  const datePart = value.split("T")[0];
  const [year, month, day] = datePart.split("-");

  if (!year || !month || !day) {
    return value;
  }

  return `${year}年${month}月${day}日`;
}

function AdminAnnouncementDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [news, setNews] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const fetchNewsDetail = async () => {
      try {
        const response = await client.get(`/admin/announcements/${id}`);
        setNews(response.data);
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/admins/adminslogin");
          return;
        }

        if (error.response?.status === 404) {
          setErrorMessage("お知らせが見つかりませんでした。");
          return;
        }

        setErrorMessage("お知らせ詳細の取得中にエラーが発生しました。");
      } finally {
        setLoading(false);
      }
    };

    fetchNewsDetail();
  }, [id, navigate]);

  if (loading) {
    return (
      <main className="admin-news-detail-page">
        <div className="admin-news-detail-card">
          <p className="admin-news-detail-loading">読み込み中...</p>
        </div>
      </main>
    );
  }

  if (errorMessage) {
    return (
      <main className="admin-news-detail-page">
        <div className="admin-news-detail-card">
          <h1 className="admin-news-detail-title">新着情報詳細</h1>

          <div className="admin-news-detail-alert">
            {errorMessage}
          </div>

          <Link
            to="/admins/club/announcements"
            className="admin-news-detail-back-button"
          >
            一覧に戻る
          </Link>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-news-detail-page">
      <div className="admin-news-detail-card">
        <div className="admin-news-detail-header">
          <div>
            <p className="admin-news-detail-label">Admin Announcement</p>
            <h1 className="admin-news-detail-title">新着情報詳細</h1>
          </div>

          <div className="admin-news-detail-header-actions">
            <Link
              to={`/admins/club/announcements/edit/${news.id}`}
              className="admin-news-detail-edit-button"
            >
              編集する
            </Link>

            <Link
              to="/admins/club/announcements"
              className="admin-news-detail-back-button"
            >
              一覧に戻る
            </Link>
          </div>
        </div>

        <table className="admin-news-detail-table">
          <tbody>
            <tr>
              <th>ID</th>
              <td>{news.id}</td>
            </tr>

            <tr>
              <th>投稿者</th>
              <td>{news.author}</td>
            </tr>

            <tr>
              <th>配信の対象</th>
              <td>
                <div className="admin-news-detail-badge-area">
                  <span className="admin-news-detail-badge regular">
                    通常会員
                  </span>
                  <span className="admin-news-detail-badge premium">
                    プレミアム会員
                  </span>
                </div>
              </td>
            </tr>

            <tr>
              <th>タイトル</th>
              <td>{news.title}</td>
            </tr>

            <tr>
              <th>掲載日</th>
              <td>{formatJapaneseDate(news.postDate)}</td>
            </tr>

            <tr>
              <th>内容</th>
              <td className="admin-news-detail-article">
                {news.detail?.article || "本文なし"}
              </td>
            </tr>

            {news.detail?.photo && (
              <tr>
                <th>イメージ画像</th>
                <td>
                  <img
                    className="admin-news-detail-image"
                    src={`${API_ORIGIN}/uploads/${news.detail.photo}`}
                    alt={news.title}
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </main>
  );
}

export default AdminAnnouncementDetailPage;