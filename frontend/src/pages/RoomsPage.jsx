import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";

function RoomsPage() {
  const { t } = useTranslation();
  const [list, setList] = useState([]);
  const [hotelId, setHotelId] = useState("");
  const [roomTypeId, setRoomTypeId] = useState("");
  const [name, setName] = useState("");
  const [price, setPrice] = useState("");
  const [maxGuests, setMaxGuests] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [submitError, setSubmitError] = useState("");

  const load = async () => {
    try {
      const { data } = await apiClient.get("/rooms");
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
    const payload = {
      hotelId: Number(hotelId),
      roomTypeId: Number(roomTypeId),
      name,
      price: Number(price),
    };
    if (maxGuests) {
      payload.maxGuests = Number(maxGuests);
    }
    try {
      await apiClient.post("/rooms", payload);
      setHotelId("");
      setRoomTypeId("");
      setName("");
      setPrice("");
      setMaxGuests("");
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
      <h1>{t("room.listTitle")}</h1>
      {error ? <p className="error-msg">{error}</p> : null}

      <div className="card">
        <h2 style={{ marginTop: 0, fontSize: "1.1rem" }}>{t("room.createTitle")}</h2>
        <form onSubmit={handleCreate}>
          <div className="form-group">
            <label htmlFor="room-hotel">{t("room.hotelId")}</label>
            <input
              id="room-hotel"
              type="number"
              min="1"
              value={hotelId}
              onChange={(e) => setHotelId(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="room-type">{t("room.roomTypeId")}</label>
            <input
              id="room-type"
              type="number"
              min="1"
              value={roomTypeId}
              onChange={(e) => setRoomTypeId(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="room-name">{t("room.name")}</label>
            <input id="room-name" type="text" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="form-group">
            <label htmlFor="room-price">{t("room.price")}</label>
            <input
              id="room-price"
              type="number"
              step="0.01"
              min="0"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="room-max">{t("room.maxGuests")}</label>
            <input
              id="room-max"
              type="number"
              min="1"
              value={maxGuests}
              onChange={(e) => setMaxGuests(e.target.value)}
            />
          </div>
          {submitError ? <p className="error-msg">{submitError}</p> : null}
          <button type="submit" className="btn btn-primary">
            {t("room.createSubmit")}
          </button>
        </form>
      </div>

      {!list.length && !error ? <p>{t("room.empty")}</p> : null}
      {list.length > 0 ? (
        <div className="table-wrap card" style={{ padding: 0 }}>
          <table className="data">
            <thead>
              <tr>
                <th>{t("admin.colId")}</th>
                <th>{t("room.hotelId")}</th>
                <th>{t("room.roomTypeId")}</th>
                <th>{t("room.name")}</th>
                <th>{t("room.type")}</th>
                <th>{t("room.price")}</th>
              </tr>
            </thead>
            <tbody>
              {list.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.hotelId ?? "—"}</td>
                  <td>{r.roomTypeId ?? "—"}</td>
                  <td>{r.name}</td>
                  <td>{r.type ?? "—"}</td>
                  <td>{r.price != null ? r.price : "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </div>
  );
}

export default RoomsPage;
