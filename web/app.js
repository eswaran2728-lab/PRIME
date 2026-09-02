// Remix PRIME — local-first web app. All data lives in localStorage.
const STORE_KEY = "prime.v1";
const todayStr = () => new Date().toISOString().slice(0, 10);

const defaultState = () => ({
  workouts: [],   // sessions: {id, date, exercises: [{name, sets: [{weightKg, reps, rpe}]}], durationMin}
  nutrition: [],  // {id, date, food, calories, protein}
  habits: [],     // {id, name, logs: {date: true}}
  blocks: [],     // {id, date, title, start, end, category}
  body: [],       // {id, date, weightKg, waistCm}
  chat: [],       // {role: "user"|"assistant", text}
  activeSession: null, // {startedAt, exercises: [{name, sets: [{weightKg, reps, rpe, completed}]}]}
  profile: null,  // {heightCm, weightKg, age, gender, activityLevel, goalCalories, proteinGoal, carbsGoal, fatGoal, waterGoalMl}
  workoutPlan: null, // {goal, days: {Mon:[{exercise,sets,reps}], ...}}
  water: [],      // {id, date, amountMl}
});

function loadState() {
  try {
    const raw = localStorage.getItem(STORE_KEY);
    if (!raw) return defaultState();
    const parsed = JSON.parse(raw);
    const merged = { ...defaultState(), ...parsed };
    merged.workouts = migrateWorkouts(merged.workouts);
    return merged;
  } catch {
    return defaultState();
  }
}

function saveState() {
  localStorage.setItem(STORE_KEY, JSON.stringify(state));
}

function migrateWorkouts(workouts) {
  // Old flat entries {id,date,exercise,sets,reps,weightKg} -> session per date.
  if (!workouts.some(w => !w.exercises)) return workouts;
  const byDate = {};
  for (const w of workouts) {
    if (w.exercises) { (byDate[w.date] ??= { id: w.id, date: w.date, exercises: [] }).exercises.push(...w.exercises); continue; }
    const session = (byDate[w.date] ??= { id: uid(), date: w.date, exercises: [] });
    session.exercises.push({ name: w.exercise, sets: [{ weightKg: w.weightKg || 0, reps: w.reps || 0, rpe: null }] });
  }
  return Object.values(byDate).sort((a, b) => b.date.localeCompare(a.date));
}

const uid = () => Math.random().toString(36).slice(2, 10);
let state = loadState();

