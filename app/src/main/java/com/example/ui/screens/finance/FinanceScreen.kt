package com.example.ui.screens.finance

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.entities.FinanceTransactionEntity
import com.example.data.local.entities.FinancialGoalEntity
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
import com.example.ui.theme.PrimeRed
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceScreen(
    transactions: List<FinanceTransactionEntity>,
    financialGoals: List<FinancialGoalEntity>,
    userCurrency: String,
    onAddTransaction: (type: String, category: String, amount: Double, notes: String, isRecurring: Boolean) -> Unit,
    onAddGoal: (title: String, goalType: String, targetAmount: Double, currentAmount: Double, deadline: String) -> Unit,
    onUpdateGoalProgress: (goal: FinancialGoalEntity, addedAmount: Double) -> Unit,
    onDeleteTransaction: (FinanceTransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Monthly Cash Flow", "Financial Goals", "Ledger")

    var showAddTxDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    // Calculations
    val totalIncome = transactions.filter { it.type == "Income" || it.type == "Business Income" }.sumOf { it.amount }
    val totalExpenses = transactions.filter { it.type == "Expense" || it.type == "Business Expense" }.sumOf { it.amount }
    val totalSavings = transactions.filter { it.type == "Savings" }.sumOf { it.amount }
    val netCashFlow = totalIncome - totalExpenses
    val savingsRate = if (totalIncome > 0) ((totalSavings + maxOf(0.0, netCashFlow)) / totalIncome * 100).toInt().coerceIn(0, 100) else 0

    val currencyFormatter = remember(userCurrency) {
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            currency = try {
                java.util.Currency.getInstance(userCurrency)
            } catch (e: Exception) {
                java.util.Currency.getInstance("MYR")
            }
        }
    }

    fun formatMoney(amount: Double): String {
        return "$userCurrency ${String.format(Locale.US, "%,.2f", amount)}"
    }

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
                modifier = Modifier.fillMaxWidth().testTag("finance_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WAR CHEST & CAPITAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Financial Mastery & Runway",
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
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Net cash flow headline
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimeSurface,
                        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Monthly Net Cash Flow", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                Text(
                                    text = formatMoney(netCashFlow),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = if (netCashFlow >= 0) PrimeGreen else PrimeRed
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimeGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$savingsRate% Savings Rate",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Metric columns
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = PrimeGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Income", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                }
                                Text(
                                    text = formatMoney(totalIncome),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeGreen,
                                    maxLines = 1
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = PrimeRed, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Expenses", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                }
                                Text(
                                    text = formatMoney(totalExpenses),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeRed,
                                    maxLines = 1
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Savings, contentDescription = null, tint = PrimeBlue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Saved", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                }
                                Text(
                                    text = formatMoney(totalSavings),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeBlue,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legal Disclaimer banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = PrimeTextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "For self-quantification & personal discipline only. No regulated financial advice.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = PrimeTextSecondary
                        )
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
                    .testTag("finance_tabs")
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
                // Monthly Cash Flow summary & quick log
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cash Flow Allocation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddTxDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_transaction_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Cash Flow", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Category Breakdowns
                item {
                    val expenseMap = transactions.filter { it.type == "Expense" || it.type == "Business Expense" }
                        .groupBy { it.category }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }

                    if (expenseMap.isEmpty()) {
                        EmptyFinanceState(
                            title = "No Expenses Logged This Month",
                            subtitle = "Keep an ironclad budget by recording expenses and tracking your savings margin.",
                            onAdd = { showAddTxDialog = true }
                        )
                    } else {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Top Expense Categories",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimeTextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                expenseMap.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                                    val percent = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0f
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(category, style = MaterialTheme.typography.bodySmall, color = PrimeTextPrimary)
                                            Text(formatMoney(amount), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { percent },
                                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(2.5.dp)),
                                            color = PrimeOrange,
                                            trackColor = PrimeBorder.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Financial Goals
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Capital Reserves & Milestones",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddGoalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_financial_goal_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Goal", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (financialGoals.isEmpty()) {
                    item {
                        EmptyFinanceState(
                            title = "No Capital Goals Configured",
                            subtitle = "Create 6-month emergency funds, business investment targets, or major asset goals.",
                            onAdd = { showAddGoalDialog = true }
                        )
                    }
                } else {
                    items(financialGoals) { goal ->
                        FinancialGoalCard(
                            goal = goal,
                            currency = userCurrency,
                            onAddFunds = { added -> onUpdateGoalProgress(goal, added) }
                        )
                    }
                }
            }

            2 -> {
                // Ledger
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddTxDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Entry", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (transactions.isEmpty()) {
                    item {
                        EmptyFinanceState(
                            title = "No Ledger Entries",
                            subtitle = "All cash movements, subscriptions, and investments will appear here.",
                            onAdd = { showAddTxDialog = true }
                        )
                    }
                } else {
                    items(transactions) { tx ->
                        FinanceTransactionRow(
                            tx = tx,
                            currency = userCurrency,
                            onDelete = { onDeleteTransaction(tx) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddTxDialog) {
        AddTransactionDialog(
            userCurrency = userCurrency,
            onDismiss = { showAddTxDialog = false },
            onAdd = { type, cat, amount, notes, recurring ->
                onAddTransaction(type, cat, amount, notes, recurring)
                showAddTxDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddFinancialGoalDialog(
            userCurrency = userCurrency,
            onDismiss = { showAddGoalDialog = false },
            onAdd = { title, type, target, current, deadline ->
                onAddGoal(title, type, target, current, deadline)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun FinancialGoalCard(
    goal: FinancialGoalEntity,
    currency: String,
    onAddFunds: (Double) -> Unit
) {
    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("finance_goal_card_${goal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.goalType.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = when (goal.goalType) {
                            "Emergency Fund" -> PrimeGreen
                            "Business Goal" -> PrimeBlue
                            else -> PrimePrimary
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimeGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimeGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimeGreen,
                trackColor = PrimeBorder.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currency ${String.format(Locale.US, "%,.0f", goal.currentAmount)} of $currency ${String.format(Locale.US, "%,.0f", goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimeTextSecondary
                )

                Button(
                    onClick = { onAddFunds(500.0) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("+500", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun FinanceTransactionRow(
    tx: FinanceTransactionEntity,
    currency: String,
    onDelete: () -> Unit
) {
    val isPositive = tx.type == "Income" || tx.type == "Business Income"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth().testTag("tx_row_${tx.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPositive) PrimeGreen.copy(alpha = 0.15f) else PrimeRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isPositive) PrimeGreen else PrimeRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (tx.notes.isNotBlank()) tx.notes else tx.category,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimeTextPrimary
                    )
                    Text(
                        text = "${tx.type} • ${tx.category} • ${tx.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )
                }
            }

            Text(
                text = "${if (isPositive) "+" else "-"} $currency ${String.format(Locale.US, "%,.2f", tx.amount)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isPositive) PrimeGreen else PrimeRed
            )
        }
    }
}

@Composable
fun EmptyFinanceState(title: String, subtitle: String, onAdd: () -> Unit) {
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
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(36.dp))
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
                Text("Log Entry", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun AddTransactionDialog(
    userCurrency: String,
    onDismiss: () -> Unit,
    onAdd: (type: String, category: String, amount: Double, notes: String, isRecurring: Boolean) -> Unit
) {
    var type by remember { mutableStateOf("Expense") }
    var category by remember { mutableStateOf("Food") }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val types = listOf("Expense", "Income", "Savings", "Business Expense", "Business Income")
    val expenseCategories = listOf("Food", "Housing", "Transport", "Health & Fitness", "Tech & Tools", "Utilities", "Personal", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_transaction_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Log Cash Flow", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Expense", "Income", "Savings").forEach { t ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (type == t) PrimePrimary else PrimeSurfaceVariant,
                            border = BorderStroke(1.dp, if (type == t) PrimePrimary else PrimeBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { type = t }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (type == t) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (type == t) PrimeOnPrimaryDark else PrimeTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($userCurrency)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Description / Merchant") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAdd(type, category, amount, notes, false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_transaction_button")
                ) {
                    Text("Save Entry (+20 XP)", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddFinancialGoalDialog(
    userCurrency: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, type: String, target: Double, current: Double, deadline: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var goalType by remember { mutableStateOf("Emergency Fund") }
    var targetText by remember { mutableStateOf("") }
    var currentText by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_financial_goal_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Create Capital Goal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title (e.g. 6-Month Runway)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Target Amount ($userCurrency)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val target = targetText.toDoubleOrNull() ?: 0.0
                        val current = currentText.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && target > 0) {
                            onAdd(title, goalType, target, current, "2026")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Goal (+50 XP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
