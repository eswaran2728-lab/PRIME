// Remix PRIME — local-first web app. All data lives in localStorage.
const STORE_KEY = "prime.v1";
const todayStr = () => new Date().toISOString().slice(0, 10);

const defaultState = () => ({
  workouts: [],   // {id, date, exercise, sets, reps, weightKg}
  nutrition: [],  // {id, date, food, calories, protein}
  habits: [],     // {id, name, logs: {date: true}}
  blocks: [],     // {id, date, title, start, end, category}
  body: [],       // {id, date, weightKg, waistCm}
});

function loadState() {
  try {
    const raw = localStorage.getItem(STORE_KEY);
    if (!raw) return defaultState();
    const parsed = JSON.parse(raw);
    return { ...defaultState(), ...parsed };
  } catch {
    return defaultState();
  }
}

function saveState() {
  localStorage.setItem(STORE_KEY, JSON.stringify(state));
}

let state = loadState();
const uid = () => Math.random().toString(36).slice(2, 10);

// ---------- PRIME score ----------
// Simplified, deterministic sub-scores over the last 7 days, weighted to 100.
function computeScore() {
  const days = [...Array(7)].map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() - i);
    return d.toISOString().slice(0, 10);
  });

  const workoutDays = new Set(state.workouts.filter(w => days.includes(w.date)).map(w => w.date)).size;
  const fitness = Math.min(1, workoutDays / 4); // target 4 sessions/wk

  const nutritionDays = new Set(state.nutrition.filter(n => days.includes(n.date)).map(n => n.date)).size;
  const nutrition = Math.min(1, nutritionDays / 7);

  const habitTotal = state.habits.length * days.length;
  const habitDone = state.habits.reduce((sum, h) => sum + days.filter(d => h.logs[d]).length, 0);
  const discipline = habitTotal ? habitDone / habitTotal : 0;

  const blockDays = new Set(state.blocks.filter(b => days.includes(b.date)).map(b => b.date)).size;
  const focus = Math.min(1, blockDays / 5);

  const bodyDays = new Set(state.body.filter(b => days.includes(b.date)).map(b => b.date)).size;
  const bodyTracking = Math.min(1, bodyDays / 2);

  const weights = { fitness: 25, nutrition: 20, discipline: 20, focus: 20, bodyTracking: 15 };
  const subs = { fitness, nutrition, discipline, focus, bodyTracking };
  let total = 0;
  for (const k in weights) total += subs[k] * weights[k];

  return { total: Math.round(total), subs, weights };
}

// ---------- rendering ----------
const view = document.getElementById("view");
const tabsEl = document.getElementById("tabs");
let activeTab = "dashboard";

function setActiveTab(tab) {
  activeTab = tab;
  [...tabsEl.querySelectorAll(".tab")].forEach(btn => {
    btn.classList.toggle("active", btn.dataset.tab === tab);
  });
  render();
}

tabsEl.addEventListener("click", e => {
  const btn = e.target.closest(".tab");
  if (btn) setActiveTab(btn.dataset.tab);
});

function render() {
  updateScorePill();
  const renderers = {
    dashboard: renderDashboard,
    workout: renderWorkout,
    nutrition: renderNutrition,
    habits: renderHabits,
    planner: renderPlanner,
    body: renderBody,
  };
  view.innerHTML = "";
  renderers[activeTab]();
}

function updateScorePill() {
  document.getElementById("scoreNum").textContent = computeScore().total;
}

function el(tag, attrs = {}, children = []) {
  const node = document.createElement(tag);
  for (const [k, v] of Object.entries(attrs)) {
    if (k === "class") node.className = v;
    else if (k.startsWith("on") && typeof v === "function") node.addEventListener(k.slice(2), v);
    else node.setAttribute(k, v);
  }
  for (const c of [].concat(children)) {
    if (c == null) continue;
    node.appendChild(typeof c === "string" ? document.createTextNode(c) : c);
  }
  return node;
}

