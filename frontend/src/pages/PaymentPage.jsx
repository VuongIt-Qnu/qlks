import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";

function PaymentPage() {
  const { id } = useParams();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [payment, setPayment] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let c = false;
    (async () => {
      try {
        const { data } = await apiClient.get(`/payments/${id}`);
        if (!c) setPayment(data);
      } catch {
        if (!c) setError(t("errors.loadFailed"));
      } finally {
        if (!c) setLoading(false);
      }
    })();
    return () => {
      c = true;
    };
  }, [id, t]);

  const confirm = async (success) => {
    setError("");
    try {
      await apiClient.post(`/payments/${id}/confirm`, { success });
      navigate("/bookings");
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
      <div className="card">
        <h1>{t("payment.title")}</h1>
        {error ? <p className="error-msg">{error}</p> : null}
        {payment ? (
          <>
            <p>
              {t("payment.amount")}: {payment.amount} — {t("payment.status")}: {payment.status}
            </p>
            <p>{t("payment.mockHint")}</p>
            <button type="button" className="btn btn-primary me-1" onClick={() => confirm(true)}>
              {t("payment.success")}
            </button>
            <button type="button" className="btn btn-outline-secondary" onClick={() => confirm(false)}>
              {t("payment.fail")}
            </button>
          </>
        ) : null}
      </div>
    </div>
  );
}

export default PaymentPage;
