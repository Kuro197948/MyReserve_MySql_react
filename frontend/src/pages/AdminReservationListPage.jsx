import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import client from "../api/client";
import "./AdminReservationListPage.css";

function formatDateTime(value) {
  if (!value) return "";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hour = String(date.getHours()).padStart(2, "0");
  const minute = String(date.getMinutes()).padStart(2, "0");

  return `${year}/${month}/${day} ${hour}:${minute}`;
}

function AdminReservationListPage() {
  const navigate = useNavigate();

  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const fetchReservations = async () => {
    try {
      setLoading(true);
      setErrorMessage("");

      const response = await client.get("/admin/reservations");
      setReservations(response.data);
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("現在予約の取得中にエラーが発生しました。");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  const handleCancel = async (reservationId) => {
    const confirmed = window.confirm(
      "この予約をキャンセル済みにして、予約履歴へ移動しますか？"
    );

    if (!confirmed) {
      return;
    }

    try {
      setSuccessMessage("");
      setErrorMessage("");

      await client.post(`/admin/reservations/${reservationId}/cancel`);

      setReservations((prevReservations) =>
        prevReservations.filter((reservation) => reservation.id !== reservationId)
      );

      setSuccessMessage("予約をキャンセル済みにして、予約履歴へ移動しました。");
    } catch (error) {
      if (error.response?.status === 401) {
        navigate("/admins/adminslogin");
        return;
      }

      setErrorMessage("予約のキャンセル処理中にエラーが発生しました。");
    }
  };

  if (loading) {
    return (
      <main className="admin-reservation-list-page">
        <div className="admin-reservation-list-card">
          <p className="admin-reservation-loading">読み込み中...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-reservation-list-page">
      <div className="admin-reservation-list-card">
        <div className="admin-reservation-header">
          <div>
            <p className="admin-reservation-label">RESERVATION MANAGEMENT</p>
            <h1 className="admin-reservation-title">現在予約</h1>
            <p className="admin-reservation-description">
              予約済み・来店予定の予約を確認できます。キャンセル済み・完了済みの予約は予約履歴から確認してください。
            </p>
          </div>
        </div>

        {successMessage && (
          <div className="admin-reservation-alert success">
            {successMessage}
          </div>
        )}

        {errorMessage && (
          <div className="admin-reservation-alert error">
            {errorMessage}
          </div>
        )}

        {reservations.length === 0 ? (
          <div className="admin-reservation-empty">
            現在、予約はありません。
          </div>
        ) : (
          <div className="admin-reservation-panel">
            <div className="admin-reservation-section-head">
              <div>
                <h2 className="admin-reservation-section-title">予約一覧</h2>
                <p className="admin-reservation-section-description">
                  現在有効な予約のみ表示しています。
                </p>
              </div>

              <Link
                to="/admin/reservations/history"
                className="admin-reservation-history-top-button"
              >
                予約履歴を見る
              </Link>
            </div>

            <div className="admin-reservation-table-wrap">
              <table className="admin-reservation-table">
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
                      <td className="admin-reservation-id">
                        {reservation.id}
                      </td>

                      <td>{formatDateTime(reservation.reservationDate)}</td>

                      <td>{reservation.peopleCount}名</td>

                      <td>{reservation.representativeName}</td>

                      <td>{reservation.phoneNumber}</td>

                      <td>
                        {reservation.courseName &&
                        reservation.courseName.trim() !== ""
                          ? reservation.courseName
                          : "コース未設定"}
                      </td>

                      <td>
                        <div className="admin-reservation-status-area">
                          <span
                            className={`admin-reservation-status-badge ${
                              reservation.reserved ? "reserved" : "default"
                            }`}
                          >
                            {reservation.statusLabel}
                          </span>

                          {reservation.pendingRequestType === "CANCEL" && (
                            <span className="admin-reservation-request-badge cancel">
                              キャンセル申請中
                            </span>
                          )}

                          {reservation.pendingRequestType === "CHANGE" && (
                            <span className="admin-reservation-request-badge change">
                              変更申請中
                            </span>
                          )}
                        </div>
                      </td>

                      <td className="admin-reservation-operation-cell">
                        <div className="admin-reservation-operation-stack">
                          <Link
                            to={`/admin/reservations/${reservation.id}`}
                            className="admin-reservation-operation-button detail"
                          >
                            詳細
                          </Link>

                          {!reservation.hasPendingRequest && (
                            <button
                              type="button"
                              className="admin-reservation-operation-button cancel"
                              onClick={() => handleCancel(reservation.id)}
                            >
                              キャンセル済み
                            </button>
                          )}

                          {reservation.pendingRequestType === "CANCEL" && (
                            <Link
                              to="/admin/reservation-requests"
                              className="admin-reservation-operation-button request-cancel"
                            >
                              申請対応へ
                            </Link>
                          )}

                          {reservation.pendingRequestType === "CHANGE" && (
                            <Link
                              to="/admin/reservation-requests"
                              className="admin-reservation-operation-button request-change"
                            >
                              申請対応へ
                            </Link>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        <div className="admin-reservation-bottom-actions">
          <Link
            to="/admin/reservations/history"
            className="admin-reservation-button secondary"
          >
            予約履歴を見る
          </Link>

          <Link
            to="/admin/home"
            className="admin-reservation-button gray"
          >
            管理ホームへ戻る
          </Link>
        </div>
      </div>
    </main>
  );
}

export default AdminReservationListPage;