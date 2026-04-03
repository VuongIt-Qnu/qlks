import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getRole } from "../utils/authStorage";

function RoleRoute({ roles, children }) {
  const { t } = useTranslation();
  const role = getRole();
  if (!roles.includes(role)) {
    return (
      <div className="page">
        <p className="error-msg">{t("common.forbidden")}</p>
        <p>
          <Link to="/dashboard">{t("menu.dashboard")}</Link>
        </p>
      </div>
    );
  }
  return children;
}

export default RoleRoute;
