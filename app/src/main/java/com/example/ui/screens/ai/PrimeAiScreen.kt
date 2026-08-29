package com.example.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiActionSuggestion
import com.example.data.ai.DailyCoachSummary
import com.example.data.ai.NutritionEstimateResult
import com.example.data.ai.WeeklyReviewSummary
import com.example.data.local.entities.AiChatMessageEntity
import com.example.data.local.entities.AiUsageStatsEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun PrimeAiScreen(
    messages: List<AiChatMessageEntity>,
    activeAssistantType: String,
    isGenerating: Boolean,
    dailyCoach: DailyCoachSummary?,
    weeklyReview: WeeklyReviewSummary?,
    foodEstimate: NutritionEstimateResult?,
    aiUsage: AiUsageStatsEntity?,
    onSelectAssistant: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onApplyAction: (AiActionSuggestion) -> Unit,
    onRefreshDailyCoach: () -> Unit,
    onRefreshWeeklyReview: () -> Unit,
    onEstimateFood: (String) -> Unit,
    onLogEstimatedFood: (NutritionEstimateResult) -> Unit,
    onClearChat: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: AI Chat & Coach, 1: Daily/Weekly Debriefs, 2: AI Food Scanner
    var inputMessage by remember { mutableStateOf("") }
    var foodPromptInput by remember { mutableStateOf("") }

    val assistantChips = listOf(
        Pair("general", "Prime Coach"),
        Pair("nutrition", "Nutritionist"),
        Pair("workout", "Strength Coach"),
        Pair("schedule", "Time Strategist"),
        Pair("goals", "Goal Architect"),
        Pair("journal", "Stoic Guide")
    )

    val promptSuggestions = when (activeAssistantType) {
        "nutrition" -> listOf("How should I hit 180g protein today?", "Estimate calories for grilled salmon & rice", "What should I eat post-workout?")
        "workout" -> listOf("How do I safely progress my bench press?", "Give me a 3-exercise push warmup", "How to manage fatigue on leg day?")
        "schedule" -> listOf("Plan my deep work blocks today", "How to handle afternoon energy dips?", "Optimize my morning routine")
        "goals" -> listOf("Break down my 85kg muscle goal", "Set 3 milestones for this week", "How to track quarterly goals?")
        "journal" -> listOf("Help me reframe today's obstacles", "Stoic reflection for stress", "Evaluate my discipline score")
        else -> listOf("Audit my PRIME score today", "What is my biggest leverage action?", "Review my sleep and recovery")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PRIME INTELLIGENCE ENGINE",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tactical AI Operating Assistant",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = { onClearChat(if (selectedTopTab == 0) activeAssistantType else null) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimeSurface)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = PrimeTextMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Tab Row
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = PrimeSurface,
            contentColor = PrimePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                    color = PrimePrimary
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, PrimeBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = { Text("AI Coach", fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = { Text("Debriefs", fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = { Text("Food Scanner", fontWeight = if (selectedTopTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTopTab == 0) {
            // Assistant Type Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(assistantChips) { (type, label) ->
                    FilterChip(
                        selected = activeAssistantType == type,
                        onClick = { onSelectAssistant(type) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Messages Stream
            val filteredMessages = messages.filter { it.assistantType == activeAssistantType }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Safety Protocol Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Calibrated for performance guidance. For medical or psychiatric care, consult licensed professionals.",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimeTextSecondary
                            )
                        }
                    }
                }

                if (filteredMessages.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Ready to assist your development.", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Ask any question or tap a high-signal prompt below to calibrate your targets.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                        }
                    }
                } else {
                    items(filteredMessages, key = { it.id }) { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.85f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) PrimePrimary.copy(alpha = 0.2f) else PrimeCardBg
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(if (isUser) PrimePrimary else PrimeBorder)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )

                                    // Action Suggestion Button if attached
                                    if (!msg.suggestedActionTitle.isNullOrBlank() && !msg.suggestedActionPayload.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                onApplyAction(
                                                    AiActionSuggestion(
                                                        title = msg.suggestedActionTitle,
                                                        actionType = msg.suggestedActionType ?: "ADD_TASK",
                                                        payload = msg.suggestedActionPayload
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(msg.suggestedActionTitle, style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimePrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PRIME AI is formulating strategy...", style = MaterialTheme.typography.bodySmall, color = PrimePrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Suggested Prompt Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(promptSuggestions) { prompt ->
                    Card(
                        modifier = Modifier
                            .clickable { onSendMessage(prompt) }
                            .clip(RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Ask PRIME AI...", color = PrimeTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimePrimary,
                        unfocusedBorderColor = PrimeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            onSendMessage(inputMessage)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimePrimary)
                        .testTag("send_ai_message_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimeOnPrimaryDark)
                }
            }
        } else if (selectedTopTab == 1) {
            // Daily Coach & Weekly Review Debriefs Tab
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Daily Coach Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Daily Tactical Coach", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = onRefreshDailyCoach) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimePrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (dailyCoach != null) {
                                Text(dailyCoach.summary, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("🎯 High-Leverage Focus: ${dailyCoach.primaryFocus}", style = MaterialTheme.typography.bodySmall, color = PrimePrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("⚠️ Potential Friction: ${dailyCoach.potentialFriction}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        onApplyAction(
                                            AiActionSuggestion(
                                                title = "Execute Daily Focus",
                                                actionType = "ADD_TASK",
                                                payload = dailyCoach.primaryFocus
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Apply Strategy to Planner")
                                }
                            } else {
                                Text("Tap refresh to generate today's daily tactical debrief.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onRefreshDailyCoach,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                                ) {
                                    Text("Generate Coach Brief")
                                }
                            }
                        }
                    }
                }

                // Weekly Review Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Insights, contentDescription = null, tint = Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Weekly Performance Audit", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = onRefreshWeeklyReview) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimePrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (weeklyReview != null) {
                                Text("Score: ${weeklyReview.averageScore.toInt()} Avg • ${weeklyReview.highlights}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🏆 Consistency Wins: ${weeklyReview.consistencyWins}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF10B981))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("🔍 Blind Spots: ${weeklyReview.blindSpots}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("🚀 Next Week Tactical Adjustments: ${weeklyReview.tacticalAdjustments}", style = MaterialTheme.typography.bodySmall, color = PrimePrimary)
                            } else {
                                Text("Generate a deep weekly debrief evaluating all 7-day habits, scores, and workouts.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onRefreshWeeklyReview,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                                ) {
                                    Text("Run Weekly Audit")
                                }
                            }
                        }
                    }
                }

                // AI Usage Stats Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Engine Telemetry & Reliability", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Daily Requests: ${aiUsage?.requestsCount ?: 0} | Errors: ${aiUsage?.errorsCount ?: 0} | Rate Limit Hits: ${aiUsage?.rateLimitHits ?: 0}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                        }
                    }
                }
            }
        } else {
            // AI Food Scanner Tab (Structured Estimation Output)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = PrimePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Food & Macro Estimator", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Describe any meal in natural language (e.g. '200g ribeye steak with 1 cup mashed potatoes and asparagus').",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = foodPromptInput,
                                onValueChange = { foodPromptInput = it },
                                placeholder = { Text("Enter meal description...", color = PrimeTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimePrimary,
                                    unfocusedBorderColor = PrimeBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onEstimateFood(foodPromptInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimeOnPrimaryDark, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Estimate Nutrition & Macros")
                            }
                        }
                    }
                }

                if (foodEstimate != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                            shape = RoundedCornerShape(16.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimePrimary))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(foodEstimate.foodName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("Confidence: ${(foodEstimate.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = PrimePrimary)
                                }
                                Text(foodEstimate.portionSize, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Calories", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                        Text("${foodEstimate.calories} kcal", style = MaterialTheme.typography.titleMedium, color = PrimePrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Protein", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                        Text("${foodEstimate.proteinGrams.toInt()}g", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Carbs", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                        Text("${foodEstimate.carbsGrams.toInt()}g", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Fat", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                        Text("${foodEstimate.fatGrams.toInt()}g", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (foodEstimate.disclaimer.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(foodEstimate.disclaimer, style = MaterialTheme.typography.labelSmall, color = PrimeTextMuted)
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { onLogEstimatedFood(foodEstimate) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Log to Today's Nutrition (+20 XP)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
