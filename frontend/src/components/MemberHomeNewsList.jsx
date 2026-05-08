import { useEffect, useState } from "react";
import api from "../api/api";
import "./MemberHomeNewsList.css";

function MemberHomeNewsList() {
  const [newsList, setNewsList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchNews = async () => {
      try {
        const response = await api.get("/api/member/home/news?limit=3");
        setNewsList(response.data || []);
      } catch (error) {
        console.error("新着情報取得エラー:", error);
        setNewsList([]);
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, []);

  const formatDate = (postDate) => {
    if (!postDate) {
      return "";
    }

    const date = new Date(postDate);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}/${month}/${day}`;
  };

  const abbreviate = (text, maxLength = 160) => {
    if (!text) {
      return "本文はありません";
    }

    if (text.length <= maxLength) {
      return text;
    }

    return `${text.slice(0, maxLength)}…`;
  };

  const isRecentNews = (postDate) => {
    if (!postDate) {
      return false;
    }

    const postedDate = new Date(postDate);
    const today = new Date();

    postedDate.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    const diffTime = today.getTime() - postedDate.getTime();
    const diffDays = diffTime / (1000 * 60 * 60 * 24);

    return diffDays >= 0 && diffDays <= 7;
  };

  const hasNewNews = newsList.some((news) => isRecentNews(news.postDate));

  return (
    <section className="member-news-section">
      <div className="member-news-heading">
        <div className="member-news-title-area">
          <div className="member-news-title-row">
            <h2 className="member-news-title">新着情報</h2>

            {!loading && hasNewNews && (
              <span className="member-news-new-badge">NEW</span>
            )}
          </div>

         
        </div>

        <a href="/members/club/announcements" className="member-news-more">
          もっと見る
        </a>
      </div>

      {loading && (
        <div className="member-news-empty">
          新着情報を読み込み中です。
        </div>
      )}

      {!loading && newsList.length === 0 && (
        <div className="member-news-empty">
          新着情報はありません。
        </div>
      )}

      {!loading && newsList.length > 0 && (
        <div className="member-news-list">
          {newsList.map((news) => (
            <article className="member-news-card" key={news.id}>
              <div className="member-news-card-main">
                <h3 className="member-news-card-title">
                  <a href={`/members/club/announcements/${news.id}`}>
                    {news.title}
                  </a>
                </h3>

                <p className="member-news-author">
                  {news.author || "投稿者未設定"}
                </p>

                <p className="member-news-body">
                  {abbreviate(news.article)}
                </p>
              </div>

              <div className="member-news-date">
                {formatDate(news.postDate)}
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

export default MemberHomeNewsList;