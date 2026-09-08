package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BookingRequest
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.TruckEmerald
import com.example.ui.theme.TruckEmeraldDark
import com.example.ui.theme.TruckOrange
import com.example.util.AppStrings

@Composable
fun FareEstimateScreen(
    language: AppLanguage,
    booking: BookingRequest,
    onBookNow: () -> Unit,
    onTryBidding: () -> Unit,
    onApplyPromo: (String) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promoInput by remember { mutableStateOf(booking.promoCode) }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var promoSuccess by remember { mutableStateOf(booking.discount > 0) }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("fare_estimate", language),
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onBookNow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("fare_book_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald)
                    ) {
                        Text(
                            text = AppStrings.t("book_now", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = onTryBidding,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("fare_try_bidding_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, TruckEmerald)
                    ) {
                        Text(
                            text = AppStrings.t("try_bidding", language),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TruckEmerald
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle preview card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = booking.vehicle.imageRes),
                            contentDescription = booking.vehicle.nameEn,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) booking.vehicle.nameBn else booking.vehicle.nameEn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) booking.vehicle.subtitleBn else booking.vehicle.subtitleEn,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Estimated Fare Big Header
            Column {
                Text(
                    text = AppStrings.t("estimated_fare", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TruckEmeraldDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${booking.totalFare}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = TruckEmeraldDark,
                        letterSpacing = (-1).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Fare Breakdown Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    FareRow(
                        label = AppStrings.t("base_fare", language),
                        amount = "৳ ${booking.vehicle.baseFare}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FareRow(
                        label = "${AppStrings.t("per_km", language)} (${booking.distanceKm} km)",
                        amount = "৳ ${booking.distanceFare}"
                    )

                    if (booking.discount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FareRow(
                            label = AppStrings.t("promo_discount", language),
                            amount = "- ৳ ${booking.discount}",
                            amountColor = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(14.dp))

                    FareRow(
                        label = AppStrings.t("total", language),
                        amount = "৳ ${booking.totalFare}",
                        isTotal = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtext note
            Text(
                text = AppStrings.t("fare_note", language),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Promo Code Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = TruckOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.t("promo_code", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoInput,
                            onValueChange = { promoInput = it },
                            placeholder = { Text("e.g. FIRST50 or SAVE100") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("promo_code_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TruckEmerald,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val ok = onApplyPromo(promoInput)
                                promoSuccess = ok
                                promoMessage = if (ok) AppStrings.t("promo_applied", language) else "Invalid Code"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("apply_promo_button")
                        ) {
                            Text(AppStrings.t("apply", language), fontSize = 13.sp)
                        }
                    }

                    if (promoMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = promoMessage!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (promoSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FareRow(
    label: String,
    amount: String,
    isTotal: Boolean = false,
    amountColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = amountColor ?: if (isTotal) TruckEmeraldDark else MaterialTheme.colorScheme.onBackground
        )
    }
}
