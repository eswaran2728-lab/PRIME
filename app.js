/**
 * PRIME OS — Interactive Web Client Logic
 * Offline-first personal self-mastery operating system.
 */

// Initial Default State
const DEFAULT_STATE = {
  score: 88,
  streak: 14,
  waterMl: 2750,
  waterTarget: 3500,
  calories: 2450,
  caloriesTarget: 2600,
  protein: 175,
  proteinTarget: 180,
  habits: [
    { id: 1, name: "Hydrate 1L with Electrolytes", category: "Physical", completed: true, streak: 14 },
    { id: 2, name: "Heavy Hypertrophy Push Session", category: "Physical", completed: true, streak: 14 },
    { id: 3, name: "3h Deep Focus Sprint (No Phone)", category: "Cognitive", completed: true, streak: 12 },
    { id: 4, name: "Hit 180g Protein Target", category: "Nutrition", completed: true, streak: 9 },
    { id: 5, name: "Evening Stoic Journal Calibration", category: "Mindset", completed: false, streak: 8 }
  ],
  workoutSets: [
    { id: 1, exercise: "Incline DB Press", set: 1, weight: 32, reps: 12, rpe: 8.0 },
    { id: 2, exercise: "Incline DB Press", set: 2, weight: 34, reps: 10, rpe: 8.5 },
    { id: 3, exercise: "Incline DB Press", set: 3, weight: 34, reps: 9, rpe: 9.0 },
    { id: 4, exercise: "Standing Cable Flyes", set: 1, weight: 15, reps: 15, rpe: 8.5 },
    { id: 5, exercise: "Overhead Tricep Extension", set: 1, weight: 28, reps: 12, rpe: 8.5 }
  ],
  meals: [
    { id: 1, time: "08:30 AM", name: "Whey Isolate + Oats & Berries", calories: 580, protein: 48 },
    { id: 2, time: "01:15 PM", name: "200g Grass-fed Beef, Jasmine Rice, Avocado", calories: 920, protein: 62 },
    { id: 3, time: "05:00 PM", name: "Greek Yogurt Bowl + Almond Butter", calories: 410, protein: 35 },
    { id: 4, time: "08:00 PM", name: "Grilled Salmon, Sweet Potato & Asparagus", calories: 540, protein: 30 }
  ],
  skills: [
    { name: "Fullstack Systems & Kotlin/Compose", level: 92, category: "Engineering" },
    { name: "Distributed Architectures & AI Integrations", level: 88, category: "Engineering" },
    { name: "High-Leverage Product Strategy", level: 85, category: "Leadership" },
    { name: "Financial Capital Allocation & Asset Scaling", level: 80, category: "Finance" }
  ],
  transactions: [
    { id: 1, date: "Today", desc: "Consulting Retainer Inflow", type: "income", amount: 4500 },
    { id: 2, date: "Yesterday", desc: "Index Fund Auto-Invest", type: "expense", amount: 1500 },
    { id: 3, date: "Sep 5", desc: "High-Protein Meal Prep & Groceries", type: "expense", amount: 220 }
  ],
  books: [
    { title: "Principles for Dealing with the Changing World Order", author: "Ray Dalio", pages: "480/576 (83%)", status: "Reading" },
    { title: "Deep Work: Rules for Focused Success", author: "Cal Newport", pages: "Complete", status: "Mastered" },
    { title: "Meditations (Hays Translation)", author: "Marcus Aurelius", pages: "Complete", status: "Mastered" }
  ]
};

// Load state from localStorage or default
let state = JSON.parse(localStorage.getItem('prime_os_state') || 'null') || DEFAULT_STATE;

function saveState() {
  localStorage.setItem('prime_os_state', JSON.stringify(state));
}

// Focus Timer State
let focusTimerInterval = null;
let focusSecondsRemaining = 50 * 60;
let isFocusRunning = false;
let distractionsCount = 0;

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) {
    window.lucide.createIcons();
  }

  setupNavigation();
  renderAllComponents();
  setupTimer();
  setupAI();
  setupModals();
});

// Navigation Handling
function setupNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  const screens = document.querySelectorAll('.tab-pane');
  const screenTitle = document.getElementById('currentScreenTitle');
  const sidebar = document.getElementById('sidebar');
  const openSidebarBtn = document.getElementById('openSidebarBtn');
  const closeSidebarBtn = document.getElementById('closeSidebarBtn');

  function switchTab(targetTab) {
    navItems.forEach(item => {
      const active = item.getAttribute('data-tab') === targetTab;
      item.classList.toggle('active', active);
    });

    screens.forEach(screen => {
      const active = screen.id === `tab-${targetTab}`;
      screen.classList.toggle('active', active);
    });

    if (screenTitle) {
      screenTitle.textContent = targetTab.charAt(0).toUpperCase() + targetTab.slice(1);
    }

    if (sidebar && window.innerWidth <= 900) {
      sidebar.classList.remove('open');
    }

    if (window.lucide) {
      window.lucide.createIcons();
    }
  }

  navItems.forEach(item => {
    item.addEventListener('click', (e) => {
      e.preventDefault();
      const targetTab = item.getAttribute('data-tab');
      switchTab(targetTab);
    });
  });

  if (openSidebarBtn && sidebar) {
    openSidebarBtn.addEventListener('click', () => sidebar.classList.add('open'));
  }
  if (closeSidebarBtn && sidebar) {
    closeSidebarBtn.addEventListener('click', () => sidebar.classList.remove('open'));
  }
}

