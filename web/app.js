// Remix PRIME — local-first web app. All data lives in localStorage.
const STORE_KEY = "prime.v1";
const todayStr = () => new Date().toISOString().slice(0, 10);

const defaultState = () => ({
  workouts: [],   // {id, date, exercise, sets, reps, weightKg}
  nutrition: [],  // {id, date, food, calories, protein}
  habits: [],     // {id, name, logs: {date: true}}
  blocks: [],     // {id, date, title, start, end, category}
  body: [],       // {id, date, weightKg, waistCm}
  chat: [],       // {role: "user"|"assistant", text}
  profile: null,  // {heightCm, weightKg, age, gender, activityLevel, goalCalories}
  workoutPlan: null, // {goal, days: {Mon:[{exercise,sets,reps}], ...}}
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
    coach: renderCoach,
    profile: renderProfile,
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
const WEEKDAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
let planPending = false;
let selectedPlanDays = new Set();

function renderWorkout() {
  view.appendChild(el("h1", { class: "page-title" }, "Workout"));
  view.appendChild(el("p", { class: "page-sub" }, "Log your training sessions, or let AI build your week."));

  view.appendChild(renderPlanGenerator());
  if (state.workoutPlan) view.appendChild(renderPlanDisplay());

  const sectionHead = el("div", { class: "section-head" }, el("h2", {}, "Manual log"));
  view.appendChild(sectionHead);

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

function renderPlanGenerator() {
  const card = el("div", { class: "card section" });
  card.appendChild(el("h3", {}, "AI workout plan"));
  card.appendChild(el("p", { class: "dim" }, "Pick the days you can train and your goal — AI builds the week."));

  const dayRow = el("div", { class: "form-row" });
  for (const d of WEEKDAYS) {
    const btn = el("button", {
      class: `btn-ghost ${selectedPlanDays.has(d) ? "active" : ""}`,
      style: selectedPlanDays.has(d) ? "border-color:#ff5a3c;color:#f2f4f7" : "",
      onclick: () => {
        if (selectedPlanDays.has(d)) selectedPlanDays.delete(d); else selectedPlanDays.add(d);
        render();
      },
    }, d);
    dayRow.appendChild(btn);
  }
  card.appendChild(dayRow);

  const goal = el("input", { placeholder: "Goal (e.g. build muscle, lose fat, strength)" });
  const genBtn = el("button", { class: "btn", onclick: async () => {
    if (!selectedPlanDays.size) return;
    planPending = true; render();
    try {
      const resp = await fetch("/api/plan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          days: WEEKDAYS.filter(d => selectedPlanDays.has(d)),
          goal: goal.value.trim() || "general fitness",
          profile: state.profile,
        }),
      });
      const data = await resp.json();
      if (!resp.ok) throw new Error(data.error || "Request failed");
      state.workoutPlan = { goal: goal.value.trim() || "general fitness", days: data.plan };
      saveState();
    } catch (err) {
      alert("Couldn't generate plan: " + err.message);
    } finally {
      planPending = false; render();
    }
  }}, planPending ? "Generating..." : "Generate plan with AI");

  card.appendChild(el("div", { class: "form-row" }, [goal, genBtn]));
  return card;
}

function renderPlanDisplay() {
  const card = el("div", { class: "card section" });
  card.appendChild(el("h3", {}, `This week's plan — ${state.workoutPlan.goal}`));

  const todayIdx = (new Date().getDay() + 6) % 7; // Mon=0
  const todayKey = WEEKDAYS[todayIdx];

  for (const day of WEEKDAYS) {
    const exercises = state.workoutPlan.days?.[day];
    if (!exercises || !exercises.length) continue;
    const row = el("div", { class: "list-item" }, [
      el("div", { class: "li-main" }, [
        el("div", { class: "li-title" }, day + (day === todayKey ? " (today)" : "")),
        el("div", { class: "li-sub" }, exercises.map(e => `${e.exercise} ${e.sets}×${e.reps}`).join(", ")),
      ]),
      el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
        for (const e of exercises) {
          state.workouts.unshift({ id: uid(), date: todayStr(), exercise: e.exercise, sets: e.sets, reps: e.reps, weightKg: 0 });
        }
        saveState(); render();
      }}, "Log to today")),
    ]);
    card.appendChild(row);
  }
  return card;
}

// ---------- Nutrition ----------
let foodPending = false;