// ---------- PRIME score ----------
// Simplified, deterministic sub-scores over the last 7 days, weighted to 100.
function computeScore(endDate) {
  const end = endDate ? new Date(endDate) : new Date();
  const days = [...Array(7)].map((_, i) => {
    const d = new Date(end);
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
    progress: renderProgress,
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
let exerciseSearchQuery = "";
let restRemaining = 0;
let restInterval = null;
let progressExercise = null;

function estOneRM(weightKg, reps) {
  return Math.round(weightKg * (1 + reps / 30));
}

// All past sets for an exercise, oldest -> newest: {date, weightKg, reps}
function exerciseHistory(name) {
  const out = [];
  for (const s of state.workouts) {
    const ex = s.exercises.find(e => e.name === name);
    if (ex) for (const set of ex.sets) out.push({ date: s.date, ...set });
  }
  return out.sort((a, b) => a.date.localeCompare(b.date));
}

function bestSetLabel(name) {
  const hist = exerciseHistory(name);
  if (!hist.length) return null;
  const last = hist[hist.length - 1];
  return `Last: ${last.weightKg}kg × ${last.reps}`;
}

function exercisePR(name) {
  const hist = exerciseHistory(name);
  if (!hist.length) return 0;
  return Math.max(...hist.map(s => estOneRM(s.weightKg, s.reps)));
}

function startSession(prefill) {
  state.activeSession = {
    startedAt: new Date().toISOString(),
    exercises: prefill || [],
  };
  saveState();
}

function renderWorkout() {
  view.appendChild(el("h1", { class: "page-title" }, "Workout"));
  view.appendChild(el("p", { class: "page-sub" }, "Log sets like Strong — track weight, reps, PRs, and rest between sets."));

  if (state.activeSession) {
    view.appendChild(renderActiveSession());
  } else {
    view.appendChild(renderPlanGenerator());
    if (state.workoutPlan) view.appendChild(renderPlanDisplay());
    view.appendChild(renderStartWorkoutCard());
  }

  view.appendChild(renderExerciseProgress());
  view.appendChild(renderWorkoutHistory());
}

function renderStartWorkoutCard() {
  const card = el("div", { class: "card section" });
  card.appendChild(el("h3", {}, "Start a workout"));
  card.appendChild(el("button", { class: "btn", onclick: () => { startSession([]); render(); } }, "Start empty workout"));
  return card;
}

function exercisePicker(onPick) {
  const wrap = el("div", { class: "search-wrap" });
  const input = el("input", { placeholder: "Search exercise (e.g. Squat, Bench)" });
  const results = el("div", { class: "search-results" });
  results.style.display = "none";
  input.addEventListener("input", () => {
    const q = input.value.trim().toLowerCase();
    results.innerHTML = "";
    if (!q) { results.style.display = "none"; return; }
    const matches = (typeof EXERCISE_DB !== "undefined" ? EXERCISE_DB : [])
      .filter(e => e.name.toLowerCase().includes(q)).slice(0, 8);
    if (!matches.length) { results.style.display = "none"; return; }
    for (const e of matches) {
      results.appendChild(el("div", { class: "search-result-item", onclick: () => {
        onPick(e.name); input.value = ""; results.style.display = "none"; render();
      }}, [
        el("div", { class: "search-result-name" }, e.name),
        el("div", { class: "search-result-sub" }, e.group),
      ]));
    }
    results.style.display = "block";
  });
  wrap.appendChild(input); wrap.appendChild(results);
  return wrap;
}

function renderActiveSession() {
  const s = state.activeSession;
  const card = el("div", { class: "card section" });
  const elapsedMin = Math.max(0, Math.round((Date.now() - new Date(s.startedAt).getTime()) / 60000));
  card.appendChild(el("div", { class: "section-head" }, [
    el("h3", {}, `Active workout · ${elapsedMin} min`),
    el("button", { class: "btn", onclick: () => {
      const totalSets = s.exercises.reduce((n, e) => n + e.sets.length, 0);
      if (!totalSets) { state.activeSession = null; saveState(); render(); return; }
      state.workouts.unshift({
        id: uid(), date: todayStr(),
        exercises: s.exercises.filter(e => e.sets.length),
        durationMin: elapsedMin,
      });
      state.activeSession = null;
      saveState(); render();
    }}, "Finish workout"),
  ]));

  for (const ex of s.exercises) {
    const exBlock = el("div", { class: "section" });
    const last = bestSetLabel(ex.name);
    exBlock.appendChild(el("div", { class: "section-head" }, [
      el("h2", {}, ex.name),
      last ? el("span", { class: "dim" }, last) : null,
    ]));

    const list = el("div", { class: "list" });
    ex.sets.forEach((set, i) => {
      const weight = el("input", { type: "number", value: set.weightKg, placeholder: "kg", style: "max-width:90px" });
      const reps = el("input", { type: "number", value: set.reps, placeholder: "reps", style: "max-width:90px" });
      const pr = exercisePR(ex.name);
      const isPr = set.weightKg && set.reps && estOneRM(set.weightKg, set.reps) > pr && pr > 0;
      weight.addEventListener("change", () => { set.weightKg = Number(weight.value) || 0; saveState(); });
      reps.addEventListener("change", () => { set.reps = Number(reps.value) || 0; saveState(); });
      list.appendChild(el("div", { class: "list-item" }, [
        el("div", { class: "li-main" }, [
          el("div", { class: "li-title" }, `Set ${i + 1}${isPr ? " 🏆 PR" : ""}`),
        ]),
        el("div", { class: "li-actions" }, [weight, el("span", { class: "dim" }, "×"), reps,
          el("button", { class: "btn-ghost", onclick: () => {
            ex.sets.splice(i, 1); saveState(); render();
          }}, "Remove"),
        ]),
      ]));
    });
    exBlock.appendChild(list);
    exBlock.appendChild(el("div", { class: "form-row" }, [
      el("button", { class: "btn-ghost", onclick: () => {
        const lastSet = ex.sets[ex.sets.length - 1];
        ex.sets.push({ weightKg: lastSet?.weightKg || 0, reps: lastSet?.reps || 0, rpe: null });
        saveState(); render();
      }}, "+ Add set"),
    ]));
    card.appendChild(exBlock);
  }

  card.appendChild(exercisePicker(name => {
    s.exercises.push({ name, sets: [{ weightKg: 0, reps: 0, rpe: null }] });
    saveState();
  }));

  card.appendChild(renderRestTimer());
  return card;
}

function renderRestTimer() {
  const wrap = el("div", { class: "card section" });
  wrap.appendChild(el("h3", {}, "Rest timer"));
  if (restRemaining > 0) {
    wrap.appendChild(el("div", { class: "big" }, `${restRemaining}s`));
  } else {
    const row = el("div", { class: "water-row" });
    for (const secs of [60, 90, 120, 180]) {
      row.appendChild(el("button", { class: "water-btn", onclick: () => {
        restRemaining = secs;
        clearInterval(restInterval);
        restInterval = setInterval(() => {
          restRemaining -= 1;
          if (restRemaining <= 0) { clearInterval(restInterval); restRemaining = 0; }
          if (activeTab === "workout") render();
        }, 1000);
        render();
      }}, `${secs}s`));
    }
    wrap.appendChild(row);
  }
  return wrap;
}

function renderExerciseProgress() {
  const card = el("div", { class: "card section" });
  card.appendChild(el("h3", {}, "Exercise progress"));
  const names = [...new Set(state.workouts.flatMap(s => s.exercises.map(e => e.name)))];
  if (!names.length) {
    card.appendChild(el("div", { class: "empty" }, "Log a workout to see progress charts here."));
    return card;
  }
  if (!progressExercise || !names.includes(progressExercise)) progressExercise = names[0];
  const select = el("select", {}, names.map(n => el("option", { value: n }, n)));
  select.value = progressExercise;
  select.addEventListener("change", () => { progressExercise = select.value; render(); });
  card.appendChild(el("div", { class: "form-row" }, select));

  const hist = exerciseHistory(progressExercise);
  const byDate = {};
  for (const h of hist) {
    const rm = estOneRM(h.weightKg, h.reps);
    byDate[h.date] = Math.max(byDate[h.date] || 0, rm);
  }
  const points = Object.keys(byDate).sort().map(d => ({ v: byDate[d] }));
  const canvas = el("canvas", { class: "chart" });
  card.appendChild(canvas);
  card.appendChild(el("div", { class: "dim" }, `Estimated 1RM (Epley formula) · PR: ${exercisePR(progressExercise)}kg`));
  requestAnimationFrame(() => drawLineChart(canvas, points, "#ff5a3c"));
  return card;
}

function renderWorkoutHistory() {
  const card = el("div", { class: "section" });
  card.appendChild(el("div", { class: "section-head" }, el("h2", {}, "History")));
  if (!state.workouts.length) {
    card.appendChild(el("div", { class: "empty" }, "No workouts logged yet."));
    return card;
  }
  const list = el("div", { class: "list" });
  for (const s of state.workouts) {
    const volume = s.exercises.reduce((v, e) => v + e.sets.reduce((sv, set) => sv + set.weightKg * set.reps, 0), 0);
    const setCount = s.exercises.reduce((n, e) => n + e.sets.length, 0);
    list.appendChild(el("div", { class: "list-item" }, [
      el("div", { class: "li-main" }, [
        el("div", { class: "li-title" }, `${s.date} · ${s.exercises.length} exercises`),
        el("div", { class: "li-sub" }, `${setCount} sets · ${Math.round(volume)}kg total volume${s.durationMin ? ` · ${s.durationMin} min` : ""}`),
      ]),
      el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
        state.workouts = state.workouts.filter(x => x.id !== s.id); saveState(); render();
      }}, "Delete")),
    ]));
  }
  card.appendChild(list);
  return card;
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
        startSession(exercises.map(e => ({
          name: e.exercise,
          sets: Array.from({ length: e.sets || 1 }, () => ({ weightKg: 0, reps: e.reps || 0, rpe: null })),
        })));
        render();
      }}, "Start this workout")),
    ]);
    card.appendChild(row);
  }
  return card;
}

