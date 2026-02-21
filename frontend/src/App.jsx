import { useEffect, useState } from "react";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8082";

const emptyRegister = { name: "", email: "", password: "" };
const emptyLogin = { email: "", password: "" };

export default function App() {
  const [registerForm, setRegisterForm] = useState(emptyRegister);
  const [loginForm, setLoginForm] = useState(emptyLogin);
  const [profile, setProfile] = useState(null);
  const [status, setStatus] = useState("Ready");
  const [error, setError] = useState("");
  const [users, setUsers] = useState([]);
  const [roleDrafts, setRoleDrafts] = useState({});
  const [adminStatus, setAdminStatus] = useState("");
  const [auditLogs, setAuditLogs] = useState([]);

  const authFetch = async (path, options = {}) => {
    const token = localStorage.getItem("token");
    const headers = {
      "Content-Type": "application/json",
      ...(options.headers || {})
    };
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
    return fetch(`${API_BASE}${path}`, { ...options, headers });
  };

  const handleOAuthRedirect = () => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    if (token) {
      localStorage.setItem("token", token);
      window.history.replaceState({}, "", "/");
    }
  };

  const loadProfile = async () => {
    setStatus("Loading profile...");
    setError("");
    try {
      const res = await authFetch("/api/auth/me");
      if (!res.ok) {
        setProfile(null);
        setStatus("Not authenticated");
        return;
      }
      const data = await res.json();
      setProfile(data);
      setStatus("Authenticated");
      if (data?.roles?.includes("ROLE_ADMIN")) {
        await loadUsers();
      }
    } catch (err) {
      setError("Failed to load profile");
      setStatus("Error");
    }
  };

  const loadUsers = async () => {
    setAdminStatus("Loading users...");
    try {
      const res = await authFetch("/api/auth/admin/users");
      if (!res.ok) {
        setAdminStatus("Admin access required");
        return;
      }
      const data = await res.json();
      setUsers(data);
      const drafts = {};
      data.forEach((user) => {
        drafts[user.id] = (user.roles || []).join(", ");
      });
      setRoleDrafts(drafts);
      await loadAuditLogs();
      setAdminStatus("");
    } catch (err) {
      setAdminStatus("Failed to load users");
    }
  };

  const loadAuditLogs = async () => {
    try {
      const res = await authFetch("/api/auth/admin/audit");
      if (!res.ok) {
        setAuditLogs([]);
        return;
      }
      const data = await res.json();
      setAuditLogs(data.slice(0, 20));
    } catch (err) {
      setAuditLogs([]);
    }
  };

  useEffect(() => {
    handleOAuthRedirect();
    loadProfile();
  }, []);

  const handleRegister = async (event) => {
    event.preventDefault();
    setStatus("Registering...");
    setError("");
    try {
      const res = await authFetch("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(registerForm)
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Registration failed");
      }
      const data = await res.json();
      localStorage.setItem("token", data.token);
      setRegisterForm(emptyRegister);
      await loadProfile();
    } catch (err) {
      setError(err.message || "Registration failed");
      setStatus("Error");
    }
  };

  const handleLogin = async (event) => {
    event.preventDefault();
    setStatus("Logging in...");
    setError("");
    try {
      const res = await authFetch("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(loginForm)
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Login failed");
      }
      const data = await res.json();
      localStorage.setItem("token", data.token);
      setLoginForm(emptyLogin);
      await loadProfile();
    } catch (err) {
      setError(err.message || "Login failed");
      setStatus("Error");
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = `${API_BASE}/oauth2/authorization/google`;
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setProfile(null);
    setUsers([]);
    setRoleDrafts({});
    setStatus("Logged out");
  };

  const updateRoles = async (userId) => {
    const draft = roleDrafts[userId] || "";
    const roles = draft
      .split(",")
      .map((role) => role.trim())
      .filter((role) => role.length > 0);
    setAdminStatus("Saving roles...");
    try {
      const res = await authFetch(`/api/auth/admin/users/${userId}/roles`, {
        method: "PUT",
        body: JSON.stringify({ roles })
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to update roles");
      }
      const updated = await res.json();
      setUsers((prev) => prev.map((user) => (user.id === userId ? updated : user)));
      await loadAuditLogs();
      setAdminStatus("Roles updated");
      setTimeout(() => setAdminStatus(""), 2000);
    } catch (err) {
      setAdminStatus(err.message || "Failed to update roles");
    }
  };

  const isAdmin = profile?.roles?.includes("ROLE_ADMIN");

  return (
    <div className="page">
      <div className="card">
        <header>
          <h1>Ecommerce Auth</h1>
          <p>JWT + Google OAuth2 + Eureka gateway flow</p>
        </header>

        <div className="status">
          <span>{status}</span>
          {error && <span className="error">{error}</span>}
        </div>

        <div className="grid">
          <section>
            <h2>Create Account</h2>
            <form onSubmit={handleRegister}>
              <input
                type="text"
                placeholder="Name"
                value={registerForm.name}
                onChange={(e) => setRegisterForm({ ...registerForm, name: e.target.value })}
              />
              <input
                type="email"
                placeholder="Email"
                value={registerForm.email}
                onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
              />
              <input
                type="password"
                placeholder="Password"
                value={registerForm.password}
                onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
              />
              <button className="primary" type="submit">Register</button>
            </form>
          </section>

          <section>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
              <input
                type="email"
                placeholder="Email"
                value={loginForm.email}
                onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })}
              />
              <input
                type="password"
                placeholder="Password"
                value={loginForm.password}
                onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
              />
              <button className="primary" type="submit">Login</button>
            </form>
            <button className="ghost" onClick={handleGoogleLogin}>Login with Google</button>
            <button className="danger" onClick={handleLogout}>Logout</button>
          </section>
        </div>

        <section className="profile">
          <h2>Profile</h2>
          {profile ? (
            <pre>{JSON.stringify(profile, null, 2)}</pre>
          ) : (
            <p className="muted">No active session</p>
          )}
        </section>

        {isAdmin && (
          <section className="admin">
            <div className="admin-header">
              <h2>Admin: Manage Roles</h2>
              <button className="ghost" onClick={loadUsers}>Reload</button>
            </div>
            {adminStatus && <p className="status-line">{adminStatus}</p>}
            <div className="user-list">
              {users.map((user) => (
                <div className="user-card" key={user.id}>
                  <div className="user-meta">
                    <strong>{user.name}</strong>
                    <span>{user.email}</span>
                    <span className="muted">{user.provider}</span>
                  </div>
                  <input
                    type="text"
                    value={roleDrafts[user.id] || ""}
                    onChange={(e) => setRoleDrafts({ ...roleDrafts, [user.id]: e.target.value })}
                    placeholder="ROLE_USER, ROLE_ADMIN"
                  />
                  <button className="primary" onClick={() => updateRoles(user.id)}>Save Roles</button>
                </div>
              ))}
              {users.length === 0 && <p className="muted">No users found.</p>}
            </div>

            <div className="audit">
              <h3>Recent Role Changes</h3>
              {auditLogs.length === 0 ? (
                <p className="muted">No audit entries yet.</p>
              ) : (
                <div className="audit-list">
                  {auditLogs.map((log) => (
                    <div className="audit-item" key={log.id}>
                      <div>
                        <strong>{log.actorEmail}</strong>
                        <span className="muted"> updated </span>
                        <strong>{log.targetEmail}</strong>
                      </div>
                      <div className="muted">{log.roles}</div>
                      <div className="muted">{new Date(log.createdAt).toLocaleString()}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </section>
        )}
      </div>
    </div>
  );
}