// Render All UI Components
function renderAllComponents() {
  renderScore();
  renderHabits();
  renderWorkoutSets();
  renderMeals();
  renderSkills();
  renderFinance();
  renderBooks();
}

function renderScore() {
  const completedHabits = state.habits.filter(h => h.completed).length;
  const habitPoints = Math.round((completedHabits / state.habits.length) * 20);
  const totalScore = 20 + 20 + 20 + 20 + habitPoints; // 80 base + habits up to 20 = 80-100
  state.score = totalScore;
  saveState();

  const heroVal = document.getElementById('heroScoreVal');
  const sidebarVal = document.getElementById('sidebarScoreVal');
  const heroRing = document.getElementById('heroScoreRing');
  const sidebarArc = document.getElementById('sidebarScoreArc');

  if (heroVal) heroVal.textContent = totalScore;
  if (sidebarVal) sidebarVal.textContent = totalScore;

  if (heroRing) {
    // 314 circumference
    const offset = 314 - (314 * (totalScore / 100));
    heroRing.style.strokeDashoffset = offset;
  }
  if (sidebarArc) {
    sidebarArc.setAttribute('stroke-dasharray', `${totalScore}, 100`);
  }
}

function renderHabits() {
  const dashList = document.getElementById('dashboardHabitsList');
  const fullGrid = document.getElementById('fullHabitsGrid');
  const pendingBadge = document.getElementById('pendingHabitsBadge');

  const pendingCount = state.habits.filter(h => !h.completed).length;
  if (pendingBadge) pendingBadge.textContent = pendingCount;

  if (dashList) {
    dashList.innerHTML = state.habits.map(h => `
      <div class="habit-item ${h.completed ? 'completed' : ''}" onclick="toggleHabit(${h.id})">
        <div class="habit-checkbox">
          ${h.completed ? '✓' : ''}
        </div>
        <span class="habit-name">${h.name}</span>
        <span class="habit-streak">🔥 ${h.streak}d</span>
      </div>
    `).join('');
  }

  if (fullGrid) {
    fullGrid.innerHTML = state.habits.map(h => `
      <div class="card glass-card habit-item ${h.completed ? 'completed' : ''}" onclick="toggleHabit(${h.id})">
        <div class="habit-checkbox">
          ${h.completed ? '✓' : ''}
        </div>
        <div style="flex: 1;">
          <div class="habit-name">${h.name}</div>
          <span class="text-dim text-sm">${h.category} Pillar</span>
        </div>
        <span class="habit-streak">🔥 ${h.streak} days</span>
      </div>
    `).join('');
  }
}

window.toggleHabit = function(id) {
  state.habits = state.habits.map(h => {
    if (h.id === id) {
      return { ...h, completed: !h.completed, streak: !h.completed ? h.streak + 1 : Math.max(1, h.streak - 1) };
    }
    return h;
  });
  saveState();
  renderHabits();
  renderScore();
};

function renderWorkoutSets() {
  const tbody = document.getElementById('workoutSetsBody');
  if (!tbody) return;

  tbody.innerHTML = state.workoutSets.map((s, idx) => `
    <tr>
      <td><strong>${s.exercise}</strong></td>
      <td>Set ${s.set}</td>
      <td>${s.weight} kg</td>
      <td>${s.reps}</td>
      <td>${s.rpe}</td>
      <td><button class="btn btn-ghost btn-xs text-rose" onclick="deleteSet(${idx})">✕</button></td>
    </tr>
  `).join('');
}

window.deleteSet = function(index) {
  state.workoutSets.splice(index, 1);
  saveState();
  renderWorkoutSets();
};

const addSetBtn = document.getElementById('addSetBtn');
if (addSetBtn) {
  addSetBtn.addEventListener('click', () => {
    const exInput = document.getElementById('setExerciseInput');
    const wInput = document.getElementById('setWeightInput');
    const rInput = document.getElementById('setRepsInput');
    const rpeInput = document.getElementById('setRpeInput');

    const name = exInput.value.trim() || "Incline DB Press";
    const weight = parseFloat(wInput.value) || 30;
    const reps = parseInt(rInput.value) || 10;
    const rpe = parseFloat(rpeInput.value) || 8.0;

    const existingSets = state.workoutSets.filter(s => s.exercise === name).length;

    state.workoutSets.push({
      id: Date.now(),
      exercise: name,
      set: existingSets + 1,
      weight: weight,
      reps: reps,
      rpe: rpe
    });

    saveState();
    renderWorkoutSets();
    exInput.value = '';
  });
}

