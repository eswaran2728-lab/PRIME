package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.PrimeScreen
import com.example.ui.theme.PrimeBackground
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary
import com.example.ui.viewmodel.ToastMessage

/**
 * Minimal command-center header: "PRIME · DAY N", a streak indicator, and a
 * hamburger that opens [PrimeModulesSheet] for everything not on the bottom
 * tab bar — matches the PRIME.dc.html design handoff.
 */
@Composable
fun PrimeTopBar(
    dayCount: Int,
    streak: Int,
    onStreakClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PrimeBackground,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "PRIME",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 20.sp
                    ),
                    color = PrimeTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                SquareTag(
                    text = "DAY $dayCount",
                    variant = SquareTagVariant.Outline,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "$streak day streak",
                    tint = if (streak > 0) PrimeOrange else PrimeTextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onStreakClick() }
                )
                Spacer(modifier = Modifier.width(14.dp))
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "More modules",
                    tint = PrimeTextPrimary.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onMenuClick() }
                )
            }
        }
    }
}

private data class ModuleEntry(val screen: PrimeScreen, val subtitle: String)

private val OTHER_MODULES = listOf(
    ModuleEntry(PrimeScreen.Today, "Unified snapshot of today's log"),
    ModuleEntry(PrimeScreen.Workout, "Sessions, sets & templates"),
    ModuleEntry(PrimeScreen.Focus, "Sprint timer & distraction log"),
    ModuleEntry(PrimeScreen.Sleep, "Bedtime, quality & sleep debt"),
    ModuleEntry(PrimeScreen.Mindset, "Daily scales & journal"),
    ModuleEntry(PrimeScreen.Grooming, "AM/PM/weekly hygiene stacks"),
    ModuleEntry(PrimeScreen.Career, "Goals, matrix & resume items"),
    ModuleEntry(PrimeScreen.Learning, "Courses, reading list & study logs"),
    ModuleEntry(PrimeScreen.Finance, "Cashflow, war chest & goals"),
    ModuleEntry(PrimeScreen.Gamification, "Level progress & trophy ledger"),
    ModuleEntry(PrimeScreen.Vision, "Identity pillars & 5Y roadmap"),
    ModuleEntry(PrimeScreen.WeeklyReview, "Performance summary & AI audit"),
    ModuleEntry(PrimeScreen.Analytics, "Trends, volume & score charts"),
    ModuleEntry(PrimeScreen.Ai, "Tactical debriefs & food scanner"),
    ModuleEntry(PrimeScreen.GlobalSearch, "Search across the whole OS"),
    ModuleEntry(PrimeScreen.Settings, "Profile & data")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeModulesSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelect: (PrimeScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PrimeSurface,
        modifier = modifier
    ) {
        Text(
            text = "MODULES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = PrimeTextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(OTHER_MODULES) { entry ->
                ModuleRow(entry = entry, onClick = { onSelect(entry.screen) })
            }
            item { Spacer(modifier = Modifier.padding(bottom = 24.dp)) }
        }
    }
}

@Composable
private fun ModuleRow(entry: ModuleEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = entry.screen.icon,
            contentDescription = null,
            tint = PrimePrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.screen.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = PrimeTextPrimary
            )
            Text(
                text = entry.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = PrimeTextSecondary
            )
        }
    }
}

@Composable
fun PrimeToastSnackbar(
    toast: ToastMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (toast != null) {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimeSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = toast.message,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = PrimeTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (toast.undoAction != null) {
                        TextButton(
                            onClick = {
                                toast.undoAction.invoke()
                                onDismiss()
                            }
                        ) {
                            Text(
                                text = "UNDO",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = PrimePrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