// ---------- Nutrition ----------
let foodPending = false;
let selectedMealType = "breakfast";
let pendingFoodEntry = null; // {name, calories, protein, carbs, fat} chosen from search, awaiting quantity confirm
const MEAL_TYPES = ["breakfast", "lunch", "dinner", "snack"];
const MEAL_LABELS = { breakfast: "Breakfast", lunch: "Lunch", dinner: "Dinner", snack: "Snack" };

function todayTotals() {
  const entries = state.nutrition.filter(n => n.date === todayStr());
  return entries.reduce((acc, n) => {
    acc.calories += n.calories || 0;
    acc.protein += n.protein || 0;
    acc.carbs += n.carbs || 0;
    acc.fat += n.fat || 0;
    return acc;
  }, { calories: 0, protein: 0, carbs: 0, fat: 0 });
}

function ringSvg(pct, color, size = 64) {
  const r = (size - 8) / 2;
  const c = 2 * Math.PI * r;
  const offset = c - Math.min(1, pct) * c;
  const svgNs = "http://www.w3.org/2000/svg";
  const svg = document.createElementNS(svgNs, "svg");
  svg.setAttribute("width", size); svg.setAttribute("height", size);
  const bg = document.createElementNS(svgNs, "circle");
  bg.setAttribute("cx", size / 2); bg.setAttribute("cy", size / 2); bg.setAttribute("r", r);
  bg.setAttribute("fill", "none"); bg.setAttribute("stroke", "#232833"); bg.setAttribute("stroke-width", 6);
  const fg = document.createElementNS(svgNs, "circle");
  fg.setAttribute("cx", size / 2); fg.setAttribute("cy", size / 2); fg.setAttribute("r", r);
  fg.setAttribute("fill", "none"); fg.setAttribute("stroke", color); fg.setAttribute("stroke-width", 6);
  fg.setAttribute("stroke-linecap", "round");
  fg.setAttribute("stroke-dasharray", `${c}`);
  fg.setAttribute("stroke-dashoffset", `${offset}`);
  fg.setAttribute("transform", `rotate(-90 ${size / 2} ${size / 2})`);
  svg.appendChild(bg); svg.appendChild(fg);
  return svg;
}

