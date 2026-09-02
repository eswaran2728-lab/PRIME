package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// PRIME Field Dark Palette — accent-900 ground / accent-300 bright accent,
// derived from the Industry design system's tonal ramp (see PRIME.dc.html).
val PrimeBackground = Color(0xFF1D2D3D) // accent-900
val PrimeSurface = Color(0xFF2C455D) // accent-800
val PrimeSurfaceVariant = Color(0xFF314457) // accent-2-800
val PrimeSurfaceContainerHigh = Color(0xFF3A5975)
val PrimePrimary = Color(0xFFB5D9FD) // accent-300 — the single bold accent
val PrimePrimaryContainer = Color(0xFF2C455D) // remapped accent-100 (reversed ramp in dark field)
val PrimeOnPrimaryContainer = Color(0xFFD6EBFF) // remapped accent-800
val PrimeOnPrimary = Color(0xFF16232F)
val PrimeOnPrimaryDark = Color(0xFF0C1117)

// Backward compatible aliases & Accents
val PrimeBlack = PrimeBackground
val PrimeDarkSurface = PrimeSurface
val PrimeDarkSurfaceVariant = PrimeSurfaceVariant
val PrimeDarkSurfaceElevated = PrimeSurfaceContainerHigh
val PrimeGold = PrimePrimary
val PrimeGoldLight = PrimeOnPrimaryContainer
val PrimeGoldDark = PrimePrimaryContainer
val PrimeLavender = PrimePrimary
// One bold accent, used sparingly — per-metric distinguishing colors collapse
// onto the same accent tone rather than a rainbow of hues.
val PrimePurple = PrimePrimary
val PrimePurpleDeep = PrimePrimaryContainer
val PrimeBlue = PrimePrimary
val PrimeCyan = PrimePrimary
val PrimeGreen = Color(0xFF81D9A0)
val PrimeRed = Color(0xFFF2B8B5)
val PrimeOrange = Color(0xFFFFB74D)

// High-Contrast & Muted Typography
val PrimeTextPrimary = Color(0xFFF5F5F8)
val PrimeTextSecondary = Color(0xFFA7B4C0)
val PrimeTextMuted = Color(0xFF7C8A97)
val PrimeCardBg = PrimeSurface
val PrimeBorder = PrimeTextPrimary

val PrimeTextPrimaryDark = PrimeTextPrimary
val PrimeTextSecondaryDark = PrimeTextSecondary
val PrimeTextMutedDark = PrimeTextMuted
val PrimeBorderDark = PrimeBorder

// Light Scheme Fallback
val PrimeWhite = Color(0xFFFAFAFB)
val PrimeLightSurface = Color(0xFFFFFFFF)
val PrimeLightSurfaceVariant = Color(0xFFF1F5F9)
val PrimeLightSurfaceElevated = Color(0xFFE2E8F0)
val PrimeTextPrimaryLight = Color(0xFF141218)
val PrimeTextSecondaryLight = Color(0xFF49454F)
val PrimeTextMutedLight = Color(0xFF938F99)
val PrimeBorderLight = Color(0xFFCAC4D0)
