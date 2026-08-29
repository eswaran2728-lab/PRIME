package com.example.ui.screens.gamification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entities.AchievementBadgeEntity
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.XpTransactionEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun GamificationScreen(
    profile: ProfileEntity?,
    badges: List<AchievementBadgeEntity>,
    xpTransactions: List<XpTransactionEntity>,
    onActivateStreakRecovery: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Achievements", "XP Ledger", "Ranks & Mastery")

    val level = profile?.level ?: 1
    val xp = profile?.xp ?: 450
    val xpToNext = profile?.xpToNextLevel ?: 1000
    val progress = (xp.toFloat() / xpToNext.toFloat()).coerceIn(0f, 1f)
    val streak = profile?.currentStreak ?: 1
    val longestStreak = profile?.longestStreak ?: 1

    val unlockedCount = badges.count { it.isUnlocked }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Level Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("gamification_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HONOR & ASCENSION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Operator Level $level",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                            Text(
                                text = profile?.title ?: "Apex Operator",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PrimePrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PrimePrimaryContainer)
                                .border(2.dp, PrimePrimary.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // XP Progress bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimePrimary,
                        trackColor = PrimeBorder.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$xp / $xpToNext XP",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Text(
                            text = "${((1f - progress) * xpToNext).toInt()} XP to Next Rank",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimeTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Streak & Streak Recovery Shield
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimeSurface,
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimeOrange.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = PrimeOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "$streak-Day Protocol Streak",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeTextPrimary
                                    )
                                    Text(
                                        text = "Best Streak: $longestStreak days",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimeTextSecondary
                                    )
                                }
                            }

                            // Non-punitive recovery shield
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimeGreen.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Grace Shield Active",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PrimeSurfaceVariant,
                contentColor = PrimePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimePrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("gamification_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) PrimePrimary else PrimeTextSecondary
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Badges
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Milestones & Badges ($unlockedCount/${badges.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                    }
                }

                items(badges) { badge ->
                    BadgeListItem(badge = badge)
                }
            }

            1 -> {
                // XP Ledger
                item {
                    Text(
                        text = "Recent XP Allocations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                // XP Reward guide summary
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimeSurfaceVariant,
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Standard Protocol Rewards", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Workout Complete: +50 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Text("Habit Check: +10 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Deep Work Block: +30 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Text("Mindset Journal: +10 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Weekly Goal Met: +100 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                                Text("Module Mastery: +50 XP", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                            }
                        }
                    }
                }

                items(xpTransactions) { tx ->
                    XpTransactionRow(tx = tx)
                }
            }

            2 -> {
                // Ranks & Mastery Levels
                item {
                    Text(
                        text = "PRIME Operator Ranks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                val ranks = listOf(
                    Triple("Initiate", "Level 1 - 3", "Establishing routine baselines and daily consistency."),
                    Triple("Operator", "Level 4 - 7", "Consistent output, progressive physical overload."),
                    Triple("Architect", "Level 8 - 12", "Mastery of time blocks, deep focus, and strategic growth."),
                    Triple("Apex Operator", "Level 13 - 19", "Dominion across all 11 life vectors simultaneously."),
                    Triple("Sovereign Prime", "Level 20+", "Unbreakable compounding discipline and total freedom.")
                )

                items(ranks) { (rankTitle, rankLevels, rankDesc) ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (rankTitle.contains("Apex") || rankTitle.contains("Operator") || rankTitle.contains("Initiate")) PrimePrimary.copy(alpha = 0.15f) else PrimeBorder.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (rankTitle.contains("Apex") || rankTitle.contains("Operator") || rankTitle.contains("Initiate")) PrimePrimary else PrimeTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = rankTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = PrimeTextPrimary
                                    )
                                    Text(
                                        text = rankLevels,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = PrimePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rankDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimeTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BadgeListItem(badge: AchievementBadgeEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, if (badge.isUnlocked) PrimePrimary.copy(alpha = 0.4f) else PrimeBorder.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().testTag("badge_item_${badge.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (badge.isUnlocked) PrimePrimary.copy(alpha = 0.2f) else PrimeBorder.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) PrimePrimary else PrimeTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimePrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+${badge.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = PrimePrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )
                if (badge.isUnlocked && badge.unlockedDate.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Unlocked on ${badge.unlockedDate}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = PrimeGreen
                    )
                }
            }
        }
    }
}

@Composable
fun XpTransactionRow(tx: XpTransactionEntity) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = tx.source,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimeTextPrimary
                )
                Text(
                    text = if (tx.notes.isNotBlank()) "${tx.notes} • ${tx.date}" else tx.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimeTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimePrimary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "+${tx.xpEarned} XP",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
