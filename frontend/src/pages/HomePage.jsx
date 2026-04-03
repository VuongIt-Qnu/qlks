import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { isAuthenticated } from "../utils/authStorage";

function HomePage() {
  const { t } = useTranslation();
  const loggedIn = isAuthenticated();

  return (
    <div className="page">
      <div className="hero">
        <h1>{t("home.heroTitle")}</h1>
        <p>{t("home.heroSubtitle")}</p>
        <div className="hero-actions">
          <Link to="/search" className="btn btn-outline">
            {t("menu.search")}
          </Link>
          {loggedIn ? (
            <Link to="/dashboard" className="btn btn-primary">
              {t("menu.dashboard")}
            </Link>
          ) : (
            <>
              <Link to="/login" className="btn btn-primary">
                {t("home.ctaLogin")}
              </Link>
              <Link to="/register" className="btn btn-outline">
                {t("home.ctaRegister")}
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default HomePage;