function renderNutrition() {
  view.appendChild(el("h1", { class: "page-title" }, "Nutrition"));
  view.appendChild(el("p", { class: "page-sub" }, "Log meals manually, or snap a photo and let AI estimate calories."));

  const goalCal = state.profile?.goalCalories;
  const todayCals = state.nutrition.filter(n => n.date === todayStr()).reduce((s, n) => s + n.calories, 0);
  if (goalCal) {
    const pct = Math.min(100, Math.round((todayCals / goalCal) * 100));
    const goalCard = el("div", { class: "card section" }, [
      el("h3", {}, "Today's calories"),
      el("div", { class: "big" }, `${todayCals} / ${goalCal} kcal`),
      el("div", { class: "bar-track" }, el("div", { class: "bar-fill", style: `width:${pct}%` })),
    ]);
    view.appendChild(goalCard);
  } else {
    view.appendChild(el("div", { class: "empty" }, "Set your height/weight/age in Profile to get a personalized goal calorie target."));
  }

  const photoCard = el("div", { class: "card section" });
  photoCard.appendChild(el("h3", {}, "Snap a meal photo"));
  const fileInput = el("input", { type: "file", accept: "image/*", capture: "environment" });
  const status = el("div", { class: "chat-error" });
  fileInput.addEventListener("change", async () => {
    const file = fileInput.files?.[0];
    if (!file) return;
    foodPending = true;
    status.textContent = "Analyzing photo...";
    try {
      const base64 = await fileToBase64(file);
      const resp = await fetch("/api/food", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ imageBase64: base64, mimeType: file.type || "image/jpeg" }),
      });
      const data = await resp.json();
      if (!resp.ok) throw new Error(data.error || "Request failed");
      state.nutrition.unshift({ id: uid(), date: todayStr(), food: data.food,
        calories: data.calories, protein: data.protein, carbs: data.carbs, fat: data.fat });
      saveState();
      status.textContent = "";
    } catch (err) {
      status.textContent = "Couldn't analyze photo: " + err.message;
    } finally {
      foodPending = false; render();
    }
  });
  photoCard.appendChild(el("div", { class: "form-row" }, fileInput));
  photoCard.appendChild(status);
  view.appendChild(photoCard);

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
          el("div", { class: "li-sub" }, `${n.calories} kcal · ${n.protein}g protein${n.carbs != null ? ` · ${n.carbs}g carbs · ${n.fat}g fat` : ""} · ${n.date}`),
        ]),
        el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
          state.nutrition = state.nutrition.filter(x => x.id !== n.id); saveState(); render();
        }}, "Delete")),
      ]));
    }
  }
  view.appendChild(list);
}

function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result).split(",")[1]);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
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

// ---------- Profile & goal calories ----------
const ACTIVITY_MULTIPLIERS = {
  sedentary: 1.2, light: 1.375, moderate: 1.55, active: 1.725, veryActive: 1.9,
};

function computeGoalCalories(p) {
  if (!p || !p.heightCm || !p.weightKg || !p.age) return null;
  const bmr = p.gender === "female"
    ? 10 * p.weightKg + 6.25 * p.heightCm - 5 * p.age - 161
    : 10 * p.weightKg + 6.25 * p.heightCm - 5 * p.age + 5;
  const mult = ACTIVITY_MULTIPLIERS[p.activityLevel] || 1.375;
  return Math.round(bmr * mult);
}

function computeBmi(p) {
  if (!p || !p.heightCm || !p.weightKg) return null;
  const m = p.heightCm / 100;
  return Math.round((p.weightKg / (m * m)) * 10) / 10;
}

