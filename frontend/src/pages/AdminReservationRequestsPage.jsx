import { useEffect, useState } from "react";
import api from "../api/api";

function AdminReservationRequestsPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchRequests = async () => {
    try {
      setLoading(true);

      const response = await api.get("/api/admin/reservation-requests");
      setRequests(response.data);
    } catch (error) {
      console.error("予約申請一覧取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert("予約申請一覧の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const formatDateTime = (dateText) => {
    if (!dateText) {
      return "";
    }

    if (Array.isArray(dateText)) {
      const [year, month, day, hour = 0, minute = 0] = dateText;
      return `${year}/${String(month).padStart(2, "0")}/${String(day).padStart(
        2,
        "0"
      )} ${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
    }

    const date = new Date(dateText);

    if (Number.isNaN(date.getTime())) {
      return String(dateText).replace("T", " ").replaceAll("-", "/").slice(0, 16);
    }

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mi = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}/${mm}/${dd} ${hh}:${mi}`;
  };

  const getTextOrDefault = (value, defaultText) => {
    if (value === null || value === undefined || String(value).trim() === "") {
      return defaultText;
    }

    return value;
  };

  const handleApprove = async (request) => {
    const confirmed = window.confirm(
      `${request.requestTypeLabel}を承認します。よろしいですか？`
    );

    if (!confirmed) {
      return;
    }

    const adminComment =
      request.requestType === "CHANGE"
        ? "変更申請を承認しました。"
        : "キャンセル申請を承認しました。";

    try {
      await api.post(`/api/admin/reservation-requests/${request.id}/approve`, {
        adminComment,
      });

      await fetchRequests();
    } catch (error) {
      console.error("予約申請承認エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert(error.response?.data?.message || "承認に失敗しました。");
      }
    }
  };

  const handleReject = async (request) => {
    const confirmed = window.confirm(
      `${request.requestTypeLabel}を却下します。よろしいですか？`
    );

    if (!confirmed) {
      return;
    }

    const adminComment =
      request.requestType === "CHANGE"
        ? "変更申請を却下しました。"
        : "キャンセル申請を却下しました。";

    try {
      await api.post(`/api/admin/reservation-requests/${request.id}/reject`, {
        adminComment,
      });

      await fetchRequests();
    } catch (error) {
      console.error("予約申請却下エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert(error.response?.data?.message || "却下に失敗しました。");
      }
    }
  };

  const getRequestBadgeClass = (requestType) => {
    if (requestType === "CANCEL") {
      return "admin-request-type-badge admin-request-type-cancel";
    }

    if (requestType === "CHANGE") {
      return "admin-request-type-badge admin-request-type-change";
    }

    return "admin-request-type-badge";
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-container">
          <h1 className="admin-title">予約申請一覧</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page admin-request-react-page">
      <div className="admin-container">
        <div className="admin-page-heading-center">
          <p className="admin-page-label">REQUEST MANAGEMENT</p>
          <h1 className="admin-title">予約申請一覧</h1>
          <p className="admin-page-description">
            会員から送信されたキャンセル申請・変更申請を確認し、
            承認または却下できます。
          </p>
        </div>

        {requests.length === 0 && (
          <div className="admin-empty-card">
            現在、承認待ちの予約申請はありません。
          </div>
        )}

        <div className="admin-request-list">
          {requests.map((request) => (
            <div key={request.id} className="admin-request-card-react">
              <div className="admin-request-card-top">
                <div>
                  <span className={getRequestBadgeClass(request.requestType)}>
                    {request.requestTypeLabel}
                  </span>

                  <h2 className="admin-request-card-title">
                    {getTextOrDefault(request.courseName, "コース未設定")}
                  </h2>
                </div>

                <span className="admin-request-status-badge">
                  {request.statusLabel}
                </span>
              </div>

              {request.requestType === "CANCEL" && (
                <div className="admin-request-warning admin-request-warning-danger">
                  この申請を承認すると、対象予約はキャンセル済みになり、
                  予約履歴へ移動します。
                </div>
              )}

              {request.requestType === "CHANGE" && (
                <div className="admin-request-warning admin-request-warning-primary">
                  この申請を承認すると、下記の変更希望内容で予約情報が更新されます。
                </div>
              )}

              <div className="admin-request-detail-grid">
                <section className="admin-request-section">
                  <h3>会員情報</h3>
                  <p>
                    <strong>会員名：</strong>
                    {request.memberName}
                  </p>
                  <p>
                    <strong>メール：</strong>
                    {request.memberEmail}
                  </p>
                </section>

                <section className="admin-request-section">
                  <h3>現在の予約内容</h3>
                  <p>
                    <strong>予約日時：</strong>
                    {formatDateTime(request.reservationDate)}
                  </p>
                  <p>
                    <strong>人数：</strong>
                    {request.peopleCount}名
                  </p>
                  <p>
                    <strong>代表者名：</strong>
                    {request.representativeName}
                  </p>
                  <p>
                    <strong>電話番号：</strong>
                    {request.phoneNumber}
                  </p>
                  <p>
                    <strong>現在のコース：</strong>
                    {getTextOrDefault(request.courseName, "コース未設定")}
                  </p>
                </section>

                {request.requestType === "CHANGE" && (
                  <section className="admin-request-section">
                    <h3>変更希望内容</h3>
                    <p>
                      <strong>変更希望日時：</strong>
                      {request.requestedReservationDate
                        ? formatDateTime(request.requestedReservationDate)
                        : "変更なし"}
                    </p>
                    <p>
                      <strong>変更希望人数：</strong>
                      {request.requestedPeopleCount
                        ? `${request.requestedPeopleCount}名`
                        : "変更なし"}
                    </p>
                    <p>
                      <strong>変更希望コース：</strong>
                      {getTextOrDefault(request.requestedCourseName, "変更なし")}
                    </p>
                    <p>
                      <strong>変更希望備考：</strong>
                      {getTextOrDefault(request.requestedRemarks, "変更なし")}
                    </p>
                  </section>
                )}

                <section className="admin-request-section">
                  <h3>申請内容</h3>
                  <p>
                    <strong>申請理由：</strong>
                    {getTextOrDefault(request.requestReason, "理由なし")}
                  </p>
                  <p>
                    <strong>申請日時：</strong>
                    {formatDateTime(request.createdAt)}
                  </p>
                </section>
              </div>

              <div className="admin-request-action-row">
                <button
                  className="admin-btn admin-btn-success-outline"
                  onClick={() => handleApprove(request)}
                >
                  {request.requestType === "CHANGE"
                    ? "変更申請を承認する"
                    : "キャンセル申請を承認する"}
                </button>

                <button
                  className="admin-btn admin-btn-danger-outline"
                  onClick={() => handleReject(request)}
                >
                  {request.requestType === "CHANGE"
                    ? "変更申請を却下する"
                    : "キャンセル申請を却下する"}
                </button>
              </div>
            </div>
          ))}
        </div>

        <div className="admin-bottom-actions">
          <button
            className="admin-btn admin-btn-outline"
            onClick={() => {
              window.location.href = "http://localhost:5175/admin/reservations";
            }}
          >
            現在予約へ戻る
          </button>

          <button
            className="admin-btn admin-btn-secondary"
            onClick={() => {
              window.location.href = "http://localhost:8080/admins/club/home";
            }}
          >
            管理ホームへ戻る
          </button>
        </div>
      </div>
    </div>
  );
}

export default AdminReservationRequestsPage;