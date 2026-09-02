package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary

/**
 * The Industry design system's "blueprint" wireframe frame: a hairline
 * square-cornered border with a small registration-mark cross drawn just
 * outside each of the four corners.
 */
@Composable
fun BlueprintFrame(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    borderColor: Color = PrimeBorder.copy(alpha = 0.3f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(background)
            .border(BorderStroke(1.dp, borderColor))
    ) {
        content()
        CornerMark(modifier = Modifier.align(Alignment.TopStart).offset((-6).dp, (-6).dp))
        CornerMark(modifier = Modifier.align(Alignment.TopEnd).offset(6.dp, (-6).dp))
        CornerMark(modifier = Modifier.align(Alignment.BottomStart).offset((-6).dp, 6.dp))
        CornerMark(modifier = Modifier.align(Alignment.BottomEnd).offset(6.dp, 6.dp))
    }
}

@Composable
private fun CornerMark(
    modifier: Modifier = Modifier,
    size: Dp = 11.dp,
    color: Color = PrimeTextPrimary.copy(alpha = 0.55f)
) {
    Canvas(modifier = modifier.size(size)) {
        val strokeWidth = 1.dp.toPx()
        // Vertical stroke, centered.
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(this.size.width / 2f, 0f),
            end = androidx.compose.ui.geometry.Offset(this.size.width / 2f, this.size.height),
            strokeWidth = strokeWidth
        )
        // Horizontal stroke, centered.
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, this.size.height / 2f),
            end = androidx.compose.ui.geometry.Offset(this.size.width, this.size.height / 2f),
            strokeWidth = strokeWidth
        )
    }
}

enum class SquareTagVariant { Accent, Outline, Neutral }

/** A square-cornered (radius 0) pill tag, matching the Industry `.tag` component. */
@Composable
fun SquareTag(
    text: String,
    modifier: Modifier = Modifier,
    variant: SquareTagVariant = SquareTagVariant.Accent,
    bold: Boolean = false
) {
    val (bg, fg, border) = when (variant) {
        SquareTagVariant.Accent -> Triple(PrimePrimaryContainer, PrimeOnPrimaryContainer, null)
        SquareTagVariant.Outline -> Triple(Color.Transparent, PrimePrimary, BorderStroke(1.dp, PrimePrimary))
        SquareTagVariant.Neutral -> Triple(PrimeSurfaceVariant, PrimeTextPrimary, null)
    }
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(0.dp))
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(0.dp)) else Modifier)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            ),
            color = fg
        )
    }
}
