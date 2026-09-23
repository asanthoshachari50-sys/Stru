const API = "http://localhost:8080/api";
let token = sessionStorage.getItem("token");

const out = document.getElementById("output");
const show = (value) => out.textContent = typeof value === "string" ? value : JSON.stringify(value, null, 2);

async function api(path, options = {}) {
  const headers = {"Content-Type":"application/json", ...(options.headers || {})};
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(API + path, {...options, headers});
  const text = await response.text();
  let data = text;
  try { data = text ? JSON.parse(text) : {}; } catch {}
  if (!response.ok) throw new Error(data?.message || data?.error || `HTTP ${response.status}`);
  return data;
}

document.getElementById("registerForm").addEventListener("submit", async e => {
  e.preventDefault();
  try {
    const data = await api("/auth/register", {
      method:"POST",
      body:JSON.stringify({
        name:rName.value, email:rEmail.value, password:rPassword.value
      })
    });
    show({message:"Registered successfully", user:data});
    e.target.reset();
  } catch (err) { show(err.message); }
});

document.getElementById("loginForm").addEventListener("submit", async e => {
  e.preventDefault();
  try {
    const data = await api("/auth/login", {
      method:"POST",
      body:JSON.stringify({email:lEmail.value, password:lPassword.value})
    });
    token = data.token;
    sessionStorage.setItem("token", token);
    show({message:"Login successful", user:data.user});
    e.target.reset();
  } catch (err) { show(err.message); }
});

document.getElementById("profileBtn").onclick = async () => {
  try { show(await api("/users/me")); } catch (err) { show(err.message); }
};

document.getElementById("adminBtn").onclick = async () => {
  try { show(await api("/admin/users")); } catch (err) { show(err.message); }
};

document.getElementById("logoutBtn").onclick = () => {
  token = null;
  sessionStorage.removeItem("token");
  show("Logged out.");
};
