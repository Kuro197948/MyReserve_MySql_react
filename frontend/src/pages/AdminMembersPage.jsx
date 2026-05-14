import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./AdminMembersPage.css";

function AdminMembersPage() {
  const navigate = useNavigate();

  const [members, setMembers] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);

  const fetchMembers = async (targetPage = 1) => {
    try {
      setLoading(true);

      const response = await api.get(`/api/admin/members?page=${targetPage}`);

      setMembers(response.data.members ?? []);
      setPage(response.data.page ?? 1);
      setTotalPages(response.data.totalPages ?? 1);
    } catch (error) {
      console.error("会員一覧取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert("会員一覧の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers(1);
  }, []);

  const handleDelete = async (member) => {
    const confirmed = window.confirm(
      `「${member.name}」を削除します。よろしいですか？`
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(`/api/admin/members/${member.id}`);

      const nextPage = members.length === 1 && page > 1 ? page - 1 : page;
      await fetchMembers(nextPage);
    } catch (error) {
      console.error("会員削除エラー:", error);
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

    const date = new Date(dateText);

    if (Number.isNaN(date.getTime())) {
      return String(dateText).replaceAll("-", "/");
    }

    return new Intl.DateTimeFormat("ja-JP", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    }).format(date);
  };

  const movePage = (targetPage) => {
    if (targetPage < 1 || targetPage > totalPages || targetPage === page) {
      return;
    }

    fetchMembers(targetPage);
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-container">
          <h1 className="admin-title">会員一覧</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <div className="admin-container">
        <h1 className="admin-title">会員一覧</h1>

        <div className="admin-table-card">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>氏名</th>
                <th>会員種別</th>
                <th>登録日</th>
                <th>操作</th>
              </tr>
            </thead>

            <tbody>
              {members.map((member) => (
                <tr key={member.id}>
                  <td>{member.id}</td>
                  <td>{member.name}</td>
                  <td>{member.typeName}</td>
                  <td>{formatDate(member.created)}</td>
                  <td>
                    <div className="admin-row-actions">
                      <button
                        type="button"
                        className="admin-btn admin-btn-primary"
                        onClick={() => {
                          navigate(`/admin/members/${member.id}/edit`);
                        }}
                      >
                        編集
                      </button>

                      <button
                        type="button"
                        className="admin-btn admin-btn-danger"
                        onClick={() => handleDelete(member)}
                      >
                        削除
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {members.length === 0 && (
                <tr>
                  <td colSpan="5">会員はまだ登録されていません。</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {totalPages >= 2 && (
          <div className="admin-pagination">
            <button
              type="button"
              className="admin-page-btn"
              disabled={page === 1}
              onClick={() => movePage(page - 1)}
            >
              &laquo;
            </button>

            {Array.from({ length: totalPages }, (_, index) => index + 1).map(
              (pageNumber) => (
                <button
                  type="button"
                  key={pageNumber}
                  className={
                    pageNumber === page
                      ? "admin-page-btn admin-page-btn-active"
                      : "admin-page-btn"
                  }
                  onClick={() => movePage(pageNumber)}
                >
                  {pageNumber}
                </button>
              )
            )}

            <button
              type="button"
              className="admin-page-btn"
              disabled={page === totalPages}
              onClick={() => movePage(page + 1)}
            >
              &raquo;
            </button>
          </div>
        )}

        <div className="admin-list-bottom-actions">
          <Link
            to="/admin/members/new"
            className="admin-list-action-button primary"
          >
            会員の追加
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
    </div>
  );
}

export default AdminMembersPage;