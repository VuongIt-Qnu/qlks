import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import apiClient from "../services/apiClient";

function NewBookingPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { state } = useLocation();
  const [error, setError] = useState("");

  if (!state?.roomId || !state?.checkIn || !state?.checkOut) {
    return (
      <div className="page">
        <p>{t("booking.missingContext")}</p>
      </div>
    );
  }

  const submit = async () => {
    setError("");
    try {
      const { data } = await apiClient.post("/user/bookings", {
        roomId: state.roomId,
        checkIn: state.checkIn,
        checkOut: state.checkOut,
        guests: state.guests,
      });
      if (data.paymentId) {
        navigate(`/payments/${data.paymentId}`);
      } else {
        navigate("/bookings");
      }
    } catch {
      setError(t("errors.loadFailed"));
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h1>{t("booking.confirmTitle")}</h1>
        <p>
          {state.hotelName} — {state.roomName}
        </p>
        <p>
          {state.checkIn} → {state.checkOut} ({t("search.guests")}: {state.guests})
        </p>
        {error ? <p className="error-msg">{error}</p> : null}
        <button type="button" className="btn btn-primary" onClick={submit}>
          {t("booking.confirmSubmit")}
        </button>
      </div>
    </div>
  );
}

export default NewBookingPage;
