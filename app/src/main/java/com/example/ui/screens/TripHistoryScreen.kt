package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Trip
import com.example.model.TripStatus
import com.example.ui.components.StatusPill
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun TripHistoryScreen(
    language: AppLanguage,
    trips: List<Trip>,
    onRebookTrip: (Trip) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTrips = remember(selectedFilter, trips) {
        when (selectedFilter) {
            "COMPLETED" -> trips.filter { it.status == TripStatus.COMPLETED }
            "CANCELLED" -> trips.filter { it.status == TripStatus.CANCELLED }
            else -> trips
        }
    }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("trip_history", language),
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
            Spacer(modifier = Modifier.height(6.dp))

            // Filter Tabs: [All] [Completed] [Cancelled]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HistoryFilterPill(
                    title = "All",
                    isSelected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" }
                )
                HistoryFilterPill(
                    title = "Completed",
                    isSelected = selectedFilter == "COMPLETED",
                    onClick = { selectedFilter = "COMPLETED" }
                )
                HistoryFilterPill(
                    title = "Cancelled",
                    isSelected = selectedFilter == "CANCELLED",
                    onClick = { selectedFilter = "CANCELLED" }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredTrips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trips found in this category.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredTrips) { trip ->
                        TripHistoryCard(
                            trip = trip,
                            language = language,
                            onRebook = { onRebookTrip(trip) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryFilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TruckEmerald else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("history_filter_$title")
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TripHistoryCard(
    trip: Trip,
    language: AppLanguage,
    onRebook: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_card_${trip.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.dateFormatted,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trip.status == TripStatus.COMPLETED) {
                    StatusPill(
                        text = AppStrings.t("completed_badge", language),
                        statusColor = StatusGreen,
                        backgroundColor = StatusGreenLight
                    )
                } else if (trip.status == TripStatus.CANCELLED) {
                    StatusPill(
                        text = AppStrings.t("cancelled_badge", language),
                        statusColor = StatusRed,
                        backgroundColor = StatusRedLight
                    )
                } else {
                    StatusPill(
                        text = trip.status.name,
                        statusColor = StatusAmber,
                        backgroundColor = StatusAmberLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route: Pickup -> Drop
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.booking.pickup.nameEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "to",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(16.dp)
                )
                Text(
                    text = trip.booking.drop.nameEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Vehicle • Fare & Rebook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${trip.booking.vehicle.nameEn} • ৳ ${trip.fare}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TruckEmeraldDark
                )

                Button(
                    onClick = onRebook,
                    colors = ButtonDefaults.buttonColors(containerColor = TruckMint),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = TruckEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppStrings.t("rebook", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TruckEmerald
                    )
                }
            }
        }
    }
}
