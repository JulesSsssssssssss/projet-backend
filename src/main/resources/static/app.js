'use strict';

// ── Constants ──────────────────────────────────────────────────────
const TOKEN_KEY = 'cg_token';
const USER_KEY  = 'cg_user';
const VIEWS     = ['auth', 'dashboard', 'inventory', 'trades'];

// ── State ──────────────────────────────────────────────────────────
let currentToken    = null;
let currentUsername = null;

// ── Session ────────────────────────────────────────────────────────
// sessionStorage: cleared on tab close, reducing the JWT exposure window.
// Trade-off vs localStorage: user must re-login in each new tab.
function saveSession(token, username) {
  currentToken    = token;
  currentUsername = username;
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(USER_KEY,  username);
}

function loadSession() {
  currentToken    = sessionStorage.getItem(TOKEN_KEY);
  currentUsername = sessionStorage.getItem(USER_KEY);
}

function clearSession() {
  currentToken    = null;
  currentUsername = null;
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
}

// ── Safe DOM helpers ───────────────────────────────────────────────
// All user-facing text goes through textContent — never innerHTML.
function el(id)          { return document.getElementById(id); }
function qs(sel)         { return document.querySelector(sel); }
function qsAll(sel)      { return document.querySelectorAll(sel); }

function setText(id, value) {
  const node = el(id);
  if (node) node.textContent = String(value ?? '');
}

function show(id)  { el(id)?.classList.remove('hidden'); }
function hide(id)  { el(id)?.classList.add('hidden'); }
function clear(id) { const n = el(id); if (n) n.textContent = ''; }

// Only safe attribute names are passed here — no user-controlled attr names.
function createEl(tag, attrs, text) {
  const node = document.createElement(tag);
  if (attrs) {
    for (const [k, v] of Object.entries(attrs)) {
      node.setAttribute(k, String(v));
    }
  }
  if (text != null) node.textContent = String(text);
  return node;
}

// ── View router ────────────────────────────────────────────────────
function showView(name) {
  VIEWS.forEach(v => {
    const node = el('view-' + v);
    if (node) node.classList.toggle('active', v === name);
  });
}

// ── Message helpers ────────────────────────────────────────────────
function showError(id, msg) {
  const node = el(id);
  if (!node) return;
  node.textContent = String(msg);   // textContent = XSS-safe
  node.className = 'error';
}

function showMsg(id, msg, type = 'success') {
  const node = el(id);
  if (!node) return;
  node.textContent = String(msg);
  node.className = type === 'error' ? 'msg error' : 'msg success';
}

// ── Input validation ───────────────────────────────────────────────
// Validate before every fetch to prevent obviously malformed requests.
function isValidUsername(v) {
  return typeof v === 'string' && /^[a-zA-Z0-9_]{3,20}$/.test(v);
}

function isValidPassword(v) {
  return typeof v === 'string' && v.length >= 6 && v.length <= 128;
}

function isValidEmail(v) {
  return typeof v === 'string'
    && v.length <= 254
    && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
}

function isValidId(v) {
  const n = parseInt(v, 10);
  return Number.isInteger(n) && n > 0;
}

// ── API wrapper ────────────────────────────────────────────────────
// Centralises the Authorization header and 401/403 handling.
// Returns { ok, status, data } or null on network error.
async function api(path, opts = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (currentToken) headers['Authorization'] = 'Bearer ' + currentToken;

  let res;
  try {
    res = await fetch('/api' + path, { ...opts, headers });
  } catch {
    return null; // network error
  }

  // Expired or invalid token → force logout
  if (res.status === 401 || res.status === 403) {
    clearSession();
    showView('auth');
    return null;
  }

  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }

  return { ok: res.ok, status: res.status, data };
}

// ── Auth ───────────────────────────────────────────────────────────
async function doLogin(e) {
  e.preventDefault();
  clear('auth-error');

  const uname = el('login-username').value.trim();
  const pwd   = el('login-password').value;

  if (!isValidUsername(uname)) {
    showError('auth-error', 'Nom d\'utilisateur invalide (3-20 caractères, lettres/chiffres/_).');
    return;
  }
  if (!isValidPassword(pwd)) {
    showError('auth-error', 'Mot de passe trop court (minimum 6 caractères).');
    return;
  }

  const btn = e.target.querySelector('button[type="submit"]');
  btn.disabled = true;
  const res = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: uname, password: pwd }),
  });
  btn.disabled = false;

  if (!res) { showError('auth-error', 'Erreur réseau, réessayez.'); return; }
  if (!res.ok) {
    showError('auth-error', res.data?.message ?? 'Identifiants incorrects.');
    return;
  }

  saveSession(res.data.token, res.data.username);
  await openDashboard();
}

