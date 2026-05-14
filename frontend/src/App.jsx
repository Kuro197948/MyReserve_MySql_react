import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
  useParams,
} from "react-router-dom";

import MemberLoginPage from "./pages/MemberLoginPage";
import MemberLogoutPage from "./pages/MemberLogoutPage";

import AdminLogoutPage from "./pages/AdminLogoutPage";
import AdminHome from "./components/AdminHome";

import AdminAnnouncementsPage from "./pages/AdminAnnouncementsPage";
import AdminMembersPage from "./pages/AdminMembersPage";
import AdminMemberCreatePage from "./pages/AdminMemberCreatePage";
import AdminMemberEditPage from "./pages/AdminMemberEditPage";

import AdminReservationsPage from "./pages/AdminReservationsPage";
import AdminReservationRequestsPage from "./pages/AdminReservationRequestsPage";
import AdminReservationHistoryPage from "./pages/AdminReservationHistoryPage";
import AdminReservationDetailPage from "./pages/AdminReservationDetailPage";
import AdminReservationEditPage from "./pages/AdminReservationEditPage";

import AdminAnnouncementDetailPage from "./pages/AdminAnnouncementDetailPage";
import AdminAnnouncementCreatePage from "./pages/AdminAnnouncementCreatePage";
import AdminAnnouncementListPage from "./pages/AdminAnnouncementListPage";
import { useEffect } from "react";
import AdminAnnouncementEditPage from "./pages/AdminAnnouncementEditPage";

function OldAnnouncementDetailRedirect() {
  const { id } = useParams();

  return <Navigate to={`/admin/announcements/${id}`} replace />;
}

function OldReservationDetailRedirect() {
  const { id } = useParams();

  return <Navigate to={`/admin/reservations/${id}`} replace />;
}

function OldMemberEditRedirect() {
  const { id } = useParams();

  return <Navigate to={`/admin/members/${id}/edit`} replace />;
}

function ExternalRedirect({ to }) {
  useEffect(() => {
    window.location.replace(to);
  }, [to]);

  return null;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
	  
	  <Route
	    path="/admins/club/announcements/:id"
	    element={<AdminAnnouncementDetailPage />}
	  />
	  <Route
	    path="/admins/club/announcements/edit/:id"
	    element={<AdminAnnouncementEditPage />}
	  />
	  
	  <Route
	    path="/admins/adminslogin"
	    element={<ExternalRedirect to="http://localhost:8080/admins/adminslogin" />}
	  />

	  <Route
	    path="/loginhome"
	    element={<ExternalRedirect to="http://localhost:8080/loginhome" />}
	  />

	  <Route
	    path="/loginHome"
	    element={<ExternalRedirect to="http://localhost:8080/loginHome" />}
	  />
        <Route
          path="/"
          element={<Navigate to="/members/memberslogin" replace />}
        />

        <Route
          path="/members/memberslogin"
          element={<MemberLoginPage />}
        />
        <Route
          path="/members/logout"
          element={<MemberLogoutPage />}
        />

        <Route
          path="/admin/home"
          element={<AdminHome />}
        />
        <Route
          path="/admin/logout"
          element={<AdminLogoutPage />}
        />

        <Route
          path="/admin/reservations"
          element={<AdminReservationsPage />}
        />
        <Route
          path="/admin/reservations/history"
          element={<AdminReservationHistoryPage />}
        />
        <Route
          path="/admin/reservations/:id"
          element={<AdminReservationDetailPage />}
        />
        <Route
          path="/admin/reservations/:id/edit"
          element={<AdminReservationEditPage />}
        />
        <Route
          path="/admin/reservation-requests"
          element={<AdminReservationRequestsPage />}
        />

		<Route
		  path="/admin/announcements"
		  element={<AdminAnnouncementListPage />}
		/>
        <Route
          path="/admin/announcements/new"
          element={<AdminAnnouncementCreatePage />}
        />
        <Route
          path="/admin/announcements/:id"
          element={<AdminAnnouncementDetailPage />}
        />

        <Route
          path="/admin/members"
          element={<AdminMembersPage />}
        />
        <Route
          path="/admin/members/new"
          element={<AdminMemberCreatePage />}
        />
        <Route
          path="/admin/members/:id/edit"
          element={<AdminMemberEditPage />}
        />

        {/* 旧URLからReact正規URLへのリダイレクト */}
        <Route
          path="/admins/club/home"
          element={<Navigate to="/admin/home" replace />}
        />

        <Route
          path="/admins/club/reservations"
          element={<Navigate to="/admin/reservations" replace />}
        />
        <Route
          path="/admins/club/reservations/history"
          element={<Navigate to="/admin/reservations/history" replace />}
        />
        <Route
          path="/admins/club/reservations/:id"
          element={<OldReservationDetailRedirect />}
        />
        <Route
          path="/admins/club/reservation-requests"
          element={<Navigate to="/admin/reservation-requests" replace />}
        />

        <Route
          path="/admins/club/announcements"
          element={<Navigate to="/admin/announcements" replace />}
        />
        <Route
          path="/admins/club/announcements/new"
          element={<Navigate to="/admin/announcements/new" replace />}
        />
        <Route
          path="/admins/club/save"
          element={<Navigate to="/admin/announcements/new" replace />}
        />
        <Route
          path="/admins/club/detail/:id"
          element={<OldAnnouncementDetailRedirect />}
        />

        <Route
          path="/admins/club/members"
          element={<Navigate to="/admin/members" replace />}
        />
        <Route
          path="/admins/club/memberslist"
          element={<Navigate to="/admin/members" replace />}
        />
        <Route
          path="/admins/club/members/add"
          element={<Navigate to="/admin/members/new" replace />}
        />
        <Route
          path="/admins/club/edit/:id"
          element={<OldMemberEditRedirect />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;