import { Outlet } from "react-router-dom";
import Header from "./layout/Header";

function Layout() {
  return (
    <div className="layout">
      <Header />
      <main>
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
