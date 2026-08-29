package com.example.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.PrimeScreen
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

data class SearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val color: Color,
    val targetScreen: PrimeScreen
)

@Composable
fun GlobalSearchScreen(
    onNavigateToScreen: (PrimeScreen) -> Unit,
    allFoods: List<String>,
    allExercises: List<String>,
    allHabits: List<String>,
    allGoals: List<String>,
    allTasks: List<String>,
    allNotes: List<String>,
    allCourses: List<String>,
    allTransactions: List<String>,
    allSkills: List<String>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Exercises", "Nutrition", "Habits", "Goals", "Tasks", "Mindset", "Learning", "Finance", "Career")

    // Construct searchable items pool
    val allItems = remember(allFoods, allExercises, allHabits, allGoals, allTasks, allNotes, allCourses, allTransactions, allSkills) {
        val list = mutableListOf<SearchResultItem>()
        allExercises.forEach {
            list.add(SearchResultItem("ex_$it", it, "Exercise & Routine", "Exercises", Icons.Default.FitnessCenter, PrimePrimary, PrimeScreen.Workout))
        }
        allFoods.forEach {
            list.add(SearchResultItem("food_$it", it, "Macro Database Food Item", "Nutrition", Icons.Default.Restaurant, PrimeGreen, PrimeScreen.Nutrition))
        }
        allHabits.forEach {
            list.add(SearchResultItem("hab_$it", it, "Daily Atomic Habit", "Habits", Icons.Default.CheckCircle, PrimeGreen, PrimeScreen.Habits))
        }
        allGoals.forEach {
            list.add(SearchResultItem("goal_$it", it, "Target Milestone & Objective", "Goals", Icons.Default.Flag, PrimeOrange, PrimeScreen.Goals))
        }
        allTasks.forEach {
            list.add(SearchResultItem("tsk_$it", it, "Scheduled Planner Task", "Tasks", Icons.Default.CheckCircle, PrimeBlue, PrimeScreen.Planner))
        }
        allNotes.forEach {
            list.add(SearchResultItem("nt_$it", it, "Journal Log / Reflection", "Mindset", Icons.Default.AddComment, PrimePurple, PrimeScreen.Mindset))
        }
        allCourses.forEach {
            list.add(SearchResultItem("crs_$it", it, "Course / Reading Matrix", "Learning", Icons.Default.School, PrimeCyan, PrimeScreen.Learning))
        }
        allTransactions.forEach {
            list.add(SearchResultItem("tx_$it", it, "Ledger Cash Flow Entry", "Finance", Icons.Default.AccountBalanceWallet, PrimeGreen, PrimeScreen.Finance))
        }
        allSkills.forEach {
            list.add(SearchResultItem("sk_$it", it, "Career Skill / Project", "Career", Icons.Default.Work, PrimeBlue, PrimeScreen.Career))
        }
        list
    }

    val filteredResults = remember(searchQuery, selectedCategoryFilter, allItems) {
        allItems.filter { item ->
            val matchesCategory = selectedCategoryFilter == "All" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true) || item.subtitle.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("search_hero_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "GLOBAL OMNI-SEARCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = PrimePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Search Everything in PRIME",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search foods, workouts, habits, goals, notes, finance...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PrimePrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = PrimeTextSecondary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedContainerColor = PrimeSurface,
                            unfocusedContainerColor = PrimeSurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("global_search_input")
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedCategoryFilter == cat) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = PrimeSurfaceVariant,
                            labelColor = PrimeTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryFilter == cat,
                            borderColor = PrimeBorder.copy(alpha = 0.3f),
                            selectedBorderColor = PrimePrimary
                        )
                    )
                }
            }
        }

        // Search Results Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Results (${filteredResults.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeTextPrimary
                )
            }
        }

        if (filteredResults.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PrimeSurfaceVariant,
                    border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = PrimeTextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No matching records found", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try searching another keyword or resetting the category filter.", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                    }
                }
            }
        } else {
            items(filteredResults) { result ->
                SearchResultRow(
                    result = result,
                    onClick = { onNavigateToScreen(result.targetScreen) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SearchResultRow(
    result: SearchResultItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("search_result_${result.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(result.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = result.icon,
                    contentDescription = null,
                    tint = result.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${result.category} • ${result.subtitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimeSurface
            ) {
                Text(
                    text = "Open",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