async function doRegister(e) {
  e.preventDefault();
  clear('auth-error');

  const uname = el('reg-username').value.trim();
  const email = el('reg-email').value.trim();
  const pwd   = el('reg-password').value;

  if (!isValidUsername(uname)) {
    showError('auth-error', 'Nom d\'utilisateur invalide (3-20 caractères, lettres/chiffres/_).');
    return;
  }
  if (!isValidEmail(email)) {
    showError('auth-error', 'Adresse email invalide.');
    return;
  }
  if (!isValidPassword(pwd)) {
    showError('auth-error', 'Mot de passe trop court (minimum 6 caractères).');
    return;
  }

  const btn = e.target.querySelector('button[type="submit"]');
  btn.disabled = true;
  const res = await api('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username: uname, email, password: pwd }),
  });
  btn.disabled = false;

  if (!res) { showError('auth-error', 'Erreur réseau, réessayez.'); return; }
  if (!res.ok) {
    showError('auth-error', res.data?.message ?? 'Inscription échouée.');
    return;
  }

  // Register ne retourne pas de token → auto-login
  const loginRes = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: uname, password: pwd }),
  });

  if (loginRes?.ok) {
    saveSession(loginRes.data.token, loginRes.data.username);
    await openDashboard();
  } else {
    showError('auth-error', 'Inscription réussie ! Connectez-vous.');
    switchTab('login');
  }
}

function doLogout() {
  clearSession();
  setText('dash-username', '');
  setText('dash-points', '0');
  hide('gacha-result');
  clear('gacha-result');
  showView('auth');
}

// ── Dashboard ──────────────────────────────────────────────────────
async function openDashboard() {
  showView('dashboard');
  setText('dash-username', currentUsername ?? '');
  hide('gacha-result');
  clear('gacha-result');
  await refreshPoints('dash-points');
}

async function refreshPoints(targetId) {
  const res = await api('/gacha/points');
  if (res?.ok) setText(targetId, res.data);
}

async function doPull() {
  const btn = el('btn-pull');
  btn.disabled = true;
  hide('gacha-result');
  clear('gacha-result');

  const res = await api('/gacha', { method: 'POST' });
  btn.disabled = false;

  const container = el('gacha-result');

  if (!res || !res.ok) {
    const msg = typeof res?.data === 'string'
      ? res.data
      : (res?.data?.message ?? 'Erreur lors du tirage. Vérifiez vos points.');
    container.appendChild(createEl('p', { class: 'error' }, msg));
    show('gacha-result');
    return;
  }

  const { card, remainingPoints } = res.data;
  setText('dash-points', remainingPoints ?? 0);
  container.appendChild(buildCardEl(card));
  show('gacha-result');
}

// ── Inventory ──────────────────────────────────────────────────────
async function openInventory() {
  showView('inventory');
  clear('inventory-grid');

  await refreshPoints('inv-points');
  const res = await api('/cards/inventory');
  const grid = el('inventory-grid');

  if (!res?.ok || !Array.isArray(res.data) || res.data.length === 0) {
    grid.appendChild(createEl('p', { class: 'empty-state' }, 'Votre inventaire est vide.'));
    return;
  }

  res.data.forEach(card => grid.appendChild(buildCardEl(card)));
}

// ── Trades ─────────────────────────────────────────────────────────
async function openTrades() {
  showView('trades');
  clear('trade-msg');
  await loadPendingTrades();
}

async function loadPendingTrades() {
  const container = el('trades-list');
  clear('trades-list');

  const res = await api('/trades/pending');

  if (!res?.ok || !Array.isArray(res.data) || res.data.length === 0) {
    container.appendChild(createEl('p', { class: 'empty-state' }, 'Aucun échange en attente.'));
    return;
  }

  res.data.forEach(trade => container.appendChild(buildTradeEl(trade)));
}

