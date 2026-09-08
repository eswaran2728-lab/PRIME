# PRIME — Personal Operating System for Total Self-Mastery

[![Build Android App Bundle & APK](https://github.com/eswaran2728-lab/PRIME/actions/workflows/main.yml/badge.svg)](https://github.com/eswaran2728-lab/PRIME/actions/workflows/main.yml)
[![Repository](https://img.shields.io/badge/GitHub-PRIME-181717?logo=github)](https://github.com/eswaran2728-lab/PRIME)

**PRIME** is a unified, offline-first personal operating system built with **Jetpack Compose (Material 3)**, **Room Database**, and **Gemini AI**. It integrates physical performance, cognitive output, personal finance, career advancement, and long-term vision into an explainable **0–100 Daily PRIME Score**.

---

## 🔗 Quick Links

- **Live Web App (GitHub Pages)**: [https://eswaran2728-lab.github.io/PRIME/](https://eswaran2728-lab.github.io/PRIME/)
- **GitHub Repository**: [https://github.com/eswaran2728-lab/PRIME](https://github.com/eswaran2728-lab/PRIME)
- **CI / Build Artifacts (APK & AAB)**: [https://github.com/eswaran2728-lab/PRIME/actions](https://github.com/eswaran2728-lab/PRIME/actions)
- **Deployment Pipelines**: [https://github.com/eswaran2728-lab/PRIME/actions/workflows/deploy_pages.yml](https://github.com/eswaran2728-lab/PRIME/actions/workflows/deploy_pages.yml)

---

## ⚡ Key Modules & Features

### 1. 📊 Daily PRIME Score Engine (0–100)
- **Algorithmically Transparent**: Transparent weighted score calculated from 5 core pillars:
  - **Habits** (20 pts): Daily completion consistency
  - **Nutrition & Hydration** (20 pts): Calorie target adherence + water intake
  - **Training** (20 pts): Active workout session logging + volume calculations
  - **Sleep & Recovery** (20 pts): Sleep duration & subjective quality
  - **Focus & Mindset** (20 pts): Deep work sprints + stoic journaling calibration
- **Score Breakdown Dialog**: Interactive modal showing exact points earned per pillar and actionable tips for reaching 100%.

### 2. 🏋️ Training & Workout Architecture
- **Template System**: Pre-configured workout splits (Push/Pull/Legs, Upper/Lower, Full Body).
- **Active Session Tracker**: Real-time set logging (Weight kg, Reps, RPE rating).
- **Integrated Rest Timer**: Countdown timer with customizable intervals (60s, 90s, 120s, 180s).
- **Exercise Library**: Searchable database with muscle group tags and 1RM tracking.

### 3. 🥗 Precision Nutrition & Hydration
- **Macro & Micronutrient Tracking**: Calories, Protein, Carbohydrates, and Fats with visual progress arcs.
- **AI Food Estimator**: Natural language meal parsing powered by Gemini AI to estimate nutritional breakdowns.
- **Fast Water Logger**: One-tap hydration logging (+250ml, +500ml, +1000ml).
- **Custom Food Database**: Add and reuse custom verified nutritional items.

### 4. 🧠 Cognitive Focus & Pomodoro Engine
- **Focus Modes**: Deep Work (50/10), Pomodoro (25/5), Flow State (90m Sprint), or Custom timers.
- **Distraction Tracker**: In-session distraction logger to identify and eliminate cognitive friction.
- **Session History & Analytics**: Total focus hours, completion rates, and average focus quality.

### 5. 🧘 Stoic Mindset & Journaling
- **Daily Calibration**: Mood, Energy, Stress, Confidence, and Focus state sliders.
- **Structured Stoic Prompts**:
  - *Daily Accomplishments*
  - *Obstacles Faced & How They Were Overcome*
  - *Lessons Learned & Wisdom Gained*
  - *Freeform Reflection*

### 6. 💼 Career, Skills & Project Portfolio
- **Strategic Career Goals**: Target roles, salary milestones, and timeline horizons.
- **Skill Tree Matrix**: Track proficiency across technical, leadership, and domain skills.
- **Certifications & Projects**: Document professional milestones, outcomes, and technology stacks.

### 7. 📚 Continuous Learning Hub
- **Course Tracker**: Module completion percentage and platform tracking.
- **Reading List**: Book progress tracker with page counts and key insights.
- **Study Sessions**: Dedicated logging for deep research topics and takeaways.

### 8. 💰 Personal Finance & Wealth Goals
- **Cash Flow Tracking**: Income, Expense, and Recurring subscription logging.
- **Financial Milestones**: Emergency fund, investment targets, and debt payoff trackers with dynamic progress bars.
- **Multi-Currency Support**: Configurable currency formatting (USD, EUR, GBP, MYR, SGD, INR, etc.).

### 9. 🎮 Gamification, Streaks & XP System
- **Leveling Progression**: Gain XP from completing workouts, hitting macros, finishing focus sprints, and maintaining habits.
- **Achievement Badges**: Unlockable milestones for discipline, physical feats, and cognitive consistency.
- **Streak Grace Shield**: Streak protection mechanics to maintain momentum during recovery days.

### 10. 🔮 Long-Term Vision & Weekly Review
- **Identity Statements**: "Who I Am" and "Who I Want to Become" core anchor points.
- **Horizon Milestones**: 1-Year, 3-Year, 5-Year, and 10-Year milestone mapping.
- **AI Weekly Review Generator**: Automated retrospective synthesizing wins, challenges, and next week's focus areas.

---

## 🛠️ Architecture & Tech Stack

```
com.example
├── data
│   ├── ai          # Gemini AI provider, REST client, fallback offline rules
│   ├── local       # Room Database (PrimeDatabase), DAOs, Entity schemas
│   └── repository  # PrimeRepository (Single Source of Truth, reactive Flows)
├── ui
│   ├── components  # Reusable UI widgets (TopBar, BottomBar, Modals, Sliders)
│   ├── navigation  # Screen routing and state transitions
│   ├── screens     # 17 Feature Screens (Dashboard, Workout, Nutrition, etc.)
│   ├── theme       # Obsidian/Cyberpunk dark design system (Color, Theme, Typography)
│   └── viewmodel   # PrimeViewModel (MVVM state orchestration & event handling)
```

- **UI Framework**: Jetpack Compose with Material 3 & Edge-to-Edge display
- **Persistence**: Room Database with 18+ normalized SQLite tables and pre-seeded sample data
- **AI Integration**: Google Gemini 2.5/2.0 API with offline graceful fallback
- **State Management**: Kotlin Coroutines & `StateFlow`
- **CI/CD**: GitHub Actions building signed Release AAB and Debug APK on every push

---

## 📦 Automated Release Downloads

Every commit to `main` automatically triggers the GitHub Actions CI pipeline:
1. Navigate to [PRIME GitHub Actions](https://github.com/eswaran2728-lab/PRIME/actions).
2. Click on the latest workflow run.
3. Download the build artifacts:
   - **`PRIME-Debug-APK`**: Ready to install directly on Android devices.
   - **`PRIME-Release-Bundle`**: `.aab` package formatted for Google Play Store upload.
