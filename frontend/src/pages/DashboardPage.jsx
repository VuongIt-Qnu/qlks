import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getRole, getUsername } from "../utils/authStorage";

function DashboardPage() {
  const { t } = useTranslation();
  const name = getUsername();
  const role = getRole();

  let hintKey = "dashboard.hintUser";
  if (role === "OWNER") {
    hintKey = "dashboard.hintOwner";
  } else if (role === "ADMIN") {
    hintKey = "dashboard.hintAdmin";
  }

  return (
    <div className="page">
      <div className="card">
        <h1 style={{ marginTop: 0 }}>{t("dashboard.title")}</h1>
        <p>{t("dashboard.welcome", { name: name || "—" })}</p>
        <p>
          <strong>{t("dashboard.roleLabel")}:</strong> {role || "—"}
        </p>
        <p>{t(hintKey)}</p>
        <div className="dashboard-links">
          {role === "USER" ? (
            <Link to="/bookings">{t("menu.bookingHistory")}</Link>
          ) : null}
          {role === "OWNER" || role === "ADMIN" ? (
            <>
              <Link to="/hotels">{t("menu.hotels")}</Link>
              <Link to="/rooms">{t("menu.rooms")}</Link>
              {role === "OWNER" ? <Link to="/owner/bookings">{t("menu.ownerBookings")}</Link> : null}
            </>
          ) : null}
          {role === "ADMIN" ? <Link to="/admin/users">{t("menu.users")}</Link> : null}
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
