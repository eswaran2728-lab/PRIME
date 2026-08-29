package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeRed
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val actionType: QuickActionType
)

enum class QuickActionType {
    LOG_FOOD,
    START_WORKOUT,
    ADD_HABIT,
    ADD_TASK,
    LOG_WEIGHT,
    START_FOCUS,
    LOG_WATER_500,
    JOURNAL
}

@Composable
fun QuickActionDialog(
    onDismiss: () -> Unit,
    onActionSelected: (QuickActionType) -> Unit
) {
    val actions = listOf(
        QuickActionItem("Log Food", Icons.Default.Restaurant, PrimeGreen, QuickActionType.LOG_FOOD),
        QuickActionItem("Start Workout", Icons.Default.FitnessCenter, PrimePrimary, QuickActionType.START_WORKOUT),
        QuickActionItem("+500ml Water", Icons.Default.WaterDrop, PrimeCyan, QuickActionType.LOG_WATER_500),
        QuickActionItem("Add Habit", Icons.Default.Repeat, PrimePurple, QuickActionType.ADD_HABIT),
        QuickActionItem("Add Task", Icons.Default.AddTask, PrimeBlue, QuickActionType.ADD_TASK),
        QuickActionItem("Log Weight", Icons.Default.MonitorWeight, PrimeOrange, QuickActionType.LOG_WEIGHT),
        QuickActionItem("Focus Session", Icons.Default.HourglassTop, PrimeRed, QuickActionType.START_FOCUS),
        QuickActionItem("Journal / Note", Icons.Default.AddComment, PrimePrimary, QuickActionType.JOURNAL)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("quick_actions_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = PrimeTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PrimeTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(actions) { item ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PrimeSurfaceVariant,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    onActionSelected(item.actionType)
                                    onDismiss()
                                }
                                .testTag("quick_action_${item.actionType.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = item.color.copy(alpha = 0.18f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = PrimeTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

