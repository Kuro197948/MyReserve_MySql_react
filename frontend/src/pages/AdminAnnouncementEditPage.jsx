import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import client from "../api/client";
import "./AdminAnnouncementEditPage.css";

const API_ORIGIN = "http://localhost:8080";

function toDateInputValue(value) {
  if (!value) return "";
  return value.split("T")[0];
}

function AdminAnnouncementEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [postDate, setPostDate] = useState("");
  const [article, setArticle] = useState("");
  const [targetIdList, setTargetIdList] = useState([1, 2]);
  const [upfile, setUpfile] = useState(null);
  const [currentPhoto, setCurrentPhoto] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const fetchNewsDetail = async () => {
      try {
        const response = await client.get(`/admin/announcements/${id}`);
        const news = response.data;

        setTitle(news.title || "");
        setPostDate(toDateInputValue(news.postDate));
        setArticle(news.detail?.article || "");
        setCurrentPhoto(news.detail?.photo || "");

        // 現時点では詳細APIに配信対象IDが無いため、初期値は両方チェックにしておく
        setTargetIdList([1, 2]);
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/admins/adminslogin");
          return;
        }

        if (error.response?.status === 404) {
          setErrorMessage("お知らせが見つかりませんでした。");
          return;
        }

        setErrorMessage("お知らせ情報の取得中にエラーが発生しました。");
      } finally {
        setLoading(false);
      }
    };

    fetchNewsDetail();
  }, [id, navigate]);

  const handleTargetChange = (targetId) => {
    setTargetIdList((prev) => {
      if (prev.includes(targetId)) {
        return prev.filter((id) => id !== targetId);
      }

      return [...prev, targetId];
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage("");

    if (!title.trim()) {
      setErrorMessage("タイトルを入力してください。");
      return;
    }

    if (!postDate) {
      setErrorMessage("掲載日を入力してください。");
      return;
    }

    if (!article.trim()) {
      setErrorMessage("内容を入力してください。");
      return;
    }

    if (targetIdList.length === 0) {
      setErrorMessage("配信対象を1つ以上選択してください。");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("postDate", postDate);
    formData.append("article", article);

    targetIdList.forEach((targetId) => {
      formData.append("targetIdList", targetId);
    });

    if (upfile) {
      formData.append("upfile", upfile);
    }

    try {
      setSubmitting(true);

      await client.put(`/admin/announcements/${id}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      navigate(`/admins/club/announcements/${id}`);
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("お知らせの更新中にエラーが発生しました。");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <main className="admin-news-edit-page">
        <div className="admin-news-edit-card">
          <p className="admin-news-edit-loading">読み込み中...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-news-edit-page">
      <div className="admin-news-edit-card">
        <div className="admin-news-edit-header">
          <div>
            <p className="admin-news-edit-label">Admin Announcement</p>
            <h1 className="admin-news-edit-title">新着情報編集</h1>
          </div>

          <Link
            to={`/admins/club/announcements/${id}`}
            className="admin-news-edit-back-button"
          >
            詳細に戻る
          </Link>
        </div>

        {errorMessage && (
          <div className="admin-news-edit-alert">
            {errorMessage}
          </div>
        )}

        <form className="admin-news-edit-form" onSubmit={handleSubmit}>
          <div className="admin-news-edit-field">
            <label>タイトル</label>
            <input
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="タイトルを入力してください"
            />
          </div>

          <div className="admin-news-edit-field">
            <label>掲載日</label>
            <input
              type="date"
              value={postDate}
              onChange={(event) => setPostDate(event.target.value)}
            />
          </div>

          <div className="admin-news-edit-field">
            <label>配信の対象</label>

            <div className="admin-news-edit-check-area">
              <label className="admin-news-edit-check-card regular">
                <input
                  type="checkbox"
                  checked={targetIdList.includes(1)}
                  onChange={() => handleTargetChange(1)}
                />
                <span>通常会員</span>
              </label>

              <label className="admin-news-edit-check-card premium">
                <input
                  type="checkbox"
                  checked={targetIdList.includes(2)}
                  onChange={() => handleTargetChange(2)}
                />
                <span>プレミアム会員</span>
              </label>
            </div>
          </div>

          <div className="admin-news-edit-field">
            <label>内容</label>
            <textarea
              value={article}
              onChange={(event) => setArticle(event.target.value)}
              placeholder="お知らせの内容を入力してください"
              rows="7"
            />
          </div>

          <div className="admin-news-edit-field">
            <label>イメージ画像</label>

            {currentPhoto && (
              <div className="admin-news-edit-current-image-area">
                <p>現在の画像</p>
                <img
                  className="admin-news-edit-current-image"
                  src={`${API_ORIGIN}/uploads/${currentPhoto}`}
                  alt={title}
                />
              </div>
            )}

            <input
              type="file"
              accept="image/*"
              onChange={(event) => setUpfile(event.target.files[0])}
            />

            <p className="admin-news-edit-help">
              画像を変更しない場合は、未選択のままで大丈夫です。
            </p>
          </div>

          <div className="admin-news-edit-actions">
            <button
              type="submit"
              className="admin-news-edit-submit-button"
              disabled={submitting}
            >
              {submitting ? "更新中..." : "更新する"}
            </button>

            <Link
              to={`/admins/club/announcements/${id}`}
              className="admin-news-edit-cancel-button"
            >
              キャンセル
            </Link>
          </div>
        </form>
      </div>
    </main>
  );
}

export default AdminAnnouncementEditPage;