function renderProfile() {
  view.appendChild(el("h1", { class: "page-title" }, "Profile"));
  view.appendChild(el("p", { class: "page-sub" }, "Used to calculate your BMI and daily goal calories — the AI coach and nutrition tracker use this."));

  const p = state.profile || {};
  const height = el("input", { placeholder: "Height (cm)", type: "number", value: p.heightCm || "" });
  const weight = el("input", { placeholder: "Weight (kg)", type: "number", value: p.weightKg || "" });
  const age = el("input", { placeholder: "Age", type: "number", value: p.age || "" });
  const gender = el("select", {}, [
    el("option", { value: "male" }, "Male"),
    el("option", { value: "female" }, "Female"),
  ]);
  gender.value = p.gender || "male";
  const activity = el("select", {}, [
    el("option", { value: "sedentary" }, "Sedentary (little exercise)"),
    el("option", { value: "light" }, "Light (1-3 days/wk)"),
    el("option", { value: "moderate" }, "Moderate (3-5 days/wk)"),
    el("option", { value: "active" }, "Active (6-7 days/wk)"),
    el("option", { value: "veryActive" }, "Very active (physical job/2x day)"),
  ]);
  activity.value = p.activityLevel || "light";

  const saveBtn = el("button", { class: "btn", onclick: () => {
    const profile = {
      heightCm: Number(height.value) || 0,
      weightKg: Number(weight.value) || 0,
      age: Number(age.value) || 0,
      gender: gender.value,
      activityLevel: activity.value,
    };
    profile.goalCalories = computeGoalCalories(profile);
    state.profile = profile;
    saveState(); render();
  }}, "Save profile");

  view.appendChild(el("div", { class: "form-row" }, [height, weight, age, gender, activity]));
  view.appendChild(el("div", { class: "form-row" }, saveBtn));

  if (state.profile && state.profile.goalCalories) {
    const bmi = computeBmi(state.profile);
    view.appendChild(el("div", { class: "grid" }, [
      statCard("BMI", String(bmi), bmiLabel(bmi)),
      statCard("Goal calories", String(state.profile.goalCalories), "kcal/day (maintenance, from BMR × activity)"),
    ]));
  }
}

function bmiLabel(bmi) {
  if (bmi == null) return "";
  if (bmi < 18.5) return "Underweight";
  if (bmi < 25) return "Normal";
  if (bmi < 30) return "Overweight";
  return "Obese";
}

// ---------- AI Coach ----------
function buildContextSnapshot() {
  const { total, subs } = computeScore();
  return {
    primeScore: total,
    scoreBreakdown: subs,
    profile: state.profile,
    workoutPlan: state.workoutPlan,
    recentWorkouts: state.workouts.slice(0, 5),
    recentNutrition: state.nutrition.slice(0, 5),
    habits: state.habits.map(h => ({ name: h.name, streak: computeStreak(h) })),
    todaysBlocks: state.blocks.filter(b => b.date === todayStr()),
    recentBody: state.body.slice(0, 3),
  };
}

let coachPending = false;

function renderCoach() {
  view.appendChild(el("h1", { class: "page-title" }, "AI Coach"));
  view.appendChild(el("p", { class: "page-sub" }, "Ask anything — your coach sees your logged data and personalizes its advice."));

  const windowEl = el("div", { class: "chat-window", id: "chatWindow" });
  if (!state.chat.length) {
    windowEl.appendChild(el("div", { class: "msg assistant" },
      "Hey — I'm your PRIME coach. Ask me about your training, nutrition, habits, or what to focus on today."));
  }
  for (const m of state.chat) {
    windowEl.appendChild(el("div", { class: `msg ${m.role}` }, m.text));
  }
  if (coachPending) {
    windowEl.appendChild(el("div", { class: "msg assistant pending" }, "Thinking..."));
  }
  view.appendChild(windowEl);
  windowEl.scrollTop = windowEl.scrollHeight;

  const input = el("input", { placeholder: "Ask your coach..." });
  input.addEventListener("keydown", e => { if (e.key === "Enter") send(); });
  const sendBtn = el("button", { class: "btn", onclick: send }, "Send");
  view.appendChild(el("div", { class: "chat-input-row" }, [input, sendBtn]));

  const errorEl = el("div", { class: "chat-error", id: "chatError" });
  view.appendChild(errorEl);

  async function send() {
    const text = input.value.trim();
    if (!text || coachPending) return;
    state.chat.push({ role: "user", text });
    saveState();
    coachPending = true;
    render();
    document.getElementById("chatWindow")?.scrollTo(0, 999999);

    try {
      const resp = await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: text,
          history: state.chat.slice(0, -1).map(m => ({ role: m.role, text: m.text })),
          context: buildContextSnapshot(),
        }),
      });
      const data = await resp.json();
      if (!resp.ok) throw new Error(data.error || "Request failed");
      state.chat.push({ role: "assistant", text: data.reply || "(no response)" });
    } catch (err) {
      const errEl = document.getElementById("chatError");
      if (errEl) errEl.textContent = "Coach unavailable: " + err.message;
    } finally {
      coachPending = false;
      saveState();
      render();
    }
  }
}

render();
