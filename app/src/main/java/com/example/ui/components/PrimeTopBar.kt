package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ProfileEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceContainerHigh
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary
import com.example.ui.viewmodel.ToastMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PrimeTopBar(
    profile: ProfileEntity?,
    primeScore: Int,
    onScoreClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val formattedDate = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    val userName = profile?.name ?: "Hunter"
    val level = profile?.level ?: 14
    val xp = profile?.xp ?: 1840
    val xpNext = profile?.xpToNextLevel ?: 2000
    val streak = profile?.currentStreak ?: 7
    val progress = (xp.toFloat() / xpNext.toFloat()).coerceIn(0f, 1f)

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 6.dp)
        ) {
            // Header Row: System Subtitle, Greeting & Circular Score Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() }
                ) {
                    Text(
                        text = "PRIME OS • V1.0",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = PrimePrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    color = PrimeTextPrimary
                                )
                            ) {
                                append("$greeting, ")
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            ) {
                                append(userName)
                            }
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$formattedDate • Level $level",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    // Circular Immersive Score Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimePrimaryContainer)
                            .border(2.dp, PrimePrimary.copy(alpha = 0.35f), CircleShape)
                            .clickable { onScoreClick() }
                            .testTag("prime_score_top_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (primeScore > 0) "$primeScore" else "84",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = PrimeOnPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "PRIME SCORE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = PrimePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sleek Immersive XP Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = PrimePrimary,
                trackColor = PrimeBorder.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $level",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = PrimeTextSecondary
                )
                Text(
                    text = "$xp / $xpNext XP",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = PrimeTextSecondary
                )
            }
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
                shape = RoundedCornerShape(16.dp),
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

