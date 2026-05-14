import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import client from "../api/client";
import "./AdminHome.css";

const AdminHome = () => {
  const navigate = useNavigate();

  const [summaryCounts, setSummaryCounts] = useState({
    todayReservationCount: 0,
    pendingRequestCount: 0,
    monthlyReservationCount: 0,
    memberCount: 0,
  });

  const [loadingSummary, setLoadingSummary] = useState(true);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const response = await client.get("/admin/home/summary");

        setSummaryCounts({
          todayReservationCount: response.data.todayReservationCount ?? 0,
          pendingRequestCount: response.data.pendingRequestCount ?? 0,
          monthlyReservationCount: response.data.monthlyReservationCount ?? 0,
          memberCount: response.data.memberCount ?? 0,
        });
      } catch (error) {
        if (error.response?.status === 401) {
          navigate("/admins/adminslogin");
          return;
        }

        console.error("管理者ホーム集計取得エラー:", error);
      } finally {
        setLoadingSummary(false);
      }
    };

    fetchSummary();
  }, [navigate]);

  const todayReservationCount = Number(summaryCounts.todayReservationCount ?? 0);
  const requestCount = Number(summaryCounts.pendingRequestCount ?? 0);
  const monthlyReservationCount = Number(summaryCounts.monthlyReservationCount ?? 0);
  const memberCount = Number(summaryCounts.memberCount ?? 0);

  const hasPendingRequest = requestCount > 0;

  const summaryItems = [
    {
      title: "本日の予約",
      value: todayReservationCount,
      unit: "件",
      description: "本日来店予定です。",
      type: "today",
    },
    {
      title: "承認待ち申請",
      value: requestCount,
      unit: "件",
      description: "未対応の申請件数です。",
      type: "request",
    },
    {
      title: "今月の予約",
      value: monthlyReservationCount,
      unit: "件",
      description: "今月の来店予定です。",
      type: "month",
    },
    {
      title: "登録会員数",
      value: memberCount,
      unit: "名",
      description: "登録済み会員数です。",
      type: "member",
    },
  ];

  const menuItems = [
    {
      title: "現在予約",
      description: "予約済み・来店予定の予約を確認します。",
      to: "/admin/reservations",
      buttonText: "現在予約を確認する",
      type: "reservation",
    },
    {
      title: "予約申請",
      description: "キャンセル申請・変更申請を確認し、承認または却下します。",
      to: "/admin/reservation-requests",
      buttonText: "申請を確認する",
      type: "request",
      badge: hasPendingRequest ? requestCount : null,
      badgeText: "申請中",
    },
    {
      title: "予約履歴",
      description: "キャンセル済・完了済の過去予約を確認します。",
      to: "/admin/reservations/history",
      buttonText: "履歴を確認する",
      type: "history",
    },
    {
      title: "お知らせ管理",
      description: "会員向けのお知らせ一覧を確認・管理します。",
      to: "/admin/announcements",
      buttonText: "お知らせを管理する",
      type: "news",
    },
    {
      title: "会員一覧",
      description: "登録されている会員情報を確認します。",
      to: "/admin/members",
      buttonText: "会員を確認する",
      type: "member",
    },
    {
      title: "ログアウト",
      description: "管理者画面での作業を終了し、ログイン画面へ戻ります。",
      to: "/admin/logout",
      buttonText: "ログアウトする",
      type: "logout",
    },
  ];

  const renderMenuCard = (item) => (
    <>
      <div className="admin-menu-card-header">
        <div>
          <p className="admin-menu-subtitle">管理メニュー</p>
          <h2 className="admin-menu-title">{item.title}</h2>
        </div>

        {item.badge && (
          <div className="admin-menu-badge-area">
            <span className="admin-menu-badge">{item.badge}</span>
            <span className="admin-menu-badge-text">
              {item.badgeText}
            </span>
          </div>
        )}
      </div>

      <p className="admin-menu-description">{item.description}</p>

      <span className="admin-menu-button">{item.buttonText}</span>
    </>
  );

  return (
    <div className="admin-home">
      <div className="admin-home-header">
        <p className="admin-home-label">ADMIN DASHBOARD</p>
        <h1 className="admin-home-title">管理者ホーム</h1>
        <p className="admin-home-description">
          予約状況の確認、予約申請の対応、予約履歴の管理、
          お知らせ投稿、会員情報の確認を行えます。
        </p>
      </div>

      <div className="admin-summary-grid">
        {summaryItems.map((item) => (
          <div
            key={item.title}
            className={`admin-summary-card admin-summary-card-${item.type}`}
          >
            <p className="admin-summary-title">{item.title}</p>

            <div className="admin-summary-value-area">
              <span className="admin-summary-value">
                {loadingSummary ? "-" : item.value}
              </span>
              <span className="admin-summary-unit">{item.unit}</span>
            </div>

            <p className="admin-summary-description">{item.description}</p>
          </div>
        ))}
      </div>

      {hasPendingRequest && (
        <div className="admin-home-alert">
          <span className="admin-home-alert-badge">{requestCount}</span>
          件の予約申請があります。予約申請画面から確認してください。
        </div>
      )}

      <div className="admin-menu-grid">
        {menuItems.map((item) => (
          <Link
            key={item.title}
            to={item.to}
            className={`admin-menu-card admin-menu-card-${item.type}`}
          >
            {renderMenuCard(item)}
          </Link>
        ))}
      </div>
    </div>
  );
};

export default AdminHome;