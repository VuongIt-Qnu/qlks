import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { isAuthenticated, clearSession, getUsername } from "../../utils/authStorage";

function Header() {
  const { t, i18n } = useTranslation();
  const loggedIn = isAuthenticated();
  const username = getUsername();

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
    localStorage.setItem("lang", lng);
  };

  const handleLogout = () => {
    clearSession();
    window.location.href = "/";
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light px-3">
      <Link to="/" className="navbar-brand">
        {t("appTitle")}
      </Link>
      <div className="navbar-nav">
        <Link to="/">{t("menu.home")}</Link>
        <Link to="/search">{t("menu.search")}</Link>
        {loggedIn ? (
          <>
            <Link to="/dashboard">{t("menu.dashboard")}</Link>
            {username ? <span className="navbar-user">{username}</span> : null}
            <button type="button" className="btn btn-sm btn-outline" onClick={handleLogout}>
              {t("menu.logout")}
            </button>
          </>
        ) : (
          <>
            <Link to="/login">{t("menu.login")}</Link>
            <Link to="/register">{t("menu.register")}</Link>
          </>
        )}
      </div>
      <div className="navbar-spacer" />
      <div className="ms-auto d-flex align-items-center">
        <span className="me-2">{t("language")}:</span>
        <button
          type="button"
          className="btn btn-sm btn-outline-primary me-1"
          onClick={() => changeLanguage("vi")}
        >
          VI
        </button>
        <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => changeLanguage("en")}>
          EN
        </button>
      </div>
    </nav>
  );
}

export default Header;
