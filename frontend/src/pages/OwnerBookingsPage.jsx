import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";

function OwnerBookingsPage() {
  const { t, i18n } = useTranslation();
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await apiClient.get("/owner/bookings");
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

  const locale = i18n.language === "en" ? "en-US" : "vi-VN";
  const formatDate = (v) => {
    if (v == null) {
      return "—";
    }
    try {
      return new Date(v).toLocaleDateString(locale);
    } catch {
      return String(v);
    }
  };

  const approve = async (id) => {
    try {
      await apiClient.post(`/bookings/${id}/approve`);
      const { data } = await apiClient.get("/owner/bookings");
      setList(data);
    } catch {
      setError(t("errors.loadFailed"));
    }
  };

  const reject = async (id) => {
    try {
      await apiClient.post(`/bookings/${id}/reject`);
      const { data } = await apiClient.get("/owner/bookings");
      setList(data);
    } catch {
      setError(t("errors.loadFailed"));
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
      <h1>{t("owner.bookingsTitle")}</h1>
      {error ? <p className="error-msg">{error}</p> : null}
      {!list.length && !error ? <p>{t("booking.empty")}</p> : null}
      {list.length > 0 ? (
        <div className="table-wrap card" style={{ padding: 0 }}>
          <table className="data">
            <thead>
              <tr>
                <th>{t("booking.bookingId")}</th>
                <th>{t("booking.status")}</th>
                <th>{t("booking.startDate")}</th>
                <th>{t("booking.endDate")}</th>
                <th>{t("common.actions")}</th>
              </tr>
            </thead>
            <tbody>
              {list.map((b) => (
                <tr key={b.id}>
                  <td>{b.id}</td>
                  <td>{b.status ?? "—"}</td>
                  <td>{formatDate(b.startDate)}</td>
                  <td>{formatDate(b.endDate)}</td>
                  <td>
                    {b.status === "PENDING_APPROVAL" ? (
                      <>
                        <button type="button" className="btn btn-sm btn-primary me-1" onClick={() => approve(b.id)}>
                          {t("owner.approve")}
                        </button>
                        <button type="button" className="btn btn-sm btn-outline" onClick={() => reject(b.id)}>
                          {t("owner.reject")}
                        </button>
                      </>
                    ) : (
                      "—"
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </div>
  );
}

export default OwnerBookingsPage;
