import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";
import { getRole } from "../utils/authStorage";

function HotelsPage() {
  const { t } = useTranslation();
  const role = getRole();
  const [list, setList] = useState([]);
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [submitError, setSubmitError] = useState("");

  const load = async () => {
    try {
      const { data } = await apiClient.get("/hotels");
      setList(data);
      setError("");
    } catch {
      setError(t("errors.loadFailed"));
    }
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await load();
      if (!cancelled) {
        setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [t]);

  const handleCreate = async (e) => {
    e.preventDefault();
    setSubmitError("");
    try {
      await apiClient.post("/hotels", { name, address });
      setName("");
      setAddress("");
      await load();
    } catch {
      setSubmitError(t("errors.loadFailed"));
    }
  };

  if (loading) {
    return (
      <div className="page">
        <p>{t("common.loading")}</p>
      </div>
    );
  }

  return (
    <div className="page">
      <h1>{t("hotel.listTitle")}</h1>
      {error ? <p className="error-msg">{error}</p> : null}

      {role === "OWNER" ? (
        <div className="card">
          <h2 style={{ marginTop: 0, fontSize: "1.1rem" }}>{t("hotel.createTitle")}</h2>
          <form onSubmit={handleCreate}>
            <div className="form-group">
              <label htmlFor="hotel-name">{t("hotel.name")}</label>
              <input
                id="hotel-name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="hotel-address">{t("hotel.address")}</label>
              <input
                id="hotel-address"
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
              />
            </div>
            {submitError ? <p className="error-msg">{submitError}</p> : null}
            <button type="submit" className="btn btn-primary">
              {t("hotel.createSubmit")}
            </button>
          </form>
        </div>
      ) : null}

      {!list.length && !error ? <p>{t("hotel.empty")}</p> : null}
      {list.length > 0 ? (
        <div className="table-wrap card" style={{ padding: 0 }}>
          <table className="data">
            <thead>
              <tr>
                <th>{t("admin.colId")}</th>
                <th>{t("hotel.name")}</th>
                <th>{t("hotel.address")}</th>
                <th>{t("hotel.ownerId")}</th>
              </tr>
            </thead>
            <tbody>
              {list.map((h) => (
                <tr key={h.id}>
                  <td>{h.id}</td>
                  <td>{h.name}</td>
                  <td>{h.address ?? "—"}</td>
                  <td>{h.ownerId ?? "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </div>
  );
}

export default HotelsPage;
