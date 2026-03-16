import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

// Métricas personalizadas
const registerErrors   = new Counter("register_errors");
const loginErrors      = new Counter("login_errors");
const guestErrors      = new Counter("guest_errors");
const profileErrors    = new Counter("profile_errors");
const loginSuccessRate = new Rate("login_success_rate");
const profileDuration  = new Trend("profile_duration_ms");

// Configuración de la prueba
export const options = {
  stages: [
    { duration: "30s", target: 10  },
    { duration: "1m",  target: 50  },
    { duration: "30s", target: 100 },
    { duration: "1m",  target: 100 },
    { duration: "30s", target: 0   },
  ],
  thresholds: {
    http_req_duration:  ["p(95)<500"],
    login_success_rate: ["rate>0.98"],
    register_errors:    ["count<5"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

function jsonHeaders(token) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return headers;
}

function uniqueEmail() {
  return `user_${__VU}_${__ITER}_${Date.now()}@test.local`;
}

function uniqueUsername() {
  return `user_${__VU}_${__ITER}_${Date.now()}`;
}

export default function () {

  // 1. REGISTER
  const email    = uniqueEmail();
  const username = uniqueUsername();
  const password = "Test1234!";

  const registerRes = http.post(
    `${BASE_URL}/api/v1/users/register`,
    JSON.stringify({ email, username, password }),
    { headers: jsonHeaders() }
  );

  const registerOk = check(registerRes, {
    "register → status 201":        (r) => r.status === 201,
    "register → tiene accessToken": (r) => {
      try { return JSON.parse(r.body).accessToken !== undefined; }
      catch { return false; }
    },
  });

  if (!registerOk) {
    registerErrors.add(1);
    console.error(`[REGISTER] VU=${__VU} status=${registerRes.status} body=${registerRes.body}`);
  }

  sleep(0.5);

  // 2. LOGIN
  const loginRes = http.post(
    `${BASE_URL}/api/v1/users/login`,
    JSON.stringify({ email, password }),
    { headers: jsonHeaders() }
  );

  const loginOk = check(loginRes, {
    "login → status 200":         (r) => r.status === 200,
    "login → tiene refreshToken": (r) => {
      try { return JSON.parse(r.body).refreshToken !== undefined; }
      catch { return false; }
    },
  });

  loginSuccessRate.add(loginOk ? 1 : 0);

  if (!loginOk) {
    loginErrors.add(1);
    console.error(`[LOGIN] VU=${__VU} status=${loginRes.status} body=${loginRes.body}`);
  }

  // Extraemos userId y accessToken para usarlos en el perfil
  let userId      = null;
  let accessToken = null;
  try {
    const body  = JSON.parse(loginRes.body);
    userId      = body.userId;
    accessToken = body.accessToken;
  } catch (_) {}

  sleep(0.5);

  // 3. GET PROFILE (con token)
  if (userId && accessToken) {
    const profileStart = Date.now();

    const profileRes = http.get(
      `${BASE_URL}/user-profile/${userId}`,
      { headers: jsonHeaders(accessToken) }
    );

    profileDuration.add(Date.now() - profileStart);

    const profileOk = check(profileRes, {
      "profile → status 200 o 404": (r) => r.status === 200 || r.status === 404,
    });

    if (!profileOk) {
      profileErrors.add(1);
      console.error(`[PROFILE] VU=${__VU} userId=${userId} status=${profileRes.status}`);
    }
  }

  sleep(0.5);

  // 4. REGISTER GUEST
  const guestRes = http.post(
    `${BASE_URL}/api/v1/users/guest`,
    JSON.stringify({ username: `guest_${__VU}_${__ITER}` }),
    { headers: jsonHeaders() }
  );

  const guestOk = check(guestRes, {
    "guest → status 201":        (r) => r.status === 201,
    "guest → tiene accessToken": (r) => {
      try { return JSON.parse(r.body).accessToken !== undefined; }
      catch { return false; }
    },
  });

  if (!guestOk) {
    guestErrors.add(1);
    console.error(`[GUEST] VU=${__VU} status=${guestRes.status} body=${guestRes.body}`);
  }

  sleep(1);
}