// ---------- Dashboard ----------
function renderDashboard() {
  const { total, subs, weights } = computeScore();
  const labels = { fitness: "Fitness", nutrition: "Nutrition", discipline: "Discipline", focus: "Focus", bodyTracking: "Body" };

  view.appendChild(el("h1", { class: "page-title" }, "Dashboard"));
  view.appendChild(el("p", { class: "page-sub" }, "Your PRIME score reflects the last 7 days across all pillars."));

  const grid = el("div", { class: "grid" }, [
    statCard("PRIME Score", String(total), "out of 100"),
    statCard("Workouts (7d)", String(state.workouts.filter(w => inLast7(w.date)).length), "sessions logged"),
    statCard("Habits done (7d)", String(state.habits.reduce((s, h) => s + Object.values(h.logs).filter(Boolean).length, 0)), "completions"),
    statCard("Body logs (7d)", String(state.body.filter(b => inLast7(b.date)).length), "entries"),
  ]);
  view.appendChild(grid);

  const breakdown = el("div", { class: "breakdown card" });
  breakdown.appendChild(el("h3", {}, "Score breakdown"));
  for (const k of Object.keys(weights)) {
    const pct = Math.round(subs[k] * 100);
    breakdown.appendChild(el("div", { class: "breakdown-row" }, [
      el("div", { class: "breakdown-name" }, labels[k]),
      el("div", { class: "bar-track" }, el("div", { class: "bar-fill", style: `width:${pct}%` })),
      el("div", { class: "breakdown-val" }, `${pct}%`),
    ]));
  }
  view.appendChild(breakdown);
}

function statCard(title, big, dim) {
  return el("div", { class: "card" }, [
    el("h3", {}, title),
    el("div", { class: "big" }, big),
    el("div", { class: "dim" }, dim),
  ]);
}

function inLast7(dateStr) {
  const d = new Date(dateStr);
  const cutoff = new Date();
  cutoff.setDate(cutoff.getDate() - 7);
  return d >= cutoff;
}

