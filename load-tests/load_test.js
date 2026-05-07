import http from "k6/http";
import { check, sleep } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";

const registerOkCount  = new Counter("register_201");
const loginOkCount     = new Counter("login_200");
const profileOkCount   = new Counter("profile_200");
const guestOkCount     = new Counter("guest_201");

const registerErrors   = new Counter("register_errors");
const loginErrors      = new Counter("login_errors");
const guestErrors      = new Counter("guest_errors");
const profileErrors    = new Counter("profile_errors");
const loginSuccessRate = new Rate("login_success_rate");

const profileDuration  = new Trend("profile_duration_ms");
const registerDuration = new Trend("register_duration_ms");
const loginDuration    = new Trend("login_duration_ms");

export const options = {
  stages: [
    { duration: "30s", target: 10  },
    { duration: "1m",  target: 50  },
    { duration: "30s", target: 100 },
    { duration: "1m",  target: 100 },
    { duration: "30s", target: 0   },
  ],
  thresholds: {
    http_req_duration:  ["p(95)<3000"],
    login_success_rate: ["rate>0.98"],
    register_errors:    ["count<5"],
    profile_errors:     ["count<10"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

function jsonHeaders(token) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return headers;
}

/**
 * Consulta el perfil con reintentos y backoff exponencial.
 * El sistema es async (RabbitMQ), así que el perfil puede no estar listo
 * inmediatamente. Reintentamos hasta maxAttempts veces antes de contar error.
 *
 * @param {string} userId
 * @param {string} accessToken
 * @param {number} maxAttempts - cuántas veces intentar (default: 5)
 * @param {number} baseDelayMs - espera inicial en ms, se duplica cada intento
 * @returns {boolean} true si el perfil se obtuvo con 200
 */
function fetchProfileWithRetry(userId, accessToken, maxAttempts = 5, baseDelayMs = 500) {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    const start = Date.now();
    const res   = http.get(
      `${BASE_URL}/user-profile/${userId}`,
      { headers: jsonHeaders(accessToken) }
    );
    profileDuration.add(Date.now() - start);

    if (res.status === 200) {
      return true;
    }

    // Si es el último intento, no esperamos más
    if (attempt < maxAttempts) {
      const delay = (baseDelayMs * Math.pow(2, attempt - 1)) / 1000; // segundos
      console.warn(
        `[PROFILE] VU=${__VU} userId=${userId} intento=${attempt}/${maxAttempts} status=${res.status} — reintentando en ${delay.toFixed(1)}s`
      );
      sleep(delay);
    } else {
      console.error(
        `[PROFILE] VU=${__VU} userId=${userId} AGOTADOS ${maxAttempts} intentos status=${res.status}`
      );
    }
  }
  return false;
}

export default function () {
  const id       = `${__VU}_${__ITER}_${Date.now()}`;
  const email    = `user_${id}@test.local`;
  const username = `user_${id}`;
  const password = "Test1234!";

  // 1. Register
  const regStart    = Date.now();
  const registerRes = http.post(
    `${BASE_URL}/api/v1/users/register`,
    JSON.stringify({ email, username, password }),
    { headers: jsonHeaders() }
  );
  registerDuration.add(Date.now() - regStart);

  const registerOk = check(registerRes, {
    "register → 201":         (r) => r.status === 201,
    "register → accessToken": (r) => {
      try { return !!JSON.parse(r.body).accessToken; } catch { return false; }
    },
  });

  if (registerOk) {
    registerOkCount.add(1);
  } else {
    registerErrors.add(1);
    console.error(`[REGISTER] VU=${__VU} status=${registerRes.status} body=${registerRes.body}`);
    return;
  }

  sleep(0.5);

  // 2. Login
  const loginStart = Date.now();
  const loginRes   = http.post(
    `${BASE_URL}/api/v1/users/login`,
    JSON.stringify({ email, password }),
    { headers: jsonHeaders() }
  );
  loginDuration.add(Date.now() - loginStart);

  const loginOk = check(loginRes, {
    "login → 200":          (r) => r.status === 200,
    "login → refreshToken": (r) => {
      try { return !!JSON.parse(r.body).refreshToken; } catch { return false; }
    },
  });

  loginSuccessRate.add(loginOk ? 1 : 0);

  if (loginOk) {
    loginOkCount.add(1);
  } else {
    loginErrors.add(1);
    console.error(`[LOGIN] VU=${__VU} status=${loginRes.status} body=${loginRes.body}`);
    return;
  }

  let userId      = null;
  let accessToken = null;
  try {
    const body  = JSON.parse(loginRes.body);
    userId      = body.userId;
    accessToken = body.accessToken;
  } catch (_) {}

  // 3. Profile con retry — respeta la naturaleza async de RabbitMQ
  if (userId && accessToken) {
    // Espera mínima inicial: damos al consumer una oportunidad base
    sleep(1);

    const profileOk = fetchProfileWithRetry(userId, accessToken, 5, 500);
    // intentos: 1s → 2s → 4s → 8s → falla (máx ~15s extra en el peor caso)

    if (profileOk) {
      profileOkCount.add(1);
    } else {
      profileErrors.add(1);
    }
  }

  sleep(0.5);

  // 4. Guest
  const guestRes = http.post(
    `${BASE_URL}/api/v1/users/guest`,
    JSON.stringify({ username: `guest_${__VU}_${__ITER}_${Date.now()}` }),
    { headers: jsonHeaders() }
  );

  const guestOk = check(guestRes, {
    "guest → 201":         (r) => r.status === 201,
    "guest → accessToken": (r) => {
      try { return !!JSON.parse(r.body).accessToken; } catch { return false; }
    },
  });

  if (guestOk) {
    guestOkCount.add(1);
  } else {
    guestErrors.add(1);
    console.error(`[GUEST] VU=${__VU} status=${guestRes.status} body=${guestRes.body}`);
  }

  sleep(1);
}

export function handleSummary(data) {
  const m = data.metrics;

  const p95        = m.http_req_duration?.values?.["p(95)"]?.toFixed(2)     ?? "N/A";
  const p99        = m.http_req_duration?.values?.["p(99)"]?.toFixed(2)     ?? "N/A";
  const avgReq     = m.http_req_duration?.values?.avg?.toFixed(2)           ?? "N/A";
  const rps        = m.http_reqs?.values?.rate?.toFixed(2)                  ?? "N/A";
  const total      = m.http_reqs?.values?.count                             ?? 0;
  const failed     = m.http_req_failed?.values?.rate                        ?? 0;
  const maxVUs     = m.vus_max?.values?.max                                 ?? "N/A";

  const reg201     = m["register_201"]?.values?.count  ?? 0;
  const log200     = m["login_200"]?.values?.count     ?? 0;
  const prof200    = m["profile_200"]?.values?.count   ?? 0;
  const guest201   = m["guest_201"]?.values?.count     ?? 0;

  const loginRate  = ((m.login_success_rate?.values?.rate ?? 0) * 100).toFixed(2);
  const regErr     = m.register_errors?.values?.count  ?? 0;
  const logErr     = m.login_errors?.values?.count     ?? 0;
  const profErr    = m.profile_errors?.values?.count   ?? 0;
  const guestErr   = m.guest_errors?.values?.count     ?? 0;

  const regP95     = m.register_duration_ms?.values?.["p(95)"]?.toFixed(2) ?? "N/A";
  const loginP95   = m.login_duration_ms?.values?.["p(95)"]?.toFixed(2)    ?? "N/A";
  const profileP95 = m.profile_duration_ms?.values?.["p(95)"]?.toFixed(2)  ?? "N/A";

  const thresholdP95   = parseFloat(p95) < 3000;
  const thresholdLogin = parseFloat(loginRate) > 98;
  const thresholdReg   = regErr < 5;
  const thresholdProf  = profErr < 10;
  const passed = thresholdP95 && thresholdLogin && thresholdReg && thresholdProf;

  const pad = (v, n = 30) => String(v).padEnd(n);

  const report = `
╔══════════════════════════════════════════════════════════════╗
║         REPORTE DE CARGA — User Management API              ║
╠══════════════════════════════════════════════════════════════╣
║  VUs máximos concurrentes : ${pad(maxVUs)}   ║
║  Total peticiones         : ${pad(total)}   ║
║  Peticiones por segundo   : ${pad(rps + " req/s")}   ║
║  Tasa de fallos HTTP      : ${pad((failed * 100).toFixed(2) + "%")}   ║
╠══════════════════════════════════════════════════════════════╣
║  RESPUESTAS EXITOSAS POR ENDPOINT                            ║
║  POST /register  → 201    : ${pad(reg201 + " peticiones OK")}   ║
║  POST /login     → 200    : ${pad(log200 + " peticiones OK")}   ║
║  GET  /user-profile → 200 : ${pad(prof200 + " peticiones OK")}   ║
║  POST /guest     → 201    : ${pad(guest201 + " peticiones OK")}   ║
╠══════════════════════════════════════════════════════════════╣
║  TIEMPOS DE RESPUESTA GLOBALES                               ║
║  Promedio                 : ${pad(avgReq + " ms")}   ║
║  P95                      : ${pad(p95 + " ms")}   ║
║  P99                      : ${pad(p99 + " ms")}   ║
╠══════════════════════════════════════════════════════════════╣
║  TIEMPOS POR ENDPOINT (P95, todos los intentos)              ║
║  POST /register           : ${pad(regP95 + " ms")}   ║
║  POST /login              : ${pad(loginP95 + " ms")}   ║
║  GET  /user-profile/{id}  : ${pad(profileP95 + " ms")}   ║
╠══════════════════════════════════════════════════════════════╣
║  ERRORES POR FLUJO                                           ║
║  Login success rate       : ${pad(loginRate + "%")}   ║
║  Errores register         : ${pad(regErr)}   ║
║  Errores login            : ${pad(logErr)}   ║
║  Errores profile (404)    : ${pad(profErr)}   ║
║  Errores guest            : ${pad(guestErr)}   ║
╠══════════════════════════════════════════════════════════════╣
║  UMBRALES                                                    ║
║  P95 < 3000ms             : ${pad(thresholdP95   ? "✅ OK" : "❌ FALLÓ")}   ║
║  Login rate > 98%         : ${pad(thresholdLogin ? "✅ OK" : "❌ FALLÓ")}   ║
║  Register errors < 5      : ${pad(thresholdReg   ? "✅ OK" : "❌ FALLÓ")}   ║
║  Profile errors < 10      : ${pad(thresholdProf  ? "✅ OK" : "❌ FALLÓ")}   ║
╠══════════════════════════════════════════════════════════════╣
║  RESULTADO FINAL: ${passed
    ? "✅  PASÓ — Todos los umbrales cumplidos  "
    : "❌  FALLÓ — Revisar errores arriba        "}║
╚══════════════════════════════════════════════════════════════╝
`;

  console.log(report);
  return {
    stdout:                   report,
    "load-test-summary.json": JSON.stringify(data, null, 2),
  };
}