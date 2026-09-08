package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.TruckEmerald
import com.example.ui.theme.TruckEmeraldDark
import com.example.ui.theme.TruckOrange
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapExplorerScreen(
    language: AppLanguage,
    locations: List<LocationPoint>,
    currentPickup: LocationPoint,
    currentDrop: LocationPoint,
    onSetPickup: (LocationPoint) -> Unit,
    onSetDrop: (LocationPoint) -> Unit,
    onProceedToBooking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isTrafficEnabled by remember { mutableStateOf(true) }
    var selectedLocation by remember { mutableStateOf<LocationPoint?>(currentPickup) }
    var customPinLatLng by remember { mutableStateOf<LatLng?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val defaultCenter = remember { LatLng(23.7561, 90.3872) } // Farmgate, Dhaka

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 12.8f)
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("map_explorer_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "গুগল ম্যাপ ও ঢাকা ট্র্যাকিং" else "Google Maps & Dhaka Tracking",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ম্যাপে ট্যাপ করে পিকআপ বা ড্রপ নির্বাচন করুন" else "Tap on map to set pickup or dropoff",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Map Type Toggle Button
                    IconButton(
                        onClick = {
                            mapType = when (mapType) {
                                MapType.NORMAL -> MapType.SATELLITE
                                MapType.SATELLITE -> MapType.TERRAIN
                                else -> MapType.NORMAL
                            }
                        },
                        modifier = Modifier.testTag("map_explorer_layers_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Map Type",
                            tint = TruckEmerald
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    // Selected Location Info
                    val activeLoc = selectedLocation ?: LocationPoint(
                        id = "custom",
                        nameEn = customPinLatLng?.let { "Lat: ${"%.4f".format(it.latitude)}, Lng: ${"%.4f".format(it.longitude)}" } ?: "Dhaka Location",
                        nameBn = "নির্বাচিত অবস্থান",
                        areaEn = "Dhaka, Bangladesh",
                        areaBn = "ঢাকা, বাংলাদেশ",
                        lat = customPinLatLng?.latitude ?: 23.7561,
                        lng = customPinLatLng?.longitude ?: 90.3872
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TruckEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TruckEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) activeLoc.nameBn else activeLoc.nameEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) activeLoc.areaBn else activeLoc.areaEn,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons: Set as Pickup, Set as Drop, and Proceed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSetPickup(activeLoc)
                                snackbarMessage = if (language == AppLanguage.BANGLA) "${activeLoc.nameBn} পিকআপ সেট করা হয়েছে" else "${activeLoc.nameEn} set as Pickup"
                            },
                            modifier = Modifier.weight(1f).testTag("set_as_pickup_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "পিকআপ করুন" else "Set Pickup",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onSetDrop(activeLoc)
                                snackbarMessage = if (language == AppLanguage.BANGLA) "${activeLoc.nameBn} ড্রপ সেট করা হয়েছে" else "${activeLoc.nameEn} set as Drop"
                            },
                            modifier = Modifier.weight(1f).testTag("set_as_drop_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444))
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ড্রপ করুন" else "Set Drop",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onProceedToBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("map_explorer_proceed_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald)
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "এই লোকেশনে ট্রাক বুক করুন" else "Book Truck for Route",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Google Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("google_map_explorer_view"),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = mapType,
                    isTrafficEnabled = isTrafficEnabled
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    rotationGesturesEnabled = true,
                    tiltGesturesEnabled = true
                ),
                onMapClick = { latLng ->
                    customPinLatLng = latLng
                    selectedLocation = LocationPoint(
                        id = "custom_${System.currentTimeMillis()}",
                        nameEn = "Pinned Location",
                        nameBn = "পিন করা অবস্থান",
                        areaEn = "Lat: ${"%.4f".format(latLng.latitude)}, Lng: ${"%.4f".format(latLng.longitude)}",
                        areaBn = "অক্ষাংশ: ${"%.4f".format(latLng.latitude)}, দ্রাঘিমাংশ: ${"%.4f".format(latLng.longitude)}",
                        lat = latLng.latitude,
                        lng = latLng.longitude
                    )
                }
            ) {
                // Known Dhaka Logistics Hubs & Locations Markers
                locations.forEach { loc ->
                    val isPickup = loc.id == currentPickup.id
                    val isDrop = loc.id == currentDrop.id
                    val markerColor = when {
                        isPickup -> BitmapDescriptorFactory.HUE_GREEN
                        isDrop -> BitmapDescriptorFactory.HUE_RED
                        else -> BitmapDescriptorFactory.HUE_AZURE
                    }
                    Marker(
                        state = rememberMarkerState(position = LatLng(loc.lat, loc.lng)),
                        title = loc.nameEn,
                        snippet = when {
                            isPickup -> "Current Pickup Location"
                            isDrop -> "Current Drop Location"
                            else -> loc.areaEn
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                        onClick = {
                            selectedLocation = loc
                            customPinLatLng = null
                            false
                        }
                    )
                }

                // Custom User Tapped Pin
                customPinLatLng?.let { latLng ->
                    Marker(
                        state = rememberMarkerState(position = latLng),
                        title = "Custom Pin",
                        snippet = "Selected coordinate",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                    )
                }

                // Route Polyline connecting current pickup and drop
                Polyline(
                    points = listOf(
                        LatLng(currentPickup.lat, currentPickup.lng),
                        LatLng(currentDrop.lat, currentDrop.lng)
                    ),
                    color = TruckEmerald,
                    width = 10f,
                    geodesic = true
                )
            }

            // Floating Chips at the Top for Quick Dhaka Landmarks
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(locations) { loc ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            shadowElevation = 3.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (loc.id == selectedLocation?.id) TruckEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.clickable {
                                selectedLocation = loc
                                customPinLatLng = null
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(LatLng(loc.lat, loc.lng), 14.5f)
                                    )
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (loc.id == selectedLocation?.id) TruckEmerald else Color(0xFF64748B))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) loc.nameBn else loc.nameEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Right floating action buttons: Traffic, Re-center, Zoom In, Zoom Out
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Traffic Toggle
                SmallFloatingActionButton(
                    onClick = { isTrafficEnabled = !isTrafficEnabled },
                    containerColor = if (isTrafficEnabled) TruckEmerald else MaterialTheme.colorScheme.surface,
                    contentColor = if (isTrafficEnabled) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("explorer_traffic_button")
                ) {
                    Icon(Icons.Default.Traffic, contentDescription = "Traffic Layer", modifier = Modifier.size(18.dp))
                }

                // Recenter to Pickup/Dhaka
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val target = selectedLocation?.let { LatLng(it.lat, it.lng) } ?: defaultCenter
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(target, 13.5f)
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = TruckEmerald,
                    modifier = Modifier.testTag("explorer_recenter_button")
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", modifier = Modifier.size(18.dp))
                }

                // Zoom In
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.zoomIn())
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("explorer_zoom_in_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                }

                // Zoom Out
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.zoomOut())
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("explorer_zoom_out_button")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                }
            }

            // Snackbar confirmation message if any
            snackbarMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TruckEmeraldDark,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(2200)
                    snackbarMessage = null
                }
            }
        }
    }
}
