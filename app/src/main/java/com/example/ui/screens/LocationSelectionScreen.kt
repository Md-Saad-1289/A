package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.LocationPoint
import com.example.ui.components.StylizedInteractiveMap
import com.example.ui.components.TruckMateGoogleMap
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationSelectionScreen(
    language: AppLanguage,
    locations: List<LocationPoint>,
    pickupLocation: LocationPoint,
    dropLocation: LocationPoint,
    onPickupSelect: (LocationPoint) -> Unit,
    onDropSelect: (LocationPoint) -> Unit,
    onSwapLocations: () -> Unit,
    onNextClick: () -> Unit,
    onOpenFullMap: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSelectingPickup by remember { mutableStateOf(false) }
    var isSelectingDrop by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("pickup_drop_location", language),
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
                        onClick = onNextClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("location_selection_next_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald)
                    ) {
                        Text(
                            text = AppStrings.t("next", language),
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Pickup & Drop Selector Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Pickup Location Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSelectingPickup = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.t("pickup_location", language),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) pickupLocation.areaBn else pickupLocation.areaEn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        // Divider Line with Dots
                        Row(
                            modifier = Modifier.padding(start = 6.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(Color(0xFFCBD5E1))
                            ) {}
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 40.dp)
                                    .weight(1f),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }

                        // Drop Location Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSelectingDrop = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.t("drop_location", language),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) dropLocation.areaBn else dropLocation.areaEn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // Swap Button on the right
                    IconButton(
                        onClick = onSwapLocations,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("swap_locations_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Locations",
                            tint = TruckEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick City Area Chips
            Text(
                text = "Quick Select Areas",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(locations) { loc ->
                    val isPickup = loc.id == pickupLocation.id
                    val isDrop = loc.id == dropLocation.id
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when {
                            isPickup -> Color(0xFFE8F5E9)
                            isDrop -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when {
                                isPickup -> Color(0xFF10B981)
                                isDrop -> Color(0xFFEF4444)
                                else -> Color.Transparent
                            }
                        ),
                        modifier = Modifier.clickable {
                            if (!isPickup) onDropSelect(loc) else onPickupSelect(loc)
                        }
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) loc.nameBn else loc.nameEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map Header Bar with Fullscreen Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = TruckEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "লাইভ গুগল ম্যাপ" else "Live Google Map",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = if (language == AppLanguage.BANGLA) "ম্যাপ বড় করুন ↗" else "Fullscreen Map ↗",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TruckEmerald,
                    modifier = Modifier
                        .clickable { onOpenFullMap() }
                        .testTag("location_fullscreen_map_button")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Google Map View
            TruckMateGoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pickupLocation = pickupLocation,
                dropLocation = dropLocation,
                driverProgress = 0.5f,
                isLiveTracking = false,
                interactive = true,
                showControls = true,
                showModeToggle = true,
                onMapClick = { clickedLatLng ->
                    // Find nearest known location or update drop
                    val nearest = locations.minByOrNull {
                        val dLat = it.lat - clickedLatLng.latitude
                        val dLng = it.lng - clickedLatLng.longitude
                        dLat * dLat + dLng * dLng
                    }
                    if (nearest != null) {
                        onDropSelect(nearest)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Modal Sheet for Picking Pickup
    if (isSelectingPickup) {
        LocationPickerSheet(
            title = AppStrings.t("pickup_location", language),
            locations = locations,
            selected = pickupLocation,
            language = language,
            onSelect = {
                onPickupSelect(it)
                isSelectingPickup = false
            },
            onDismiss = { isSelectingPickup = false }
        )
    }

    // Modal Sheet for Picking Drop
    if (isSelectingDrop) {
        LocationPickerSheet(
            title = AppStrings.t("drop_location", language),
            locations = locations,
            selected = dropLocation,
            language = language,
            onSelect = {
                onDropSelect(it)
                isSelectingDrop = false
            },
            onDismiss = { isSelectingDrop = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    title: String,
    locations: List<LocationPoint>,
    selected: LocationPoint,
    language: AppLanguage,
    onSelect: (LocationPoint) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            locations.forEach { loc ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(loc) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (loc.id == selected.id) TruckEmerald else Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) loc.nameBn else loc.nameEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) loc.areaBn else loc.areaEn,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
