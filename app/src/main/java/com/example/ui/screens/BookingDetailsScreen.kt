package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BookingRequest
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.TruckEmerald
import com.example.ui.theme.TruckMint
import com.example.ui.theme.TruckOrange
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    language: AppLanguage,
    booking: BookingRequest,
    onChangeVehicle: () -> Unit,
    onChangeLocations: () -> Unit,
    onUpdateDetails: (dateTime: String?, goods: String?, service: String?, instruction: String?) -> Unit,
    onGetFareEstimate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var specialInstruction by remember { mutableStateOf(booking.specialInstruction) }
    var selectedServiceType by remember { mutableStateOf(booking.serviceType) } // "Open Pickup" or "House Moving"
    var showDatePickerSheet by remember { mutableStateOf(false) }
    var showGoodsPickerSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("booking_details", language),
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onUpdateDetails(null, null, selectedServiceType, specialInstruction)
                            onGetFareEstimate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("get_fare_estimate_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald)
                    ) {
                        Text(
                            text = AppStrings.t("get_fare_estimate", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
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

            // Vehicle Type Row
            DetailRowItem(
                icon = Icons.Default.LocalShipping,
                iconTint = TruckEmerald,
                title = AppStrings.t("vehicle_type", language),
                value = if (language == AppLanguage.BANGLA) booking.vehicle.nameBn else booking.vehicle.nameEn,
                changeLabel = AppStrings.t("change", language),
                onChangeClick = onChangeVehicle,
                testTag = "change_vehicle_button"
            )

            // Pickup Location Row
            DetailRowItem(
                icon = Icons.Default.LocationOn,
                iconTint = Color(0xFF10B981),
                title = AppStrings.t("pickup_location", language),
                value = if (language == AppLanguage.BANGLA) booking.pickup.areaBn else booking.pickup.areaEn,
                changeLabel = AppStrings.t("change", language),
                onChangeClick = onChangeLocations,
                testTag = "change_pickup_button"
            )

            // Drop Location Row
            DetailRowItem(
                icon = Icons.Default.LocationOn,
                iconTint = Color(0xFFEF4444),
                title = AppStrings.t("drop_location", language),
                value = if (language == AppLanguage.BANGLA) booking.drop.areaBn else booking.drop.areaEn,
                changeLabel = AppStrings.t("change", language),
                onChangeClick = onChangeLocations,
                testTag = "change_drop_button"
            )

            // Date & Time Row
            DetailRowItem(
                icon = Icons.Default.CalendarToday,
                iconTint = Color(0xFF3B82F6),
                title = AppStrings.t("date_time", language),
                value = "${booking.dateString}, ${booking.timeString}",
                changeLabel = AppStrings.t("change", language),
                onChangeClick = { showDatePickerSheet = true },
                testTag = "change_datetime_button"
            )

            // Goods Category Row
            DetailRowItem(
                icon = Icons.Default.Inventory2,
                iconTint = Color(0xFFF59E0B),
                title = AppStrings.t("goods_category", language),
                value = booking.goodsCategory,
                changeLabel = AppStrings.t("change", language),
                onChangeClick = { showGoodsPickerSheet = true },
                testTag = "change_goods_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Service Type
            Text(
                text = AppStrings.t("service_type", language),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServiceTypePill(
                    title = AppStrings.t("open_pickup", language),
                    isSelected = selectedServiceType == "Open Pickup",
                    onClick = { selectedServiceType = "Open Pickup" },
                    modifier = Modifier.weight(1f)
                )
                ServiceTypePill(
                    title = AppStrings.t("house_moving", language),
                    isSelected = selectedServiceType == "House Moving",
                    onClick = { selectedServiceType = "House Moving" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Special instruction
            Text(
                text = AppStrings.t("special_instruction", language),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = specialInstruction,
                onValueChange = { specialInstruction = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("special_instruction_input"),
                placeholder = { Text(AppStrings.t("special_placeholder", language)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TruckEmerald,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Date & Time Picker Sheet
    if (showDatePickerSheet) {
        ModalBottomSheet(onDismissRequest = { showDatePickerSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Date & Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf(
                    "Today, Immediate",
                    "Today, 02:00 PM",
                    "Today, 05:00 PM",
                    "Tomorrow, 10:00 AM",
                    "25 Apr 2025, 10:00 AM",
                    "26 Apr 2025, 11:30 AM"
                ).forEach { slot ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val parts = slot.split(", ")
                                val d = parts.getOrNull(0) ?: "Today"
                                val t = parts.getOrNull(1) ?: "10:00 AM"
                                onUpdateDetails(d, null, null, null)
                                showDatePickerSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TruckEmerald
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = slot, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Goods Picker Sheet
    if (showGoodsPickerSheet) {
        ModalBottomSheet(onDismissRequest = { showGoodsPickerSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Goods Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf(
                    "Household Items",
                    "Furniture & Appliances",
                    "Commercial Goods",
                    "Construction Materials",
                    "Electronics & Fragile",
                    "Office Relocation"
                ).forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUpdateDetails(null, cat, null, null)
                                showGoodsPickerSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = TruckOrange
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = cat, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DetailRowItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    changeLabel: String,
    onChangeClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
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
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.12f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Text(
                text = changeLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TruckEmerald,
                modifier = Modifier
                    .clickable { onChangeClick() }
                    .testTag(testTag)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ServiceTypePill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) TruckEmerald else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag("service_type_$title")
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else MaterialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
