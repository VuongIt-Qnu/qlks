import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";

function UserBookingsPage() {
  const { t, i18n } = useTranslation();
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {
    const { data } = await apiClient.get("/user/bookings");
    setList(data);
  };

  const cancelBooking = async (id) => {
    if (!window.confirm(t("booking.cancel"))) {
      return;
    }
    try {
      await apiClient.post(`/user/bookings/${id}/cancel`);
      await load();
    } catch {
      setError(t("errors.loadFailed"));
    }
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await apiClient.get("/user/bookings");
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

  if (loading) {
    return (
      <div className="page">
        <p>{t("common.loading")}</p>
      </div>
    );
  }

  return (
    <div className="page">
      <h1>{t("booking.historyTitle")}</h1>
      {error ? <p className="error-msg">{error}</p> : null}
      {!list.length && !error ? <p>{t("booking.empty")}</p> : null}
      {list.length > 0 ? (
        <div className="table-wrap card" style={{ padding: 0 }}>
          <table className="data">
            <thead>
              <tr>
                <th>{t("booking.bookingId")}</th>
                <th>{t("booking.roomId")}</th>
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
                  <td>{b.roomId ?? "—"}</td>
                  <td>{b.status ?? "—"}</td>
                  <td>{formatDate(b.startDate)}</td>
                  <td>{formatDate(b.endDate)}</td>
                  <td>
                    {b.status && b.status !== "CANCELLED" && b.status !== "COMPLETED" && b.status !== "REJECTED" ? (
                      <button type="button" className="btn btn-sm btn-outline" onClick={() => cancelBooking(b.id)}>
                        {t("booking.cancel")}
                      </button>
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

export default UserBookingsPage;
