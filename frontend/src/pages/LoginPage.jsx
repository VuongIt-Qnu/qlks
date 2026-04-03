import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import apiClient from "../services/apiClient";
import { setSession } from "../utils/authStorage";

function LoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const { data } = await apiClient.post("/auth/login", { username, password });
      setSession({
        token: data.token,
        username: data.username,
        role: data.role,
        userId: data.userId,
      });
      navigate("/dashboard", { replace: true });
    } catch {
      setError(t("errors.loginFailed"));
    }
  };

  return (
    <div className="page">
      <div className="card" style={{ maxWidth: 420, margin: "0 auto" }}>
        <h1 style={{ marginTop: 0 }}>{t("auth.loginTitle")}</h1>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="login-username">{t("auth.username")}</label>
            <input
              id="login-username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="login-password">{t("auth.password")}</label>
            <input
              id="login-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          {error ? <p className="error-msg">{error}</p> : null}
          <button type="submit" className="btn btn-primary">
            {t("auth.submitLogin")}
          </button>
        </form>
        <p style={{ marginTop: "1rem" }}>
          {t("auth.noAccount")} <Link to="/register">{t("menu.register")}</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
