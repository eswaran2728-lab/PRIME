package com.example.ui.screens.career

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.local.entities.CareerAchievementEntity
import com.example.data.local.entities.CareerGoalEntity
import com.example.data.local.entities.CareerProjectEntity
import com.example.data.local.entities.CareerSkillEntity
import com.example.data.local.entities.CertificationEntity
import com.example.data.local.entities.ResumeItemEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceContainerHigh
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun CareerScreen(
    careerGoals: List<CareerGoalEntity>,
    careerSkills: List<CareerSkillEntity>,
    certifications: List<CertificationEntity>,
    projects: List<CareerProjectEntity>,
    achievements: List<CareerAchievementEntity>,
    resumeItems: List<ResumeItemEntity>,
    onAddGoal: (title: String, targetRole: String, salary: String, timeline: String) -> Unit,
    onAddSkill: (name: String, category: String, proficiency: Int, target: Int) -> Unit,
    onUpdateSkillProgress: (skill: CareerSkillEntity, newProficiency: Int) -> Unit,
    onAddCertification: (name: String, issuer: String, issueDate: String, status: String) -> Unit,
    onAddProject: (title: String, role: String, desc: String, outcomes: String, stack: String) -> Unit,
    onAddAchievement: (title: String, date: String, impact: String) -> Unit,
    onDeleteSkill: (CareerSkillEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Skills & Roadmap", "Goals & Projects", "Certifications", "Achievements")

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showAddCertDialog by remember { mutableStateOf(false) }
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showAddAchievementDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("career_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PROFESSIONAL DOMINION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Career & Domain Mastery",
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
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini stat cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Active Skills",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimeTextSecondary
                                )
                                Text(
                                    text = "${careerSkills.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PrimePrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Certifications",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimeTextSecondary
                                )
                                Text(
                                    text = "${certifications.count { it.status == "Achieved" }}/${certifications.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGreen
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Projects",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimeTextSecondary
                                )
                                Text(
                                    text = "${projects.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeBlue
                                )
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
                    .testTag("career_tabs")
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
                // Skills & Progress Bars
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Core Competencies & Skills",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddSkillDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_career_skill_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Skill", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (careerSkills.isEmpty()) {
                    item {
                        EmptyCareerState(
                            title = "No Skills Tracked Yet",
                            subtitle = "Map out your technical and strategic competencies with target proficiency levels.",
                            onAdd = { showAddSkillDialog = true }
                        )
                    }
                } else {
                    items(careerSkills) { skill ->
                        CareerSkillCard(
                            skill = skill,
                            onUpdateProficiency = { newLevel -> onUpdateSkillProgress(skill, newLevel) },
                            onDelete = { onDeleteSkill(skill) }
                        )
                    }
                }
            }

            1 -> {
                // Goals & Projects
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Career Goals & Milestones",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddGoalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_career_goal_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Goal", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                items(careerGoals) { goal ->
                    CareerGoalCard(goal = goal)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Flagship Projects",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddProjectDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_project_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Project", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                items(projects) { project ->
                    CareerProjectCard(project = project)
                }
            }

            2 -> {
                // Certifications
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Credentials & Accreditations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddCertDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_cert_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Credential", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (certifications.isEmpty()) {
                    item {
                        EmptyCareerState(
                            title = "No Certifications Added",
                            subtitle = "Document your recognized industry certificates, cloud credentials, and licenses.",
                            onAdd = { showAddCertDialog = true }
                        )
                    }
                } else {
                    items(certifications) { cert ->
                        CertificationCard(cert = cert)
                    }
                }
            }

            3 -> {
                // Achievements
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Career Achievements & Impact",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddAchievementDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_achievement_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Impact", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                items(achievements) { ach ->
                    CareerAchievementCard(achievement = ach)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialogs
    if (showAddSkillDialog) {
        AddSkillDialog(
            onDismiss = { showAddSkillDialog = false },
            onAdd = { name, cat, prof, target ->
                onAddSkill(name, cat, prof, target)
                showAddSkillDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddCareerGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onAdd = { title, role, salary, timeline ->
                onAddGoal(title, role, salary, timeline)
                showAddGoalDialog = false
            }
        )
    }

    if (showAddCertDialog) {
        AddCertDialog(
            onDismiss = { showAddCertDialog = false },
            onAdd = { name, org, date, status ->
                onAddCertification(name, org, date, status)
                showAddCertDialog = false
            }
        )
    }

    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onAdd = { title, role, desc, outcomes, stack ->
                onAddProject(title, role, desc, outcomes, stack)
                showAddProjectDialog = false
            }
        )
    }

    if (showAddAchievementDialog) {
        AddAchievementDialog(
            onDismiss = { showAddAchievementDialog = false },
            onAdd = { title, date, impact ->
                onAddAchievement(title, date, impact)
                showAddAchievementDialog = false
            }
        )
    }
}

@Composable
fun CareerSkillCard(
    skill: CareerSkillEntity,
    onUpdateProficiency: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentSliderVal by remember { mutableFloatStateOf(skill.proficiencyPercent.toFloat()) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("career_skill_card_${skill.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = skill.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = PrimePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${skill.proficiencyPercent}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = PrimePrimary
                    )
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowRight,
                            contentDescription = "Edit skill level",
                            tint = if (isEditing) PrimeGreen else PrimeTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Proficiency Bar
            LinearProgressIndicator(
                progress = { (skill.proficiencyPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    skill.proficiencyPercent >= 80 -> PrimeGreen
                    skill.proficiencyPercent >= 50 -> PrimePrimary
                    else -> PrimeOrange
                },
                trackColor = PrimeBorder.copy(alpha = 0.4f)
            )

            AnimatedVisibility(visible = isEditing) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Calibrate Proficiency: ${currentSliderVal.toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary
                        )
                        Text(
                            text = "Target: ${skill.targetProficiencyPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimePrimary
                        )
                    }
                    Slider(
                        value = currentSliderVal,
                        onValueChange = { currentSliderVal = it },
                        onValueChangeFinished = { onUpdateProficiency(currentSliderVal.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimePrimary,
                            activeTrackColor = PrimePrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CareerGoalCard(goal: CareerGoalEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("career_goal_card_${goal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.targetRole,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeTextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimeBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = goal.timeline,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimeBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = goal.title,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimeTextSecondary
            )

            if (goal.targetSalaryOrRevenue.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Target Compensation: ${goal.targetSalaryOrRevenue}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CareerProjectCard(project: CareerProjectEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("career_project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeTextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (project.status == "Completed") PrimeGreen.copy(alpha = 0.15f) else PrimeOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = project.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (project.status == "Completed") PrimeGreen else PrimeOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Role: ${project.role}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = PrimePrimary
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimeTextSecondary
            )

            if (project.keyOutcomes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimeSurface,
                    border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = project.keyOutcomes,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CertificationCard(cert: CertificationEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("cert_card_${cert.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimeGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardMembership,
                    contentDescription = null,
                    tint = PrimeGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cert.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeTextPrimary
                )
                Text(
                    text = "${cert.issuingOrganization} • ${cert.issueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (cert.status == "Achieved") PrimeGreen.copy(alpha = 0.2f) else PrimePrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = cert.status,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (cert.status == "Achieved") PrimeGreen else PrimePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CareerAchievementCard(achievement: CareerAchievementEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("career_achievement_card_${achievement.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimeOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = PrimeOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimeTextPrimary
                )
                Text(
                    text = "Impact: ${achievement.impactMetric} • ${achievement.date}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimePrimary
                )
            }
        }
    }
}

@Composable
fun EmptyCareerState(title: String, subtitle: String, onAdd: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PrimeSurfaceVariant,
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Work, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Get Started", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun AddSkillDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, cat: String, prof: Int, target: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technical") }
    var proficiency by remember { mutableFloatStateOf(50f) }
    var target by remember { mutableFloatStateOf(90f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_skill_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Core Skill", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Skill Name (e.g. Kotlin Architecture, System Design)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimePrimary,
                        unfocusedBorderColor = PrimeBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("skill_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Proficiency: ${proficiency.toInt()}%", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                Slider(
                    value = proficiency,
                    onValueChange = { proficiency = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = PrimePrimary, activeTrackColor = PrimePrimary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) onAdd(name, category, proficiency.toInt(), target.toInt())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_skill_button")
                ) {
                    Text("Save Skill (+20 XP)", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddCareerGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, role: String, salary: String, timeline: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var timeline by remember { mutableStateOf("1 Year") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_career_goal_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Career Milestone", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Target Position / Role") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth().testTag("goal_role_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Primary Objective / Strategy") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text("Target Compensation (e.g. MYR 20,000 / mo)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (role.isNotBlank()) onAdd(title, role, salary, timeline)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_career_goal_button")
                ) {
                    Text("Save Milestone (+100 XP)", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddCertDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, org: String, date: String, status: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var org by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026") }
    var status by remember { mutableStateOf("Achieved") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_cert_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Credential", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Certification Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = org,
                    onValueChange = { org = it },
                    label = { Text("Issuing Organization (e.g. Google Cloud, AWS)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) onAdd(name, org, date, status)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Credential (+50 XP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, role: String, desc: String, outcomes: String, stack: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var outcomes by remember { mutableStateOf("") }
    var stack by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_project_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Flagship Project", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Your Role (e.g. Lead Architect)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = outcomes,
                    onValueChange = { outcomes = it },
                    label = { Text("Key Impact & Metrics") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) onAdd(title, role, desc, outcomes, stack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Project (+60 XP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddAchievementDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, date: String, impact: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var impact by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_achievement_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Log Career Impact", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Achievement / Milestone") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = impact,
                    onValueChange = { impact = it },
                    label = { Text("Impact Metric (e.g. +30% Revenue, 1M MAU)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) onAdd(title, date, impact)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Impact (+50 XP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
