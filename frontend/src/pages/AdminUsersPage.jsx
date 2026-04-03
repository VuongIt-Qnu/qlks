import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";

function AdminUsersPage() {
  const { t } = useTranslation();
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await apiClient.get("/admin/users");
        if (!cancelled) {
          setList(data);
        }
      } catch {
        if (!cancelled) {
          setError(t("errors.loadFailed"));
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [t]);

  if (loading) {
    return (
      <div className="page">
        <p>{t("common.loading")}</p>
      </div>
    );
  }

  return (
    <div className="page">
      <h1>{t("admin.usersTitle")}</h1>
      {error ? <p className="error-msg">{error}</p> : null}
      {!list.length && !error ? <p>{t("admin.emptyList")}</p> : null}
      {list.length > 0 ? (
        <div className="table-wrap card" style={{ padding: 0 }}>
          <table className="data">
            <thead>
              <tr>
                <th>{t("admin.colId")}</th>
                <th>{t("admin.colUsername")}</th>
                <th>{t("admin.colEmail")}</th>
                <th>{t("admin.colRole")}</th>
                <th>{t("admin.colActive")}</th>
              </tr>
            </thead>
            <tbody>
              {list.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.username}</td>
                  <td>{u.email}</td>
                  <td>{u.role}</td>
                  <td>{u.active ? t("admin.yes") : t("admin.no")}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </div>
  );
}

export default AdminUsersPage;
