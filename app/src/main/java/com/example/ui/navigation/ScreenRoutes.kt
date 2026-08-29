package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class PrimeScreen(
    val title: String,
    val icon: ImageVector,
    val isPrimaryTab: Boolean = true
) {
    Dashboard("Dashboard", Icons.Default.Dashboard, true),
    Today("Today", Icons.Default.Today, true),
    Workout("Workout", Icons.Default.FitnessCenter, true),
    Nutrition("Nutrition", Icons.Default.Restaurant, true),
    Planner("Planner", Icons.Default.CalendarMonth, true),
    Focus("Focus", Icons.Default.Timer, false),
    Grooming("Grooming", Icons.Default.Face, false),
    Mindset("Mindset", Icons.Default.Psychology, false),
    Sleep("Sleep", Icons.Default.Bedtime, false),
    Career("Career", Icons.Default.Work, false),
    Learning("Learning", Icons.Default.School, false),
    Finance("Finance", Icons.Default.AccountBalanceWallet, false),
    Gamification("XP & Badges", Icons.Default.EmojiEvents, false),
    Vision("My PRIME Vision", Icons.Default.Explore, false),
    WeeklyReview("Weekly Review", Icons.Default.RateReview, false),
    GlobalSearch("Global Search", Icons.Default.Search, false),
    Onboarding("Onboarding", Icons.Default.Star, false),
    Analytics("Progress", Icons.Default.Insights, false),
    PrimeAI("PRIME AI", Icons.Default.AutoAwesome, false),
    Ai("PRIME AI", Icons.Default.AutoAwesome, false),
    Goals("Goals", Icons.Default.CalendarMonth, false),
    Habits("Habits", Icons.Default.CheckCircle, false),
    Body("Body", Icons.Default.AccessibilityNew, false),
    Score("Score", Icons.Default.QueryStats, false),
    Settings("Settings", Icons.Default.Settings, false)
}
