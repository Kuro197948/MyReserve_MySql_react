import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";

function AdminReservationDetailPage() {
  const { id } = useParams();

  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchReservation = async () => {
    try {
      setLoading(true);

      const response = await api.get(`/api/admin/reservations/${id}`);
      setReservation(response.data);
    } catch (error) {
      console.error("予約詳細取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else if (error.response?.status === 404) {
        alert("予約情報が見つかりません。");
        window.location.href = "http://localhost:5175/admin/reservations";
      } else {
        alert("予約詳細の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservation();
  }, [id]);

  const handleCancel = async () => {
    const confirmed = window.confirm(
      "この予約をキャンセル済みにして、予約履歴へ移動しますか？"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.post(`/api/admin/reservations/${reservation.id}/cancel`);
      await fetchReservation();
      alert("予約をキャンセル済みにしました。");
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

  const getTextOrDefault = (value, defaultText) => {
    if (value === null || value === undefined || String(value).trim() === "") {
      return defaultText;
    }

    return value;
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

  if (loading) {
    return (
      <div className="admin-page admin-reservation-detail-page">
        <div className="admin-container">
          <h1 className="admin-title">予約詳細</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  if (!reservation) {
    return null;
  }

  return (
    <div className="admin-page admin-reservation-detail-page">
      <div className="admin-container">
        <div className="admin-page-heading-center">
          <p className="admin-page-label">RESERVATION DETAIL</p>
          <h1 className="admin-title">予約詳細</h1>
          <p className="admin-page-description">
            予約内容の確認、編集、キャンセル処理を行えます。
          </p>
        </div>

        {reservation.hasPendingCancelRequest && (
          <div className="admin-detail-alert admin-detail-alert-warning">
            この予約はキャンセル申請中です。申請内容を確認して承認・却下を行ってください。
          </div>
        )}

        {reservation.hasPendingChangeRequest && (
          <div className="admin-detail-alert admin-detail-alert-warning">
            この予約は変更申請中です。申請内容を確認して承認・却下を行ってください。
          </div>
        )}

        <div className="admin-detail-card">
          <div className="admin-detail-card-header">
            <div>
              <div className="admin-detail-badge-row">
                <span className={getStatusBadgeClass(reservation.status)}>
                  {reservation.statusLabel}
                </span>

                {reservation.hasPendingCancelRequest && (
                  <span className="admin-request-mini-badge admin-request-mini-cancel">
                    キャンセル申請中
                  </span>
                )}

                {reservation.hasPendingChangeRequest && (
                  <span className="admin-request-mini-badge admin-request-mini-change">
                    変更申請中
                  </span>
                )}
              </div>

              <h2 className="admin-detail-card-title">
                {getTextOrDefault(reservation.courseName, "コース未設定")}
              </h2>
            </div>

            <span className="admin-detail-date">
              {formatDateTime(reservation.reservationDate)}
            </span>
          </div>

          <div className="admin-detail-sections">
            <section className="admin-detail-section">
              <h3>予約概要</h3>

              <div className="admin-detail-grid">
                <div className="admin-detail-item">
                  <span>予約日時</span>
                  <strong>{formatDateTime(reservation.reservationDate)}</strong>
                </div>

                <div className="admin-detail-item">
                  <span>コース名</span>
                  <strong>
                    {getTextOrDefault(reservation.courseName, "コース未設定")}
                  </strong>
                </div>

                <div className="admin-detail-item">
                  <span>ステータス</span>
                  <strong>{reservation.statusLabel}</strong>
                </div>
              </div>
            </section>

            <section className="admin-detail-section">
              <h3>予約者情報</h3>

              <div className="admin-detail-grid">
                <div className="admin-detail-item">
                  <span>代表者名</span>
                  <strong>{reservation.representativeName}</strong>
                </div>

                <div className="admin-detail-item">
                  <span>人数</span>
                  <strong>{reservation.peopleCount}名</strong>
                </div>

                <div className="admin-detail-item">
                  <span>電話番号</span>
                  <strong>{reservation.phoneNumber}</strong>
                </div>
              </div>
            </section>

            <section className="admin-detail-section">
              <h3>管理情報</h3>

              <div className="admin-detail-grid">
                <div className="admin-detail-item">
                  <span>ID</span>
                  <strong>{reservation.id}</strong>
                </div>

                <div className="admin-detail-item">
                  <span>作成日時</span>
                  <strong>{formatDateTime(reservation.createdAt)}</strong>
                </div>

                <div className="admin-detail-item admin-detail-wide">
                  <span>備考</span>
                  <strong>{getTextOrDefault(reservation.remarks, "なし")}</strong>
                </div>
              </div>
            </section>
          </div>

          <div className="admin-detail-actions">
            <button
              className="admin-btn admin-btn-outline"
              onClick={() => {
                window.location.href = "http://localhost:5175/admin/reservations";
              }}
            >
              現在予約へ戻る
            </button>

            <button
              className="admin-btn admin-btn-outline"
              onClick={() => {
                window.location.href =
                  "http://localhost:5175/admin/reservations/history";
              }}
            >
              予約履歴へ戻る
            </button>

            {reservation.isCancelableReservation &&
              !reservation.hasPendingRequest && (
                <>
                  <button
                    className="admin-btn admin-btn-outline"
                    onClick={() => {
                      window.location.href = `http://localhost:5175/admin/reservations/${reservation.id}/edit`;
                    }}
                  >
                    予約情報を編集
                  </button>

                  <button
                    className="admin-btn admin-btn-danger-outline"
                    onClick={handleCancel}
                  >
                    キャンセル済みにする
                  </button>
                </>
              )}

            {reservation.hasPendingRequest && (
              <button
                className="admin-btn admin-btn-outline"
                onClick={() => {
                  window.location.href =
                    "http://localhost:5175/admin/reservation-requests";
                }}
              >
                申請対応へ
              </button>
            )}

            {reservation.hasPendingCancelRequest && (
              <button className="admin-btn admin-btn-danger-outline" disabled>
                キャンセル申請中
              </button>
            )}

            {reservation.hasPendingChangeRequest && (
              <button className="admin-btn admin-btn-outline" disabled>
                変更申請中
              </button>
            )}

            {reservation.canceled && (
              <button className="admin-btn admin-btn-secondary" disabled>
                キャンセル済み
              </button>
            )}

            {reservation.completed && (
              <button className="admin-btn admin-btn-secondary" disabled>
                完了済み
              </button>
            )}

            {reservation.reserved && !reservation.isCancelableReservation && (
              <button className="admin-btn admin-btn-secondary" disabled>
                予約日を過ぎています
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default AdminReservationDetailPage;