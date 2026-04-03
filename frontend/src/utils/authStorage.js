const TOKEN = "token";
const USERNAME = "username";
const ROLE = "role";
const USER_ID = "userId";

export function setSession({ token, username, role, userId }) {
  localStorage.setItem(TOKEN, token);
  localStorage.setItem(USERNAME, username ?? "");
  localStorage.setItem(ROLE, role ?? "");
  if (userId != null) {
    localStorage.setItem(USER_ID, String(userId));
  }
}

export function clearSession() {
  localStorage.removeItem(TOKEN);
  localStorage.removeItem(USERNAME);
  localStorage.removeItem(ROLE);
  localStorage.removeItem(USER_ID);
}

export function getToken() {
  return localStorage.getItem(TOKEN);
}

export function getUsername() {
  return localStorage.getItem(USERNAME) ?? "";
}

export function getRole() {
  return localStorage.getItem(ROLE) ?? "";
}

export function getUserId() {
  return localStorage.getItem(USER_ID);
}

export function isAuthenticated() {
  return Boolean(getToken());
}
