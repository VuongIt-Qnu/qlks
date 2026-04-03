import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";
import { isAuthenticated } from "../utils/authStorage";

function SearchPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [guests, setGuests] = useState(1);
  const [hotelId, setHotelId] = useState("");
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const search = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const params = { checkIn, checkOut, guests };
      if (hotelId) params.hotelId = hotelId;
      const { data } = await apiClient.get("/public/rooms/available", { params });
      setRooms(data);
    } catch {
      setError(t("errors.loadFailed"));
    } finally {
      setLoading(false);
    }
  };

  const book = (room) => {
    if (!isAuthenticated()) {
      navigate("/login");
      return;
    }
    navigate("/booking/new", {
      state: {
        roomId: room.id,
        checkIn,
        checkOut,
        guests,
        pricePerNight: room.pricePerNight,
        hotelName: room.hotelName,
        roomName: room.name,
      },
    });
  };

  return (
    <div className="page">
      <h1>{t("search.title")}</h1>
      <form onSubmit={search} className="card">
        <div className="form-group">
          <label>{t("search.checkIn")}</label>
          <input type="date" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>{t("search.checkOut")}</label>
          <input type="date" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>{t("search.guests")}</label>
          <input
            type="number"
            min={1}
            value={guests}
            onChange={(e) => setGuests(Number(e.target.value))}
            required
          />
        </div>
        <div className="form-group">
          <label>{t("search.hotelIdOptional")}</label>
          <input type="number" min={1} value={hotelId} onChange={(e) => setHotelId(e.target.value)} />
        </div>
        {error ? <p className="error-msg">{error}</p> : null}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {t("search.submit")}
        </button>
      </form>

      {rooms.length > 0 ? (
        <div className="card" style={{ marginTop: "1rem" }}>
          <h2>{t("search.results")}</h2>
          <ul style={{ listStyle: "none", padding: 0 }}>
            {rooms.map((r) => (
              <li key={r.id} style={{ borderBottom: "1px solid #eee", padding: "0.75rem 0" }}>
                <strong>{r.hotelName}</strong> — {r.name} ({r.roomTypeName}) — {t("search.price")}:{" "}
                {r.pricePerNight}
                <button type="button" className="btn btn-sm btn-primary" style={{ marginLeft: "0.5rem" }} onClick={() => book(r)}>
                  {t("search.book")}
                </button>
              </li>
            ))}
          </ul>
        </div>
      ) : null}
    </div>
  );
}

export default SearchPage;