function renderMeals() {
  const list = document.getElementById('todayMealsList');
  if (!list) return;

  list.innerHTML = state.meals.map(m => `
    <div class="score-breakdown-row" style="padding: 10px 0;">
      <div>
        <strong>${m.name}</strong>
        <div class="text-dim text-sm">${m.time}</div>
      </div>
      <div>
        <span class="text-amber font-bold">${m.calories} kcal</span> · 
        <span class="text-cyan font-bold">${m.protein}g P</span>
      </div>
    </div>
  `).join('');
}

window.addWater = function(amount) {
  state.waterMl = Math.min(5000, (state.waterMl || 0) + amount);
  saveState();
  const text = document.getElementById('waterAmountText');
  if (text) text.innerHTML = `${state.waterMl.toLocaleString()} <span class="unit">/ 3,500 ml</span>`;
};

function renderSkills() {
  const container = document.getElementById('skillsGridContainer');
  if (!container) return;

  container.innerHTML = state.skills.map(s => `
    <div class="card glass-card mb-3">
      <div class="card-header">
        <strong>${s.name}</strong>
        <span class="text-primary font-bold">${s.level}%</span>
      </div>
      <div class="progress-bar-container">
        <div class="progress-bar-fill fill-cyan" style="width: ${s.level}%;"></div>
      </div>
    </div>
  `).join('');
}

function renderFinance() {
  const list = document.getElementById('financeTransactionsList');
  if (!list) return;

  list.innerHTML = state.transactions.map(t => `
    <div class="score-breakdown-row" style="padding: 10px 0;">
      <div>
        <strong>${t.desc}</strong>
        <div class="text-dim text-sm">${t.date}</div>
      </div>
      <div class="${t.type === 'income' ? 'text-emerald' : 'text-rose'} font-bold">
        ${t.type === 'income' ? '+' : '-'}$${t.amount.toLocaleString()}
      </div>
    </div>
  `).join('');
}

function renderBooks() {
  const container = document.getElementById('booksGridContainer');
  if (!container) return;

  container.innerHTML = state.books.map(b => `
    <div class="card glass-card mb-3">
      <div style="display: flex; justify-content: space-between;">
        <div>
          <h4>${b.title}</h4>
          <span class="text-dim text-sm">By ${b.author}</span>
        </div>
        <span class="badge ${b.status === 'Mastered' ? 'badge-success' : 'badge-focus'}">${b.status}</span>
      </div>
      <div class="mt-2 text-sm text-primary font-bold">${b.pages}</div>
    </div>
  `).join('');
}

// Timer Logic
function setupTimer() {
  const dashTimer = document.getElementById('dashTimerDisplay');
  const fullTimer = document.getElementById('fullTimerDisplay');
  const dashStart = document.getElementById('dashTimerStartBtn');
  const dashReset = document.getElementById('dashTimerResetBtn');
  const fullToggle = document.getElementById('fullTimerToggleBtn');
  const fullReset = document.getElementById('fullTimerResetBtn');
  const distBtn = document.getElementById('logDistractionBtn');
  const distVal = document.getElementById('distractionCountVal');

  function updateDisplays() {
    const mins = Math.floor(focusSecondsRemaining / 60).toString().padStart(2, '0');
    const secs = (focusSecondsRemaining % 60).toString().padStart(2, '0');
    const formatted = `${mins}:${secs}`;
    if (dashTimer) dashTimer.textContent = formatted;
    if (fullTimer) fullTimer.textContent = formatted;
  }

  function toggleTimer() {
    if (isFocusRunning) {
      clearInterval(focusTimerInterval);
      isFocusRunning = false;
      if (dashStart) dashStart.innerHTML = `<i data-lucide="play"></i><span>Resume</span>`;
      if (fullToggle) fullToggle.innerHTML = `<i data-lucide="play"></i><span>Resume</span>`;
    } else {
      isFocusRunning = true;
      if (dashStart) dashStart.innerHTML = `<i data-lucide="pause"></i><span>Pause</span>`;
      if (fullToggle) fullToggle.innerHTML = `<i data-lucide="pause"></i><span>Pause</span>`;

      focusTimerInterval = setInterval(() => {
        if (focusSecondsRemaining > 0) {
          focusSecondsRemaining--;
          updateDisplays();
        } else {
          clearInterval(focusTimerInterval);
          isFocusRunning = false;
          alert("⚡ Deep Focus Sprint Conquered! Take a 10-minute recovery walk.");
        }
      }, 1000);
    }
    if (window.lucide) window.lucide.createIcons();
  }

  function resetTimer(mins = 50) {
    clearInterval(focusTimerInterval);
    isFocusRunning = false;
    focusSecondsRemaining = mins * 60;
    updateDisplays();
    if (dashStart) dashStart.innerHTML = `<i data-lucide="play"></i><span>Start Sprint</span>`;
    if (fullToggle) fullToggle.innerHTML = `<i data-lucide="play"></i><span>Begin Sprint</span>`;
    if (window.lucide) window.lucide.createIcons();
  }

  if (dashStart) dashStart.addEventListener('click', toggleTimer);
  if (fullToggle) fullToggle.addEventListener('click', toggleTimer);
  if (dashReset) dashReset.addEventListener('click', () => resetTimer(50));
  if (fullReset) fullReset.addEventListener('click', () => resetTimer(50));

  if (distBtn && distVal) {
    distBtn.addEventListener('click', () => {
      distractionsCount++;
      distVal.textContent = distractionsCount;
    });
  }

  document.querySelectorAll('.focus-mode-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.focus-mode-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const time = parseInt(btn.getAttribute('data-time')) || 50;
      resetTimer(time);
    });
  });

  updateDisplays();
}

