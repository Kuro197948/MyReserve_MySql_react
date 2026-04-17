import { useEffect, useState } from "react";
import api from "../api/api";

function MemberHomeTopWidget() {
  const [member, setMember] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const response = await api.get("/api/members/me");
        const data = response.data;

        if (!data.loggedIn) {
          window.location.href = "http://localhost:10000/members/memberslogin";
          return;
        }

        setMember(data);
      } catch (error) {
        console.error("会員情報取得エラー:", error);
        window.location.href = "http://localhost:10000/members/memberslogin";
      } finally {
        setLoading(false);
      }
    };

    fetchMe();
  }, []);

  if (loading) {
    return <h1 className="mb-3 text-center">読み込み中...</h1>;
  }

  if (!member) {
    return null;
  }

  const isPremium = member.memberTypeId === 2;

  return (
    <>
      <h1 className="mb-3 text-center">ホーム</h1>

      <div className="text-center mb-3">
        {isPremium ? (
          <div className="alert alert-warning d-inline-block px-4 py-2">
            あなたは <strong>プレミアム会員</strong> です
          </div>
        ) : (
          <div className="alert alert-secondary d-inline-block px-4 py-2">
            あなたは <strong>レギュラー会員</strong> です
          </div>
        )}
      </div>

      <div
        className="d-flex justify-content-center mb-4 flex-wrap"
        style={{ gap: "1rem" }}
      >
        <a href="/members/club/announcements" className="btn btn-primary">
          新着情報一覧
        </a>

        <a href="/members/club/reservation/form" className="btn btn-success">
          予約する
        </a>

        {!isPremium ? (
          <a href="/members/club/upgrade" className="btn btn-secondary">
            アップグレード
          </a>
        ) : (
          <a href="/members/club/downgrade" className="btn btn-danger">
            解約する
          </a>
        )}

        <a
          href="http://localhost:5175/members/logout"
          className="btn btn-secondary"
        >
          ログアウト
        </a>
      </div>
    </>
  );
}

export default MemberHomeTopWidget;