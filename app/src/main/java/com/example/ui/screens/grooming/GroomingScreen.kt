package com.example.ui.screens.grooming

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.local.entities.GroomingLogEntity
import com.example.data.local.entities.GroomingRoutineItemEntity
import com.example.data.local.entities.SkincareLogEntity
import com.example.data.local.entities.SkincareProductEntity
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCardBg
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun GroomingScreen(
    groomingItems: List<GroomingRoutineItemEntity>,
    groomingLogs: List<GroomingLogEntity>,
    skincareProducts: List<SkincareProductEntity>,
    skincareLogs: List<SkincareLogEntity>,
    onToggleGrooming: (Long, String, Boolean) -> Unit,
    onAddGroomingItem: (String, String) -> Unit,
    onToggleSkincare: (Long, String, Boolean) -> Unit,
    onAddSkincareProduct: (String, String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Grooming, 1: Skincare
    var selectedRoutineSubTab by remember { mutableStateOf("morning") } // morning, night, weekly
    var selectedSkincareSubTab by remember { mutableStateOf("AM") } // AM, PM, products
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "APPEARANCE & GROOMING",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Discipline in Presence & Care",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Main Tab Selector (Grooming vs Skincare)
        item {
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = PrimeSurface,
                contentColor = PrimePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                        color = PrimePrimary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, PrimeBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = { Text("Grooming Routines", fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = { Text("Skincare Protocol", fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (selectedMainTab == 0) {
            // Grooming Routines
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRoutineSubTab == "morning",
                        onClick = { selectedRoutineSubTab = "morning" },
                        label = { Text("Morning") },
                        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                    FilterChip(
                        selected = selectedRoutineSubTab == "night",
                        onClick = { selectedRoutineSubTab = "night" },
                        label = { Text("Night") },
                        leadingIcon = { Icon(Icons.Default.Nightlight, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                    FilterChip(
                        selected = selectedRoutineSubTab == "weekly",
                        onClick = { selectedRoutineSubTab = "weekly" },
                        label = { Text("Weekly Maintenance") },
                        leadingIcon = { Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }

            val currentItems = groomingItems.filter { it.routineType == selectedRoutineSubTab }
            val completedItemIds = groomingLogs.filter { it.routineType == selectedRoutineSubTab && it.isCompleted }.map { it.itemId }.toSet()
            val completionPct = if (currentItems.isNotEmpty()) (completedItemIds.size.toFloat() / currentItems.size) else 0f

            // Consistency / Progress Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${selectedRoutineSubTab.replaceFirstChar { it.uppercase() }} Routine Checklist",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${completedItemIds.size} of ${currentItems.size} completed (${(completionPct * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                        }
                        IconButton(
                            onClick = { showAddItemDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimePrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item", tint = PrimePrimary)
                        }
                    }
                }
            }

            // Items List
            items(currentItems, key = { it.id }) { item ->
                val isDone = completedItemIds.contains(item.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleGrooming(item.id, selectedRoutineSubTab, !isDone) }
                        .testTag("grooming_item_${item.id}"),
                    colors = CardDefaults.cardColors(containerColor = if (isDone) PrimeCardBg.copy(alpha = 0.6f) else PrimeCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isDone) PrimePrimary.copy(alpha = 0.5f) else PrimeBorder)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isDone) "Completed" else "Not Completed",
                            tint = if (isDone) PrimePrimary else PrimeTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isDone) PrimeTextMuted else Color.White,
                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // Skincare Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSkincareSubTab == "AM",
                        onClick = { selectedSkincareSubTab = "AM" },
                        label = { Text("AM Morning") },
                        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                    FilterChip(
                        selected = selectedSkincareSubTab == "PM",
                        onClick = { selectedSkincareSubTab = "PM" },
                        label = { Text("PM Evening") },
                        leadingIcon = { Icon(Icons.Default.Nightlight, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                    FilterChip(
                        selected = selectedSkincareSubTab == "products",
                        onClick = { selectedSkincareSubTab = "products" },
                        label = { Text("All Products") },
                        leadingIcon = { Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimePrimary,
                            selectedLabelColor = PrimeOnPrimaryDark
                        )
                    )
                }
            }

            // Wellness Guidance Disclaimer Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimeSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "General wellness guidance only. Consult a board-certified dermatologist for medical skin concerns or persistent conditions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimeTextSecondary
                        )
                    }
                }
            }

            if (selectedSkincareSubTab == "products") {
                // Products inventory list
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Product Cabinet (${skincareProducts.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Product")
                        }
                    }
                }

                items(skincareProducts, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimeCardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimeBorder))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = product.routineTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${product.category}${if (product.brand.isNotBlank()) " • " + product.brand else ""} • ${product.frequency}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimeTextSecondary
                            )
                            if (product.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimeTextMuted
                                )
                            }
                        }
                    }
                }
            } else {
                // AM or PM Routine Ordering Checklist
                val timeFilter = selectedSkincareSubTab
                val currentProducts = skincareProducts.filter { it.routineTime == timeFilter || it.routineTime == "BOTH" }
                val completedProductIds = skincareLogs.filter { it.routineTime == timeFilter && it.isCompleted }.map { it.productId }.toSet()

                items(currentProducts, key = { it.id }) { product ->
                    val isDone = completedProductIds.contains(product.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleSkincare(product.id, timeFilter, !isDone) }
                            .testTag("skincare_item_${product.id}"),
                        colors = CardDefaults.cardColors(containerColor = if (isDone) PrimeCardBg.copy(alpha = 0.6f) else PrimeCardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isDone) PrimePrimary.copy(alpha = 0.5f) else PrimeBorder)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isDone) PrimePrimary else PrimeTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isDone) PrimeTextMuted else Color.White,
                                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold
                                )
                                Text(
                                    text = "${product.category} • ${product.frequency}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimeTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Custom Grooming Item Dialog
    if (showAddItemDialog) {
        var newTitle by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddItemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Custom Grooming Item", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Item Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAddItemDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    onAddGroomingItem(newTitle, selectedRoutineSubTab)
                                    showAddItemDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

    // Add Skincare Product Dialog
    if (showAddProductDialog) {
        var pName by remember { mutableStateOf("") }
        var pCategory by remember { mutableStateOf("Cleanser") }
        var pBrand by remember { mutableStateOf("") }
        var pTime by remember { mutableStateOf("AM") }
        var pFreq by remember { mutableStateOf("Daily") }
        var pNotes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddProductDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PrimeCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Skincare Product", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Product Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pCategory,
                        onValueChange = { pCategory = it },
                        label = { Text("Category (e.g. Cleanser, Serum, SPF)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pBrand,
                        onValueChange = { pBrand = it },
                        label = { Text("Brand") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimePrimary,
                            unfocusedBorderColor = PrimeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAddProductDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = PrimeTextSecondary)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (pName.isNotBlank()) {
                                    onAddSkincareProduct(pName, pCategory, pBrand, pTime, pFreq, pNotes)
                                    showAddProductDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary, contentColor = PrimeOnPrimaryDark)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