// AI Coach Simulator & Natural Language Estimation
function setupAI() {
  const sendBtn = document.getElementById('sendAiMessageBtn');
  const chatInput = document.getElementById('aiChatInput');
  const chatMessages = document.getElementById('aiChatMessages');
  const estimateBtn = document.getElementById('estimateFoodAiBtn');
  const foodQuery = document.getElementById('aiFoodQuery');
  const resultBox = document.getElementById('aiFoodResultBox');
  const statsBox = document.getElementById('aiFoodStats');

  if (sendBtn && chatInput && chatMessages) {
    sendBtn.addEventListener('click', () => {
      const query = chatInput.value.trim();
      if (!query) return;

      // Add user message
      const userBubble = document.createElement('div');
      userBubble.className = 'message-bubble message-user';
      userBubble.textContent = query;
      chatMessages.appendChild(userBubble);
      chatInput.value = '';

      // Generate AI response
      setTimeout(() => {
        const aiBubble = document.createElement('div');
        aiBubble.className = 'message-bubble message-assistant';
        aiBubble.innerHTML = `<strong>PRIME AI:</strong> ${generateAiInsight(query)}`;
        chatMessages.appendChild(aiBubble);
        chatMessages.scrollTop = chatMessages.scrollHeight;
      }, 500);
    });
  }

  if (estimateBtn && foodQuery && resultBox && statsBox) {
    estimateBtn.addEventListener('click', () => {
      const q = foodQuery.value.trim();
      if (!q) return;

      resultBox.classList.remove('hidden');
      statsBox.innerHTML = `
        <strong>Estimated Breakdown for "${q}":</strong><br>
        🔥 Calories: ~680 kcal &nbsp;|&nbsp; 🥩 Protein: ~54g &nbsp;|&nbsp; 🍚 Carbs: ~52g &nbsp;|&nbsp; 🥑 Fat: ~22g
      `;
    });
  }
}

function generateAiInsight(query) {
  const q = query.toLowerCase();
  if (q.includes('workout') || q.includes('exercise') || q.includes('training')) {
    return "Based on your progressive overload log, your pressing volume is on track. Ensure you execute a minimum of 2-3 warm-up sets before the 34kg top set, and maintain an RPE of 8.5 to prevent CNS burnout.";
  } else if (q.includes('diet') || q.includes('protein') || q.includes('nutrition') || q.includes('food')) {
    return "You have consumed 175g of protein out of your 180g target (97%). You only need 5g more to achieve 100% adherence. A light Greek yogurt snack or collagen peptides will close the gap perfectly.";
  } else if (q.includes('focus') || q.includes('procrastination') || q.includes('work')) {
    return "Initiate a 50-minute Deep Focus block immediately. Close all background browser tabs, engage airplane mode on mobile, and apply the single-tasking rule until the timer expires.";
  }
  return "Strategy acknowledged. Maintain relentless consistency across your 5 core pillars. What specific metric shall we calibrate next?";
}

// Modals
function setupModals() {
  const scoreModal = document.getElementById('scoreModal');
  const viewScoreBtn = document.getElementById('viewScoreBreakdownBtn');
  const closeScoreBtn = document.getElementById('closeScoreModalBtn');

  if (viewScoreBtn && scoreModal) {
    viewScoreBtn.addEventListener('click', () => scoreModal.classList.remove('hidden'));
  }
  if (closeScoreBtn && scoreModal) {
    closeScoreBtn.addEventListener('click', () => scoreModal.classList.add('hidden'));
  }
}
