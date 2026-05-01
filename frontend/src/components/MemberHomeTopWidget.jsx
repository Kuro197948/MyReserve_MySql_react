import { useEffect, useState } from "react";
import api from "../api/api";
import "./MemberHomeTopWidget.css";

function MemberHomeTopWidget() {
  const [member, setMember] = useState(null);
  const [nextReservation, setNextReservation] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHomeData = async () => {
      try {
        const meResponse = await api.get("/api/members/me");
        const memberData = meResponse.data;

        if (!memberData.loggedIn) {
          window.location.href = "http://localhost:8080/members/memberslogin";
          return;
        }

        setMember(memberData);

        const reservationResponse = await api.get(
          "/api/member/home/next-reservation"
        );

        setNextReservation(reservationResponse.data);
      } catch (error) {
        console.error("会員ホーム情報取得エラー:", error);
        window.location.href = "http://localhost:8080/members/memberslogin";
      } finally {
        setLoading(false);
      }
    };

    fetchHomeData();
  }, []);

  const formatReservationDate = (reservationDate) => {
    if (!reservationDate) {
      return "日時未設定";
    }

    const date = new Date(reservationDate);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");

    return `${year}/${month}/${day} ${hour}:${minute}`;
  };

  if (loading) {
    return <div className="member-home-loading">読み込み中...</div>;
  }

  if (!member) {
    return null;
  }

  const isPremium = member.memberTypeId === 2;

  return (
    <section className="member-home-top">
      <div className="member-home-panel">
        <div className="member-home-heading">
          <div>
            <h1 className="member-home-title">ホーム</h1>
            <p className="member-home-description">
              予約状況やお知らせを確認できます。
            </p>
          </div>

          <div className={`member-plan-badge ${isPremium ? "premium" : "regular"}`}>
            {isPremium ? "プレミアム会員" : "レギュラー会員"}
          </div>
        </div>

        {nextReservation && (
          <div className="next-reservation-notice">
            <div className="next-reservation-left">
              <div className="notice-label">次回予約</div>

              <div className="notice-main">
                <span className="notice-course">
                  {nextReservation.courseName &&
                  nextReservation.courseName.trim() !== ""
                    ? nextReservation.courseName
                    : "コース未設定"}
                </span>

                <span className="notice-date">
                  {formatReservationDate(nextReservation.reservationDate)}
                </span>
              </div>

              <div className="notice-countdown">
                📅 {nextReservation.daysUntilReservationLabel}
              </div>
            </div>

            <a
              href={`/members/club/reservations/${nextReservation.id}`}
              className="notice-detail-link"
            >
              詳細を見る
            </a>
          </div>
        )}

        <div className="member-home-menu">
          <a href="/members/club/reservation/form" className="home-menu-card primary">
            <span className="home-menu-title">予約する</span>
            <span className="home-menu-text">新しい予約を作成</span>
          </a>

          <a href="/members/club/reservations" className="home-menu-card">
            <span className="home-menu-title">予約確認</span>
            <span className="home-menu-text">現在の予約を確認</span>
          </a>

          {!isPremium ? (
            <a href="/members/club/upgrade" className="home-menu-card">
              <span className="home-menu-title">アップグレード</span>
              <span className="home-menu-text">プレミアム会員になる</span>
            </a>
          ) : (
            <a href="/members/club/downgrade" className="home-menu-card danger">
              <span className="home-menu-title">プレミアム解約</span>
              <span className="home-menu-text">通常会員へ変更</span>
            </a>
          )}

          <a
            href="http://localhost:5175/members/logout"
            className="home-menu-card muted"
          >
            <span className="home-menu-title">ログアウト</span>
            <span className="home-menu-text">会員画面を終了</span>
          </a>
        </div>
      </div>
    </section>
	
  );
}

export default MemberHomeTopWidget;