// ---------- Workout ----------
function renderWorkout() {
  view.appendChild(el("h1", { class: "page-title" }, "Workout"));
  view.appendChild(el("p", { class: "page-sub" }, "Log your training sessions."));

  const exercise = el("input", { placeholder: "Exercise (e.g. Bench Press)" });
  const sets = el("input", { placeholder: "Sets", type: "number" });
  const reps = el("input", { placeholder: "Reps", type: "number" });
  const weight = el("input", { placeholder: "Weight (kg)", type: "number" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!exercise.value.trim()) return;
    state.workouts.unshift({ id: uid(), date: todayStr(), exercise: exercise.value.trim(),
      sets: Number(sets.value) || 0, reps: Number(reps.value) || 0, weightKg: Number(weight.value) || 0 });
    saveState(); render();
  }}, "Add");

  view.appendChild(el("div", { class: "form-row" }, [exercise, sets, reps, weight, addBtn]));

  const list = el("div", { class: "list" });
  if (!state.workouts.length) {
    list.appendChild(el("div", { class: "empty" }, "No workouts logged yet."));
  } else {
    for (const w of state.workouts) {
      list.appendChild(el("div", { class: "list-item" }, [
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, w.exercise),
          el("div", { class: "li-sub" }, `${w.sets}×${w.reps} @ ${w.weightKg}kg · ${w.date}`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.workouts = state.workouts.filter(x => x.id !== w.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

// ---------- Nutrition ----------
function renderNutrition() {
  view.appendChild(el("h1", { class: "page-title" }, "Nutrition"));
  view.appendChild(el("p", { class: "page-sub" }, "Log meals and macros."));

  const food = el("input", { placeholder: "Food (e.g. Chicken & rice)" });
  const calories = el("input", { placeholder: "Calories", type: "number" });
  const protein = el("input", { placeholder: "Protein (g)", type: "number" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!food.value.trim()) return;
    state.nutrition.unshift({ id: uid(), date: todayStr(), food: food.value.trim(),
      calories: Number(calories.value) || 0, protein: Number(protein.value) || 0 });
    saveState(); render();
  }}, "Add");

  view.appendChild(el("div", { class: "form-row" }, [food, calories, protein, addBtn]));

  const list = el("div", { class: "list" });
  if (!state.nutrition.length) {
    list.appendChild(el("div", { class: "empty" }, "No meals logged yet."));
  } else {
    for (const n of state.nutrition) {
      list.appendChild(el("div", { class: "list-item" }, [
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, n.food),
          el("div", { class: "li-sub" }, `${n.calories} kcal · ${n.protein}g protein · ${n.date}`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.nutrition = state.nutrition.filter(x => x.id !== n.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

// ---------- Habits ----------
function renderHabits() {
  view.appendChild(el("h1", { class: "page-title" }, "Habits"));
  view.appendChild(el("p", { class: "page-sub" }, "Track daily disciplines and streaks."));

  const name = el("input", { placeholder: "New habit (e.g. Meditate)" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!name.value.trim()) return;
    state.habits.unshift({ id: uid(), name: name.value.trim(), logs: {} });
    saveState(); render();
  }}, "Add");
  view.appendChild(el("div", { class: "form-row" }, [name, addBtn]));

  const list = el("div", { class: "list" });
  if (!state.habits.length) {
    list.appendChild(el("div", { class: "empty" }, "No habits yet — add one above."));
  } else {
    for (const h of state.habits) {
      const today = todayStr();
      const done = !!h.logs[today];
      const streak = computeStreak(h);
      list.appendChild(el("div", { class: "list-item" }, [
        el("button", { class: `habit-check ${done ? "done" : ""}`, onclick: () => {
          h.logs[today] = !h.logs[today]; saveState(); render();
        }}, done ? "✓" : ""),
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, h.name),
          el("div", { class: "streak" }, `${streak} day streak`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.habits = state.habits.filter(x => x.id !== h.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

function computeStreak(habit) {
  let streak = 0;
  let d = new Date();
  while (true) {
    const key = d.toISOString().slice(0, 10);
    if (habit.logs[key]) { streak++; d.setDate(d.getDate() - 1); }
    else break;
  }
  return streak;
}

// ---------- Planner (Time-Blocking) ----------
function renderPlanner() {
  view.appendChild(el("h1", { class: "page-title" }, "Time-Blocking"));
  view.appendChild(el("p", { class: "page-sub" }, "Plan today's schedule around what matters."));

  const title = el("input", { placeholder: "Block title (e.g. Deep work)" });
  const start = el("input", { type: "time" });
  const end = el("input", { type: "time" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!title.value.trim()) return;
    state.blocks.unshift({ id: uid(), date: todayStr(), title: title.value.trim(),
      start: start.value || "--:--", end: end.value || "--:--" });
    saveState(); render();
  }}, "Add");
  view.appendChild(el("div", { class: "form-row" }, [title, start, end, addBtn]));

  const todayBlocks = state.blocks.filter(b => b.date === todayStr())
    .sort((a, b) => a.start.localeCompare(b.start));

  const list = el("div", { class: "list" });
  if (!todayBlocks.length) {
    list.appendChild(el("div", { class: "empty" }, "No time blocks scheduled for today."));
  } else {
    for (const b of todayBlocks) {
      list.appendChild(el("div", { class: "list-item" }, [
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, b.title),
          el("div", { class: "li-sub" }, `${b.start} – ${b.end}`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.blocks = state.blocks.filter(x => x.id !== b.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

// ---------- Body ----------
function renderBody() {
  view.appendChild(el("h1", { class: "page-title" }, "Body Tracking"));
  view.appendChild(el("p", { class: "page-sub" }, "Log weight and measurements over time."));

  const weight = el("input", { placeholder: "Weight (kg)", type: "number" });
  const waist = el("input", { placeholder: "Waist (cm)", type: "number" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!weight.value) return;
    state.body.unshift({ id: uid(), date: todayStr(), weightKg: Number(weight.value) || 0, waistCm: Number(waist.value) || 0 });
    saveState(); render();
  }}, "Add");
  view.appendChild(el("div", { class: "form-row" }, [weight, waist, addBtn]));

  const list = el("div", { class: "list" });
  if (!state.body.length) {
    list.appendChild(el("div", { class: "empty" }, "No body measurements logged yet."));
  } else {
    for (const b of state.body) {
      list.appendChild(el("div", { class: "list-item" }, [
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, `${b.weightKg} kg`),
          el("div", { class: "li-sub" }, `Waist ${b.waistCm}cm · ${b.date}`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.body = state.body.filter(x => x.id !== b.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

render();
