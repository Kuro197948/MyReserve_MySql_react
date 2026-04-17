import React from "react";
import ReactDOM from "react-dom/client";
import MemberHomeTopWidget from "./components/MemberHomeTopWidget";

const rootElement = document.getElementById("member-home-top-widget");

if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <MemberHomeTopWidget />
    </React.StrictMode>
  );
}