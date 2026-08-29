package com.example.ui.screens.vision

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ProfileEntity
import com.example.data.local.entities.VisionItemEntity
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
fun VisionScreen(
    profile: ProfileEntity?,
    visionItems: List<VisionItemEntity>,
    onAddVisionMilestone: (title: String, content: String, horizon: String) -> Unit,
    onToggleMilestone: (VisionItemEntity) -> Unit,
    onUpdateIdentityStatements: (whoIAm: String, whoIWantToBecome: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Identity & Pillars", "Roadmap (1Y / 3Y / 5Y)")

    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var showEditIdentityDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("vision_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LONG-RANGE COMPASS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "My PRIME Vision",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimePrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Anchor your daily actions in 5-year destiny and uncompromising identity standards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )
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
                    .testTag("vision_tabs")
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
                // Identity & Core Pillars
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Core Identity Statements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        IconButton(onClick = { showEditIdentityDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Identity", tint = PrimePrimary)
                        }
                    }
                }

                // Who I Am Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimePrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "CURRENT STANDARD • WHO I AM",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = PrimePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = profile?.bio?.ifBlank { "Disciplined builder dedicated to physical and mental mastery. I hold the line on training, deep work, and high standards." }
                                    ?: "Disciplined builder dedicated to physical and mental mastery.",
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                                color = PrimeTextPrimary
                            )
                        }
                    }
                }

                // Who I Want To Become Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                        border = BorderStroke(1.dp, PrimePrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimeOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "THE ARCHETYPE • WHO I WANT TO BECOME",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = PrimeOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "A sovereign operator who commands elite physical vitality, builds compounding businesses, leads with calm authority, and lives with uncompromising purpose.",
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp, fontWeight = FontWeight.Medium),
                                color = PrimeTextPrimary
                            )
                        }
                    }
                }
            }

            1 -> {
                // Roadmap (1Y / 3Y / 5Y)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horizon Roadmaps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddMilestoneDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_vision_milestone_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Milestone", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                val horizons = listOf("1 Year", "3 Year", "5 Year")
                horizons.forEach { horizon ->
                    val horizonItems = visionItems.filter { it.targetHorizon == horizon }
                    item {
                        HorizonSectionHeader(horizon = horizon)
                    }

                    if (horizonItems.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PrimeSurfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No $horizon milestones defined yet. Tap 'Add Milestone' to plant your flag.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimeTextSecondary,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    } else {
                        items(horizonItems) { item ->
                            VisionMilestoneCard(
                                item = item,
                                onToggle = { onToggleMilestone(item) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddMilestoneDialog) {
        AddMilestoneDialog(
            onDismiss = { showAddMilestoneDialog = false },
            onAdd = { title, content, horizon ->
                onAddVisionMilestone(title, content, horizon)
                showAddMilestoneDialog = false
            }
        )
    }

    if (showEditIdentityDialog) {
        EditIdentityDialog(
            currentBio = profile?.bio ?: "",
            onDismiss = { showEditIdentityDialog = false },
            onSave = { bio ->
                onUpdateIdentityStatements(bio, "")
                showEditIdentityDialog = false
            }
        )
    }
}

@Composable
fun HorizonSectionHeader(horizon: String) {
    val color = when (horizon) {
        "1 Year" -> PrimeGreen
        "3 Year" -> PrimeBlue
        else -> PrimePurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$horizon Horizon",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimeTextPrimary
        )
    }
}

@Composable
fun VisionMilestoneCard(
    item: VisionItemEntity,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, if (item.isCompleted) PrimeGreen.copy(alpha = 0.3f) else PrimeBorder.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth().testTag("vision_milestone_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle completion",
                    tint = if (item.isCompleted) PrimeGreen else PrimeTextSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (item.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    ),
                    color = if (item.isCompleted) PrimeTextSecondary else PrimeTextPrimary
                )
                if (item.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AddMilestoneDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, content: String, horizon: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var horizon by remember { mutableStateOf("1 Year") }

    val horizons = listOf("1 Year", "3 Year", "5 Year")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_vision_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Vision Milestone", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    horizons.forEach { h ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (horizon == h) PrimePrimary else PrimeSurfaceVariant,
                            border = BorderStroke(1.dp, if (horizon == h) PrimePrimary else PrimeBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { horizon = h }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = h,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (horizon == h) PrimeOnPrimaryDark else PrimeTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone Flag (e.g. 10% Bodyfat, $500k Portfolio)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Strategic Path / Details") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) onAdd(title, content, horizon)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Plant Milestone (+50 XP)", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditIdentityDialog(
    currentBio: String,
    onDismiss: () -> Unit,
    onSave: (bio: String) -> Unit
) {
    var bio by remember { mutableStateOf(currentBio) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Refine Identity Creed", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Current Identity Standards") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onSave(bio) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Creed", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