function renderNutrition() {
  view.appendChild(el("h1", { class: "page-title" }, "Nutrition"));
  view.appendChild(el("p", { class: "page-sub" }, "Log meals by search or photo, track water, and hit your macro goals."));

  const p = state.profile;
  const totals = todayTotals();

  // Calories + macro rings
  const summaryCard = el("div", { class: "card section" });
  summaryCard.appendChild(el("h3", {}, "Today"));
  if (p?.goalCalories) {
    const pct = Math.min(100, Math.round((totals.calories / p.goalCalories) * 100));
    summaryCard.appendChild(el("div", { class: "big" }, `${totals.calories} / ${p.goalCalories} kcal`));
    summaryCard.appendChild(el("div", { class: "bar-track" }, el("div", { class: "bar-fill", style: `width:${pct}%` })));

    const rings = el("div", { class: "rings-row" });
    const macros = [
      ["Protein", totals.protein, p.proteinGoal, "#ff5a3c"],
      ["Carbs", totals.carbs, p.carbsGoal, "#ffb84c"],
      ["Fat", totals.fat, p.fatGoal, "#3ddc97"],
    ];
    for (const [label, val, goal, color] of macros) {
      const wrap = el("div", { class: "ring-wrap" });
      wrap.appendChild(ringSvg(goal ? val / goal : 0, color));
      wrap.appendChild(el("div", { class: "ring-val" }, `${Math.round(val)}/${goal || "-"}g`));
      wrap.appendChild(el("div", { class: "ring-label" }, label));
      rings.appendChild(wrap);
    }
    summaryCard.appendChild(rings);
  } else {
    summaryCard.appendChild(el("div", { class: "empty" }, "Set your Profile to unlock personalized calorie & macro goals."));
  }
  view.appendChild(summaryCard);

  // Water tracker
  const waterGoal = p?.waterGoalMl || 2500;
  const waterToday = state.water.filter(w => w.date === todayStr()).reduce((s, w) => s + w.amountMl, 0);
  const waterPct = Math.min(100, Math.round((waterToday / waterGoal) * 100));
  const waterCard = el("div", { class: "card section" });
  waterCard.appendChild(el("h3", {}, "Water"));
  waterCard.appendChild(el("div", { class: "big" }, `${(waterToday / 1000).toFixed(2)}L / ${(waterGoal / 1000).toFixed(1)}L`));
  waterCard.appendChild(el("div", { class: "bar-track" }, el("div", { class: "bar-fill", style: `width:${waterPct}%` })));
  const waterRow = el("div", { class: "water-row" });
  for (const ml of [200, 250, 500, 1000]) {
    waterRow.appendChild(el("button", { class: "water-btn", onclick: () => {
      state.water.unshift({ id: uid(), date: todayStr(), amountMl: ml });
      saveState(); render();
    }}, `+${ml}ml`));
  }
  waterRow.appendChild(el("button", { class: "btn-ghost", onclick: () => {
    state.water = state.water.filter(w => w.date !== todayStr());
    saveState(); render();
  }}, "Reset today"));
  waterCard.appendChild(waterRow);
  view.appendChild(waterCard);

  // Meal type selector
  const mealCard = el("div", { class: "card section" });
  mealCard.appendChild(el("h3", {}, "Log a meal"));
  const mealRow = el("div", { class: "form-row" });
  for (const mt of MEAL_TYPES) {
    mealRow.appendChild(el("button", {
      class: "btn-ghost", style: selectedMealType === mt ? "border-color:#ff5a3c;color:#f2f4f7" : "",
      onclick: () => { selectedMealType = mt; render(); },
    }, MEAL_LABELS[mt]));
  }
  mealCard.appendChild(mealRow);

  // Food search
  const searchWrap = el("div", { class: "search-wrap" });
  const searchInput = el("input", { placeholder: "Search a food (e.g. banana, rice, paneer)" });
  const resultsBox = el("div", { class: "search-results" });
  resultsBox.style.display = "none";
  searchInput.addEventListener("input", () => {
    const q = searchInput.value.trim().toLowerCase();
    resultsBox.innerHTML = "";
    if (!q) { resultsBox.style.display = "none"; return; }
    const matches = (typeof FOOD_DB !== "undefined" ? FOOD_DB : [])
      .filter(f => f.name.toLowerCase().includes(q)).slice(0, 8);
    if (!matches.length) { resultsBox.style.display = "none"; return; }
    for (const f of matches) {
      resultsBox.appendChild(el("div", { class: "search-result-item", onclick: () => {
        pendingFoodEntry = { ...f, quantity: 1 };
        searchInput.value = "";
        resultsBox.style.display = "none";
        render();
      }}, [
        el("div", { class: "search-result-name" }, f.name),
        el("div", { class: "search-result-sub" }, `${f.serving} · ${f.calories} kcal`),
      ]));
    }
    resultsBox.style.display = "block";
  });
  searchWrap.appendChild(searchInput);
  searchWrap.appendChild(resultsBox);
  mealCard.appendChild(el("div", { class: "form-row" }, searchWrap));

  if (pendingFoodEntry) {
    const qty = el("input", { type: "number", value: pendingFoodEntry.quantity, step: "0.5", style: "max-width:90px" });
    const confirmBtn = el("button", { class: "btn", onclick: () => {
      const q = Number(qty.value) || 1;
      state.nutrition.unshift({
        id: uid(), date: todayStr(), mealType: selectedMealType,
        food: `${pendingFoodEntry.name} (${q}×)`,
        calories: Math.round(pendingFoodEntry.calories * q),
        protein: Math.round(pendingFoodEntry.protein * q * 10) / 10,
        carbs: Math.round(pendingFoodEntry.carbs * q * 10) / 10,
        fat: Math.round(pendingFoodEntry.fat * q * 10) / 10,
      });
      pendingFoodEntry = null;
      saveState(); render();
    }}, "Add");
    const cancelBtn = el("button", { class: "btn-ghost", onclick: () => { pendingFoodEntry = null; render(); }}, "Cancel");
    mealCard.appendChild(el("div", { class: "list-item" }, [
      el("div", { class: "li-main" }, [
        el("div", { class: "li-title" }, pendingFoodEntry.name),
        el("div", { class: "li-sub" }, `${pendingFoodEntry.serving} · ${pendingFoodEntry.calories} kcal each`),
      ]),
      el("div", { class: "li-actions" }, [el("span", { class: "dim" }, "×"), qty, confirmBtn, cancelBtn]),
    ]));
  }

  // Photo logging
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
      state.nutrition.unshift({ id: uid(), date: todayStr(), mealType: selectedMealType, food: data.food,
        calories: data.calories, protein: data.protein, carbs: data.carbs, fat: data.fat });
      saveState();
      status.textContent = "";
    } catch (err) {
      status.textContent = "Couldn't analyze photo: " + err.message;
    } finally {
      foodPending = false; render();
    }
  });
  mealCard.appendChild(el("p", { class: "dim" }, "or snap a photo — AI estimates it:"));
  mealCard.appendChild(el("div", { class: "form-row" }, fileInput));
  mealCard.appendChild(status);

  const food = el("input", { placeholder: "Or type manually: food name" });
  const calories = el("input", { placeholder: "Calories", type: "number" });
  const protein = el("input", { placeholder: "Protein (g)", type: "number" });
  const addBtn = el("button", { class: "btn", onclick: () => {
    if (!food.value.trim()) return;
    state.nutrition.unshift({ id: uid(), date: todayStr(), mealType: selectedMealType, food: food.value.trim(),
      calories: Number(calories.value) || 0, protein: Number(protein.value) || 0 });
    saveState(); render();
  }}, "Add");
  mealCard.appendChild(el("div", { class: "form-row" }, [food, calories, protein, addBtn]));
  view.appendChild(mealCard);

  // Today's log, grouped by meal
  const todayEntries = state.nutrition.filter(n => n.date === todayStr());
  const logCard = el("div", { class: "section" });
  logCard.appendChild(el("div", { class: "section-head" }, el("h2", {}, "Today's log")));
  if (!todayEntries.length) {
    logCard.appendChild(el("div", { class: "empty" }, "Nothing logged yet today."));
  } else {
    for (const mt of MEAL_TYPES) {
      const items = todayEntries.filter(n => (n.mealType || "snack") === mt);
      if (!items.length) continue;
      const group = el("div", { class: "meal-group" });
      group.appendChild(el("h4", {}, MEAL_LABELS[mt]));
      const list = el("div", { class: "list" });
      for (const n of items) {
        list.appendChild(el("div", { class: "list-item" }, [
          el("div", { class: "li-main" }, [
            el("div", { class: "li-title" }, n.food),
            el("div", { class: "li-sub" }, `${n.calories} kcal · ${n.protein}g P${n.carbs != null ? ` · ${n.carbs}g C · ${n.fat}g F` : ""}`),
          ]),
          el("div", { class: "li-actions" }, el("button", { class: "btn-ghost", onclick: () => {
            state.nutrition = state.nutrition.filter(x => x.id !== n.id); saveState(); render();
          }}, "Delete")),
        ]));
      }
      group.appendChild(list);
      logCard.appendChild(group);
    }
  }
  view.appendChild(logCard);
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

