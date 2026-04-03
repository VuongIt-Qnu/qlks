import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

function NotFoundPage() {
  const { t } = useTranslation();

  return (
    <div className="page">
      <div className="card" style={{ textAlign: "center" }}>
        <h1 style={{ marginTop: 0 }}>{t("notFound.title")}</h1>
        <Link to="/" className="btn btn-primary">
          {t("notFound.backHome")}
        </Link>
      </div>
    </div>
  );
}

export default NotFoundPage;
