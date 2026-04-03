import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";
import { setSession } from "../utils/authStorage";

function RegisterPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("USER");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const { data } = await apiClient.post("/auth/register", {
        username,
        password,
        email,
        role,
      });
      setSession({
        token: data.token,
        username: data.username,
        role: data.role,
        userId: data.userId,
      });
      navigate("/dashboard", { replace: true });
    } catch {
      setError(t("errors.registerFailed"));
    }
  };

  return (
    <div className="page">
      <div className="card" style={{ maxWidth: 420, margin: "0 auto" }}>
        <h1 style={{ marginTop: 0 }}>{t("auth.registerTitle")}</h1>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="reg-username">{t("auth.username")}</label>
            <input
              id="reg-username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="reg-email">{t("auth.email")}</label>
            <input
              id="reg-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="reg-password">{t("auth.password")}</label>
            <input
              id="reg-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="reg-role">{t("auth.role")}</label>
            <select id="reg-role" value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="USER">{t("auth.roleUser")}</option>
              <option value="OWNER">{t("auth.roleOwner")}</option>
            </select>
          </div>
          {error ? <p className="error-msg">{error}</p> : null}
          <button type="submit" className="btn btn-primary">
            {t("auth.submitRegister")}
          </button>
        </form>
        <p style={{ marginTop: "1rem" }}>
          {t("auth.alreadyHaveAccount")} <Link to="/login">{t("menu.login")}</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
