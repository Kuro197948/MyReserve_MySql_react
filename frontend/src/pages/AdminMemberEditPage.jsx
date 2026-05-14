import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import client from "../api/client";
import "./AdminMemberFormPage.css";

function AdminMemberEditPage() {
  const { id } = useParams();
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
    const fetchData = async () => {
      try {
        const [typesResponse, memberResponse] = await Promise.all([
          client.get("/admin/members/types"),
          client.get(`/admin/members/${id}`),
        ]);

        setTypes(typesResponse.data);

        const member = memberResponse.data;

        setName(member.name ?? "");
        setEmail(member.email ?? "");

        const resolvedMemberTypeId =
          member.memberTypeId ??
          member.typeId ??
          member.memberType?.id ??
          "";

        setMemberTypeId(String(resolvedMemberTypeId));
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/admins/adminslogin");
          return;
        }

        if (error.response?.status === 404) {
          setErrorMessage("会員情報が見つかりませんでした。");
          return;
        }

        setErrorMessage("会員情報の取得中にエラーが発生しました。");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, navigate]);

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

      await client.put(`/admin/members/${id}`, {
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

      setErrorMessage("会員情報の更新中にエラーが発生しました。");
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
            <h1 className="admin-member-form-title">会員情報の変更</h1>
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
              {submitting ? "更新中..." : "更新する"}
            </button>
          </div>
        </form>
      </div>
    </main>
  );
}

export default AdminMemberEditPage;