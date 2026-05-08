import MemberHomeTopWidget from "../components/MemberHomeTopWidget";
import MemberHomeNewsList from "../components/MemberHomeNewsList";
import "./MemberHomePage.css";

function MemberHomePage() {
  return (
    <main className="member-home-page">
      <MemberHomeTopWidget />
      <MemberHomeNewsList />
    </main>
  );
}

export default MemberHomePage;