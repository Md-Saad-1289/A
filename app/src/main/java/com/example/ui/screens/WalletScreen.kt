package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.WalletTransaction
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.*
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    language: AppLanguage,
    balance: Int,
    transactions: List<WalletTransaction>,
    onAddMoney: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddMoneySheet by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var showTransactionsSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("wallet", language),
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Main Balance Card (Emerald Green)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(20.dp),
                color = TruckEmerald,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "৳ $balance",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = AppStrings.t("available_balance", language),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showAddMoneySheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("wallet_add_money_button")
                        ) {
                            Text(
                                text = AppStrings.t("add_money", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TruckEmeraldDark
                            )
                        }

                        OutlinedButton(
                            onClick = { showTransactionsSheet = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("wallet_transactions_button")
                        ) {
                            Text(
                                text = AppStrings.t("transactions", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Payment Methods Header
            Text(
                text = AppStrings.t("payment_methods", language),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Methods List
            PaymentMethodRow(
                title = AppStrings.t("bkash", language),
                subtitle = "Linked: 01712-345678",
                tag = "bKash",
                tagColor = Color(0xFFD81B60),
                isSelected = selectedMethod == "bKash",
                onClick = { selectedMethod = "bKash" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                title = AppStrings.t("cash_on_delivery", language),
                subtitle = "Pay directly to driver after trip completion",
                tag = "COD",
                tagColor = TruckEmerald,
                isSelected = selectedMethod == "Cash on Delivery",
                onClick = { selectedMethod = "Cash on Delivery" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                title = AppStrings.t("nagad", language),
                subtitle = "Fast digital payment",
                tag = "Nagad",
                tagColor = Color(0xFFF97316),
                isSelected = selectedMethod == "Nagad",
                onClick = { selectedMethod = "Nagad" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                title = AppStrings.t("card", language),
                subtitle = "Visa, Mastercard",
                tag = "Cards",
                tagColor = Color(0xFF2563EB),
                isSelected = selectedMethod == "Cards",
                onClick = { selectedMethod = "Cards" }
            )
        }
    }

    // Add Money Modal Sheet
    if (showAddMoneySheet) {
        ModalBottomSheet(onDismissRequest = { showAddMoneySheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Add Money to TruckMate Wallet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(200, 500, 1000, 2000).forEach { amount ->
                        Button(
                            onClick = {
                                onAddMoney(amount)
                                showAddMoneySheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TruckMint),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "+৳$amount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TruckEmeraldDark
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Transactions History Sheet
    if (showTransactionsSheet) {
        ModalBottomSheet(onDismissRequest = { showTransactionsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Transaction History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) tx.titleBn else tx.titleEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = tx.date,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${if (tx.isCredit) "+" else "-"} ৳ ${tx.amount}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.isCredit) StatusGreen else StatusRed
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("payment_method_$tag"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.5.dp,
            if (isSelected) TruckEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tagColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tag.take(2),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = TruckEmerald)
            )
        }
    }
}
