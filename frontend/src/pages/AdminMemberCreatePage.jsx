import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import client from "../api/client";
import "./AdminMemberFormPage.css";

function AdminMemberCreatePage() {
  const navigate = useNavigate();

  const [types, setTypes] = useState([]);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [memberTypeId, setMemberTypeId] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    const fetchTypes = async () => {
      try {
        const response = await client.get("/admin/members/types");
        setTypes(response.data);

        if (response.data.length > 0) {
          setMemberTypeId(String(response.data[0].id));
        }
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/admins/adminslogin");
          return;
        }

        setErrorMessage("会員種別の取得中にエラーが発生しました。");
      } finally {
        setLoading(false);
      }
    };

    fetchTypes();
  }, [navigate]);

  const validate = () => {
    const errors = {};

    if (!name.trim()) {
      errors.name = "氏名を入力してください。";
    }

    if (!email.trim()) {
      errors.email = "メールアドレスを入力してください。";
    }

    if (!memberTypeId) {
      errors.memberTypeId = "会員種別を選択してください。";
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

    try {
      setSubmitting(true);

      await client.post("/admin/members", {
        name,
        email,
        memberTypeId: Number(memberTypeId),
      });

      navigate("/admin/members");
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("会員の追加中にエラーが発生しました。");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <main className="admin-member-form-page">
        <div className="admin-member-form-card">
          <p className="admin-member-form-loading">読み込み中...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-member-form-page">
      <div className="admin-member-form-card">
        <div className="admin-member-form-header">
          <div>
            <p className="admin-member-form-label">MEMBER MANAGEMENT</p>
            <h1 className="admin-member-form-title">会員の追加</h1>
          </div>

          <Link to="/admin/members" className="admin-member-form-back-button">
            一覧に戻る
          </Link>
        </div>

        {errorMessage && (
          <div className="admin-member-form-alert">
            {errorMessage}
          </div>
        )}

        <form className="admin-member-form" onSubmit={handleSubmit}>
          <div className="admin-member-form-field">
            <label htmlFor="name">氏名</label>
            {fieldErrors.name && (
              <span className="admin-member-form-error">
                {fieldErrors.name}
              </span>
            )}
            <input
              id="name"
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="例：山田太郎"
            />
          </div>

          <div className="admin-member-form-field">
            <label htmlFor="email">メールアドレス</label>
            {fieldErrors.email && (
              <span className="admin-member-form-error">
                {fieldErrors.email}
              </span>
            )}
            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="例：sample@example.com"
            />
          </div>

          <div className="admin-member-form-field">
            <label htmlFor="memberTypeId">会員種別</label>
            {fieldErrors.memberTypeId && (
              <span className="admin-member-form-error">
                {fieldErrors.memberTypeId}
              </span>
            )}
            <select
              id="memberTypeId"
              value={memberTypeId}
              onChange={(event) => setMemberTypeId(event.target.value)}
            >
              {types.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.name}
                </option>
              ))}
            </select>
          </div>

          <div className="admin-member-form-submit-area">
            <button
              type="submit"
              className="admin-member-form-submit-button"
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

export default AdminMemberCreatePage;