function computeMacroGoals(calories) {
  // Standard 30/40/30 protein/carb/fat split.
  return {
    proteinGoal: Math.round((calories * 0.30) / 4),
    carbsGoal: Math.round((calories * 0.40) / 4),
    fatGoal: Math.round((calories * 0.30) / 9),
  };
}

function computeWaterGoalMl(weightKg) {
  return weightKg ? Math.round(weightKg * 35) : 2500;
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
    Object.assign(profile, computeMacroGoals(profile.goalCalories || 2000));
    profile.waterGoalMl = computeWaterGoalMl(profile.weightKg);
    state.profile = profile;
    saveState(); render();
  }}, "Save profile");

  view.appendChild(el("div", { class: "form-row" }, [height, weight, age, gender, activity]));
  view.appendChild(el("div", { class: "form-row" }, saveBtn));

  if (state.profile && state.profile.goalCalories) {
    const bmi = computeBmi(state.profile);
    view.appendChild(el("div", { class: "grid" }, [
      statCard("BMI", String(bmi), bmiLabel(bmi)),
      statCard("Goal calories", String(state.profile.goalCalories), "kcal/day maintenance"),
      statCard("Macro goals", `${state.profile.proteinGoal}P / ${state.profile.carbsGoal}C / ${state.profile.fatGoal}F`, "grams/day"),
      statCard("Water goal", `${(state.profile.waterGoalMl / 1000).toFixed(1)}L`, "per day"),
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

// ---------- Progress charts ----------
function drawLineChart(canvas, points, color) {
  const ctx = canvas.getContext("2d");
  const dpr = window.devicePixelRatio || 1;
  const w = canvas.clientWidth, h = canvas.clientHeight;
  canvas.width = w * dpr; canvas.height = h * dpr;
  ctx.scale(dpr, dpr);
  ctx.clearRect(0, 0, w, h);

  if (!points.length) {
    ctx.fillStyle = "#9aa4b2"; ctx.font = "13px sans-serif";
    ctx.fillText("Not enough data yet", 12, h / 2);
    return;
  }
  const vals = points.map(p => p.v);
  const min = Math.min(...vals), max = Math.max(...vals);
  const pad = 20;
  const range = max - min || 1;
  const stepX = points.length > 1 ? (w - pad * 2) / (points.length - 1) : 0;

  ctx.beginPath();
  points.forEach((p, i) => {
    const x = pad + i * stepX;
    const y = h - pad - ((p.v - min) / range) * (h - pad * 2);
    if (i === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
  });
  ctx.strokeStyle = color; ctx.lineWidth = 2.5; ctx.lineJoin = "round"; ctx.stroke();

  points.forEach((p, i) => {
    const x = pad + i * stepX;
    const y = h - pad - ((p.v - min) / range) * (h - pad * 2);
    ctx.beginPath(); ctx.arc(x, y, 3, 0, Math.PI * 2);
    ctx.fillStyle = color; ctx.fill();
  });
}

function lastNDates(n) {
  return [...Array(n)].map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() - (n - 1 - i));
    return d.toISOString().slice(0, 10);
  });
}

function chartCard(title, points, color, unit) {
  const card = el("div", { class: "card section" });
  card.appendChild(el("h3", {}, title));
  const canvas = el("canvas", { class: "chart" });
  card.appendChild(canvas);
  if (points.length) {
    const last = points[points.length - 1].v;
    card.appendChild(el("div", { class: "dim" }, `Latest: ${last}${unit || ""}`));
  }
  requestAnimationFrame(() => drawLineChart(canvas, points, color));
  return card;
}

function renderProgress() {
  view.appendChild(el("h1", { class: "page-title" }, "Progress"));
  view.appendChild(el("p", { class: "page-sub" }, "Trends across weight, calories, and your PRIME score."));

  const weightPoints = [...state.body].filter(b => b.weightKg).sort((a, b) => a.date.localeCompare(b.date))
    .slice(-14).map(b => ({ v: b.weightKg }));
  view.appendChild(chartCard("Weight (kg)", weightPoints, "#ffb84c", "kg"));

  const days14 = lastNDates(14);
  const calPoints = days14.map(d => ({
    v: state.nutrition.filter(n => n.date === d).reduce((s, n) => s + (n.calories || 0), 0),
  }));
  view.appendChild(chartCard("Calories (last 14 days)", calPoints, "#ff5a3c", " kcal"));

  const scorePoints = days14.map(d => ({ v: computeScore(d).total }));
  view.appendChild(chartCard("PRIME Score (last 14 days)", scorePoints, "#3ddc97"));
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
    todayTotals: todayTotals(),
    waterTodayMl: state.water.filter(w => w.date === todayStr()).reduce((s, w) => s + w.amountMl, 0),
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
