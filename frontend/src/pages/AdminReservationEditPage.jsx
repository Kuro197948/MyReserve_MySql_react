import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";

function AdminReservationEditPage() {
  const { id } = useParams();

  const [reservation, setReservation] = useState(null);
  const [form, setForm] = useState({
    representativeName: "",
    reservationDate: "",
    peopleCount: 1,
    phoneNumber: "",
    courseName: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchReservation = async () => {
    try {
      setLoading(true);

      const response = await api.get(`/api/admin/reservations/${id}`);
      const data = response.data;

      setReservation(data);
      setForm({
        representativeName: data.representativeName || "",
        reservationDate: toDatetimeLocalValue(data.reservationDate),
        peopleCount: data.peopleCount || 1,
        phoneNumber: data.phoneNumber || "",
        courseName: data.courseName || "",
      });
    } catch (error) {
      console.error("予約編集データ取得エラー:", error);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else if (error.response?.status === 404) {
        alert("予約情報が見つかりません。");
        window.location.href = "http://localhost:5175/admin/reservations";
      } else {
        alert("予約情報の取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReservation();
  }, [id]);

  const toDatetimeLocalValue = (dateText) => {
    if (!dateText) {
      return "";
    }

    if (Array.isArray(dateText)) {
      const [year, month, day, hour = 0, minute = 0] = dateText;

      return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
        2,
        "0"
      )}T${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
    }

    return String(dateText).slice(0, 16);
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

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: name === "peopleCount" ? Number(value) : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!form.representativeName.trim()) {
      alert("代表者名を入力してください。");
      return;
    }

    if (!form.reservationDate) {
      alert("予約日時を入力してください。");
      return;
    }

    if (!form.peopleCount || form.peopleCount < 1) {
      alert("人数は1名以上で入力してください。");
      return;
    }

    if (!form.phoneNumber.trim()) {
      alert("電話番号を入力してください。");
      return;
    }

    const confirmed = window.confirm("予約情報を更新します。よろしいですか？");

    if (!confirmed) {
      return;
    }

    try {
      setSaving(true);

      await api.put(`/api/admin/reservations/${id}`, {
        representativeName: form.representativeName,
        reservationDate: form.reservationDate,
        peopleCount: form.peopleCount,
        phoneNumber: form.phoneNumber,
        courseName: form.courseName,
      });

      alert("予約情報を更新しました。");
      window.location.href = `http://localhost:5175/admin/reservations/${id}`;
    } catch (error) {
      console.error("予約更新エラー:", error);
      console.error("status:", error.response?.status);
      console.error("data:", error.response?.data);

      if (error.response?.status === 401) {
        alert("管理者ログインが必要です。");
        window.location.href = "http://localhost:8080/admins/adminslogin";
      } else {
        alert("予約情報の更新に失敗しました。");
      }
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="admin-page admin-reservation-edit-page">
        <div className="admin-container">
          <h1 className="admin-title">予約編集</h1>
          <p>読み込み中...</p>
        </div>
      </div>
    );
  }

  if (!reservation) {
    return null;
  }

  return (
    <div className="admin-page admin-reservation-edit-page">
      <div className="admin-container">
        <div className="admin-page-heading-center">
          <p className="admin-page-label">RESERVATION EDIT</p>
          <h1 className="admin-title">予約編集</h1>
          <p className="admin-page-description">
            予約日時、人数、代表者情報、コース名を変更できます。
          </p>
        </div>

        <div className="admin-edit-card">
          <div className="admin-edit-summary">
            <div>
              <span className="admin-status-badge admin-status-reserved">
                {reservation.statusLabel}
              </span>
              <h2 className="admin-edit-title">
                {reservation.courseName && reservation.courseName.trim() !== ""
                  ? reservation.courseName
                  : "コース未設定"}
              </h2>
            </div>

            <span className="admin-edit-date">
              現在の予約日時：{formatDateTime(reservation.reservationDate)}
            </span>
          </div>

          <form onSubmit={handleSubmit} className="admin-edit-form">
            <div className="admin-edit-field">
              <label htmlFor="representativeName">代表者名</label>
              <input
                type="text"
                id="representativeName"
                name="representativeName"
                value={form.representativeName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="admin-edit-field">
              <label htmlFor="reservationDate">予約日時</label>
              <input
                type="datetime-local"
                id="reservationDate"
                name="reservationDate"
                value={form.reservationDate}
                onChange={handleChange}
                required
              />
            </div>

            <div className="admin-edit-field">
              <label htmlFor="peopleCount">人数</label>
              <input
                type="number"
                id="peopleCount"
                name="peopleCount"
                value={form.peopleCount}
                onChange={handleChange}
                min="1"
                required
              />
            </div>

            <div className="admin-edit-field">
              <label htmlFor="phoneNumber">電話番号</label>
              <input
                type="text"
                id="phoneNumber"
                name="phoneNumber"
                value={form.phoneNumber}
                onChange={handleChange}
                required
              />
            </div>

            <div className="admin-edit-field admin-edit-field-wide">
              <label htmlFor="courseName">コース名</label>
              <input
                type="text"
                id="courseName"
                name="courseName"
                value={form.courseName}
                onChange={handleChange}
                placeholder="未入力の場合はコース未設定として扱います"
              />
            </div>

            <div className="admin-edit-actions">
              <button
                type="submit"
                className="admin-btn admin-btn-primary"
                disabled={saving}
              >
                {saving ? "更新中..." : "更新する"}
              </button>

              <button
                type="button"
                className="admin-btn admin-btn-secondary"
                onClick={() => {
                  window.location.href = `http://localhost:5175/admin/reservations/${id}`;
                }}
              >
                戻る
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AdminReservationEditPage;