import { useEffect, useState } from "react";
import api from "../api/api";
import "./AdminReservationHistoryPage.css";

function AdminReservationHistoryPage() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchHistory = async () => {
    try {
      setLoading(true);

      const response = await api.get("/api/admin/reservations/history");
      setReservations(response.data);
    } catch (error) {
      console.error("予約履歴取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
       window.location.href = "http://localhost:5175/admin/home";
      } else {
        alert("予約履歴の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleDelete = async (reservation) => {
    const confirmed = window.confirm(
      `予約ID:${reservation.id} を履歴から削除しますか？`
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(`/api/admin/reservations/history/${reservation.id}`);
      await fetchHistory();
    } catch (error) {
      console.error("予約履歴削除エラー:", error);
      console.error("status:", error.response?.status);
      console.error("data:", error.response?.data);

      const status = error.response?.status;

      if (status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert(`予約履歴の削除に失敗しました。status: ${status}`);
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

  const getStatusClass = (status) => {
    if (status === "CANCELED") {
      return "admin-history-status-badge admin-history-status-canceled";
    }

    if (status === "COMPLETED") {
      return "admin-history-status-badge admin-history-status-completed";
    }

    return "admin-history-status-badge";
  };

  if (loading) {
    return (
      <div className="admin-page">
        <div className="admin-container">
          <h1 className="admin-title">予約履歴</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page admin-history-react-page">
      <div className="admin-container">
        <div className="admin-page-heading-center">
          <p className="admin-page-label">RESERVATION HISTORY</p>
          <h1 className="admin-title">予約履歴</h1>
          <p className="admin-page-description">
            キャンセル済み・完了した過去の予約履歴を確認できます。
          </p>
        </div>

        {reservations.length === 0 && (
          <div className="admin-empty-card">
            現在、予約履歴はありません。
          </div>
        )}

        <div className="admin-history-list">
          {reservations.map((reservation) => (
            <div key={reservation.id} className="admin-history-card-react">
              <div className="admin-history-card-top">
                <div>
                  <span className={getStatusClass(reservation.status)}>
                    {reservation.statusLabel}
                  </span>

                  <h2 className="admin-history-card-title">
                    {getTextOrDefault(reservation.courseName, "コース未設定")}
                  </h2>
                </div>

                <span className="admin-history-date-text">
                  {formatDateTime(reservation.reservationDate)}
                </span>
              </div>

              <div className="admin-history-detail-grid">
                <div className="admin-history-detail-item">
                  <span>ID</span>
                  <strong>{reservation.id}</strong>
                </div>

                <div className="admin-history-detail-item">
                  <span>人数</span>
                  <strong>{reservation.peopleCount}名</strong>
                </div>

                <div className="admin-history-detail-item">
                  <span>代表者名</span>
                  <strong>{reservation.representativeName}</strong>
                </div>

                <div className="admin-history-detail-item">
                  <span>電話番号</span>
                  <strong>{reservation.phoneNumber}</strong>
                </div>

                <div className="admin-history-detail-item">
                  <span>予約作成日</span>
                  <strong>{formatDateTime(reservation.createdAt)}</strong>
                </div>

                {reservation.remarks && (
                  <div className="admin-history-detail-item admin-history-detail-wide">
                    <span>備考</span>
                    <strong>{reservation.remarks}</strong>
                  </div>
                )}
              </div>

              <div className="admin-history-actions-react">
                <button
                  className="admin-btn admin-btn-danger-outline"
                  onClick={() => handleDelete(reservation)}
                >
                  履歴から削除
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

export default AdminReservationHistoryPage;