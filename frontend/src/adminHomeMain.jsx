import React from "react";
import ReactDOM from "react-dom/client";
import AdminHome from "./components/AdminHome.jsx";

const rootElement = document.getElementById("admin-home-root");

if (rootElement) {
  const summaryCounts = {
    todayReservationCount: Number(
      rootElement.dataset.todayReservationCount || 0
    ),
    pendingRequestCount: Number(
      rootElement.dataset.pendingRequestCount || 0
    ),
    monthlyReservationCount: Number(
      rootElement.dataset.monthlyReservationCount || 0
    ),
    memberCount: Number(
      rootElement.dataset.memberCount || 0
    ),
  };

  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <AdminHome
        summaryCounts={summaryCounts}
        pendingRequestCount={summaryCounts.pendingRequestCount}
      />
    </React.StrictMode>
  );
}