async function doProposeTrade(e) {
  e.preventDefault();
  clear('trade-msg');

  const offeredId   = el('trade-offered').value;
  const target      = el('trade-target').value.trim();
  const requestedId = el('trade-requested').value;

  if (!isValidId(offeredId))    { showMsg('trade-msg', 'ID carte offerte invalide.',    'error'); return; }
  if (!isValidUsername(target)) { showMsg('trade-msg', 'Nom d\'utilisateur invalide.',  'error'); return; }
  if (!isValidId(requestedId))  { showMsg('trade-msg', 'ID carte demandée invalide.',   'error'); return; }

  const btn = e.target.querySelector('button[type="submit"]');
  btn.disabled = true;
  const res = await api('/trades/propose', {
    method: 'POST',
    body: JSON.stringify({
      offeredCardId:   parseInt(offeredId,   10),
      targetUsername:  target,
      requestedCardId: parseInt(requestedId, 10),
    }),
  });
  btn.disabled = false;

  if (!res || !res.ok) {
    showMsg('trade-msg', res?.data?.message ?? 'Erreur lors de la proposition.', 'error');
    return;
  }

  showMsg('trade-msg', 'Échange proposé avec succès !', 'success');
  el('form-trade').reset();
  await loadPendingTrades();
}

async function doAcceptTrade(tradeId) {
  // Validate the ID from the API response before using it in the URL
  if (!isValidId(tradeId)) return;

  // encodeURIComponent ensures no path injection even if ID were non-numeric
  const res = await api('/trades/accept/' + encodeURIComponent(tradeId), { method: 'POST' });

  if (!res || !res.ok) {
    showMsg('trade-msg', typeof res?.data === 'string' ? res.data : 'Erreur lors de l\'acceptation.', 'error');
    return;
  }

  showMsg('trade-msg', 'Échange accepté !', 'success');
  await loadPendingTrades();
}

// ── Card builder ───────────────────────────────────────────────────
// Uses only createElement + textContent: no innerHTML, no XSS risk.
function buildCardEl(card) {
  if (!card) return createEl('p', { class: 'empty-state' }, 'Carte inconnue.');

  const rarity = card.rarity?.name ?? 'COMMON';
  const div    = createEl('div', { class: 'card', 'data-rarity': rarity });

  div.appendChild(createEl('h3', { class: 'card-name' },   card.name   ?? '?'));
  div.appendChild(createEl('span', { class: 'card-rarity' }, rarity));

  if (card.description) {
    div.appendChild(createEl('p', { class: 'card-desc' }, card.description));
  }

  const stats = createEl('div', { class: 'card-stats' });
  stats.appendChild(createEl('span', {}, 'ATK ' + (card.attack  ?? '—')));
  stats.appendChild(createEl('span', {}, 'DEF ' + (card.defense ?? '—')));
  div.appendChild(stats);

  return div;
}

function buildTradeEl(trade) {
  const div = createEl('div', { class: 'trade-item' });

  const info = createEl('span', { class: 'trade-info' },
    'De : ' + (trade.fromUser?.username ?? '?'));
  const cards = createEl('span', { class: 'trade-cards' },
    (trade.offeredCard?.name ?? '?') + ' → ' + (trade.requestedCard?.name ?? '?'));

  const btn = createEl('button', { class: 'btn-accept' }, 'Accepter');
  // Capture trade.id in closure — validated in doAcceptTrade before URL use
  btn.addEventListener('click', () => doAcceptTrade(trade.id));

  div.appendChild(info);
  div.appendChild(cards);
  div.appendChild(btn);
  return div;
}

// ── Tab switching ──────────────────────────────────────────────────
function switchTab(name) {
  qsAll('.tab').forEach(t => {
    const isActive = t.dataset.tab === name;
    t.classList.toggle('active', isActive);
    t.setAttribute('aria-selected', String(isActive));
  });
  qsAll('.tab-content').forEach(tc => {
    tc.classList.toggle('active', tc.id === 'form-' + name);
  });
}

// ── Event wiring ───────────────────────────────────────────────────
function wireEvents() {
  // Tab buttons
  qsAll('.tab').forEach(btn =>
    btn.addEventListener('click', () => switchTab(btn.dataset.tab))
  );

  // Auth
  el('form-login').addEventListener('submit', doLogin);
  el('form-register').addEventListener('submit', doRegister);

  // Dashboard
  el('btn-logout').addEventListener('click', doLogout);
  el('btn-pull').addEventListener('click', doPull);
  el('btn-to-inventory').addEventListener('click', openInventory);
  el('btn-to-trades').addEventListener('click', openTrades);

  // Inventory
  el('btn-inv-back').addEventListener('click', openDashboard);

  // Trades
  el('btn-trades-back').addEventListener('click', openDashboard);
  el('form-trade').addEventListener('submit', doProposeTrade);
}

// ── Init ───────────────────────────────────────────────────────────
function init() {
  wireEvents();
  loadSession();
  if (currentToken && currentUsername) {
    openDashboard();
  } else {
    showView('auth');
  }
}

document.addEventListener('DOMContentLoaded', init);
