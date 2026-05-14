import { useEffect, useState } from "react";
import api from "../api/api";

function AdminReservationsPage() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchReservations = async () => {
    try {
      setLoading(true);

      const response = await api.get("/api/admin/reservations");
      setReservations(response.data);
    } catch (error) {
      console.error("現在予約一覧取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert("現在予約一覧の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  const handleCancel = async (reservation) => {
    const confirmed = window.confirm(
      `予約ID:${reservation.id} をキャンセル済みにします。よろしいですか？`
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.post(`/api/admin/reservations/${reservation.id}/cancel`);
      await fetchReservations();
    } catch (error) {
      console.error("予約キャンセル処理エラー:", error);
      console.error("status:", error.response?.status);
      console.error("data:", error.response?.data);

      const status = error.response?.status;

      if (status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert(`キャンセル処理に失敗しました。status: ${status}`);
      }
    }
  };

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
      return String(dateText)
        .replace("T", " ")
        .replaceAll("-", "/")
        .slice(0, 16);
    }

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mi = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}/${mm}/${dd} ${hh}:${mi}`;
  };

  const getCourseName = (reservation) => {
    if (reservation.courseName && reservation.courseName.trim() !== "") {
      return reservation.courseName;
    }

    return "コース未設定";
  };

  const getStatusBadgeClass = (status) => {
    if (status === "RESERVED") {
      return "admin-status-badge admin-status-reserved";
    }

    if (status === "CANCELED") {
      return "admin-status-badge admin-status-canceled";
    }

    if (status === "COMPLETED") {
      return "admin-status-badge admin-status-completed";
    }

    return "admin-status-badge";
  };

  const getPendingRequestBadge = (pendingRequestType) => {
    if (pendingRequestType === "CANCEL") {
      return (
        <span className="admin-request-mini-badge admin-request-mini-cancel">
          キャンセル申請中
        </span>
      );
    }

    if (pendingRequestType === "CHANGE") {
      return (
        <span className="admin-request-mini-badge admin-request-mini-change">
          変更申請中
        </span>
      );
    }

    return null;
  };

  if (loading) {
    return (
      <div className="admin-page admin-reservation-page">
        <div className="admin-container">
          <h1 className="admin-title">現在予約</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page admin-reservation-page">
      <div className="admin-container">
        <div className="admin-page-heading-center">
          <p className="admin-page-label">RESERVATION MANAGEMENT</p>
          <h1 className="admin-title">現在予約</h1>
          <p className="admin-page-description">
            予約済み・来店予定の予約を確認できます。
            キャンセル済み・完了済みの予約は予約履歴から確認してください。
          </p>
        </div>

        <div className="admin-table-card">
          <div className="admin-table-header">
            <div>
              <h2 className="admin-section-title">予約一覧</h2>
              <p className="admin-section-description">
                現在有効な予約のみ表示しています。
              </p>
            </div>

            <button
              className="admin-btn admin-btn-outline"
              onClick={() => {
                window.location.href =
                  "http://localhost:5175/admin/reservations/history";
              }}
            >
              予約履歴を見る
            </button>
          </div>

          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>予約日時</th>
                <th>人数</th>
                <th>代表者名</th>
                <th>電話番号</th>
                <th>コース</th>
                <th>ステータス</th>
                <th>操作</th>
              </tr>
            </thead>

            <tbody>
              {reservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td>{reservation.id}</td>
                  <td>{formatDateTime(reservation.reservationDate)}</td>
                  <td>{reservation.peopleCount}名</td>
                  <td>{reservation.representativeName}</td>
                  <td>{reservation.phoneNumber}</td>
                  <td>{getCourseName(reservation)}</td>
                  <td>
                    <div className="admin-status-stack">
                      <span className={getStatusBadgeClass(reservation.status)}>
                        {reservation.statusLabel}
                      </span>
                      {getPendingRequestBadge(reservation.pendingRequestType)}
                    </div>
                  </td>
                  <td>
                    <div className="admin-row-actions">
                      <button
                        className="admin-btn admin-btn-primary admin-btn-sm"
                        onClick={() => {
							window.location.href = `http://localhost:5175/admin/reservations/${reservation.id}`;
                        }}
                      >
                        詳細
                      </button>

                      {reservation.pendingRequestType ? (
                        <button
                          className="admin-btn admin-btn-outline admin-btn-sm"
                          onClick={() => {
                            window.location.href =
                              "http://localhost:5175/admin/reservation-requests";
                          }}
                        >
                          申請対応へ
                        </button>
                      ) : (
                        <button
                          className="admin-btn admin-btn-danger-outline admin-btn-sm"
                          onClick={() => handleCancel(reservation)}
                        >
                          キャンセル済みにする
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}

              {reservations.length === 0 && (
                <tr>
                  <td colSpan="8">現在有効な予約はありません。</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="admin-bottom-actions">
          <button
            className="admin-btn admin-btn-outline"
            onClick={() => {
              window.location.href =
                "http://localhost:5175/admin/reservations/history";
            }}
          >
            予約履歴を見る
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

export default AdminReservationsPage;