import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import client from "../api/client";
import "./AdminAnnouncementCreatePage.css";

const MAX_FILE_SIZE = 10_000_000;

function AdminAnnouncementCreatePage() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [postDate, setPostDate] = useState("");
  const [targetIdList, setTargetIdList] = useState([]);
  const [article, setArticle] = useState("");
  const [upfile, setUpfile] = useState(null);

  const [errorMessage, setErrorMessage] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const memberTypeList = [
    { id: 1, name: "通常会員" },
    { id: 2, name: "プレミアム会員" },
  ];

  const handleTargetChange = (id) => {
    setTargetIdList((prevList) => {
      if (prevList.includes(id)) {
        return prevList.filter((targetId) => targetId !== id);
      }

      return [...prevList, id];
    });
  };

  const validate = () => {
    const errors = {};

    if (!title.trim()) {
      errors.title = "タイトルを入力してください。";
    }

    if (!postDate) {
      errors.postDate = "掲載日を入力してください。";
    }

    if (!article.trim()) {
      errors.article = "内容を入力してください。";
    }

    if (upfile && upfile.size >= MAX_FILE_SIZE) {
      errors.upfile = "ファイルサイズが大き過ぎます。10MB未満の画像を選択してください。";
    }

    setFieldErrors(errors);

    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setErrorMessage("");

    if (!validate()) {
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

      await client.post("/admin/announcements", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      navigate("/admins/club/announcements");
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("新着情報の追加中にエラーが発生しました。");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="admin-news-create-page">
      <div className="admin-news-create-card">
        <div className="admin-news-create-header">
          <div>
            <p className="admin-news-create-label">Admin Announcement</p>
            <h1 className="admin-news-create-title">新着情報の追加</h1>
          </div>

          <Link
            to="/admins/club/announcements"
            className="admin-news-create-back-button"
          >
            一覧に戻る
          </Link>
        </div>

        {errorMessage && (
          <div className="admin-news-create-alert">
            {errorMessage}
          </div>
        )}

        <form className="admin-news-create-form" onSubmit={handleSubmit}>
          <div className="admin-news-create-field">
            <label htmlFor="title">タイトル</label>
            {fieldErrors.title && (
              <span className="admin-news-create-error">
                {fieldErrors.title}
              </span>
            )}
            <input
              id="title"
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="例：春の大感謝祭"
            />
          </div>

          <div className="admin-news-create-field">
            <label htmlFor="postDate">掲載日</label>
            {fieldErrors.postDate && (
              <span className="admin-news-create-error">
                {fieldErrors.postDate}
              </span>
            )}
            <input
              id="postDate"
              type="date"
              value={postDate}
              onChange={(event) => setPostDate(event.target.value)}
            />
          </div>

          <div className="admin-news-create-field">
            <label>配信の対象</label>
            <p className="admin-news-create-help">
              対象を限定する場合はチェックしてください。
            </p>

            <div className="admin-news-create-checkbox-area">
              {memberTypeList.map((type) => (
                <label
                  key={type.id}
                  className="admin-news-create-checkbox-label"
                >
                  <input
                    type="checkbox"
                    checked={targetIdList.includes(type.id)}
                    onChange={() => handleTargetChange(type.id)}
                  />
                  <span>{type.name}</span>
                </label>
              ))}
            </div>
          </div>

          <div className="admin-news-create-field">
            <label htmlFor="article">内容</label>
            {fieldErrors.article && (
              <span className="admin-news-create-error">
                {fieldErrors.article}
              </span>
            )}
            <textarea
              id="article"
              rows="7"
              value={article}
              onChange={(event) => setArticle(event.target.value)}
              placeholder="お知らせ内容を入力してください。"
            />
          </div>

          <div className="admin-news-create-field">
            <label htmlFor="upfile">イメージ画像</label>
            {fieldErrors.upfile && (
              <span className="admin-news-create-error">
                {fieldErrors.upfile}
              </span>
            )}
            <input
              id="upfile"
              type="file"
              accept="image/*"
              onChange={(event) => setUpfile(event.target.files[0] || null)}
            />
          </div>

          <div className="admin-news-create-submit-area">
            <button
              type="submit"
              className="admin-news-create-submit-button"
              disabled={submitting}
            >
              {submitting ? "送信中..." : "追加する"}
            </button>
          </div>
        </form>
      </div>
    </main>
  );
}

export default AdminAnnouncementCreatePage;