package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.LocationPoint
import com.example.ui.theme.TruckEmerald
import com.example.ui.theme.TruckEmeraldDark
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

enum class MapViewMode {
    GOOGLE_MAP,
    VECTOR_MAP
}

@Composable
fun TruckMateGoogleMap(
    modifier: Modifier = Modifier,
    pickupLocation: LocationPoint? = null,
    dropLocation: LocationPoint? = null,
    driverProgress: Float = 0.5f,
    isLiveTracking: Boolean = false,
    interactive: Boolean = true,
    showControls: Boolean = true,
    showModeToggle: Boolean = true,
    onMapClick: ((LatLng) -> Unit)? = null
) {
    var viewMode by remember { mutableStateOf(MapViewMode.GOOGLE_MAP) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isTrafficEnabled by remember { mutableStateOf(false) }

    // Default Dhaka Coordinates (Center: Farmgate / Karwan Bazar)
    val defaultDhakaCenter = LatLng(23.7561, 90.3872)

    val pickupLatLng = remember(pickupLocation) {
        pickupLocation?.let { LatLng(it.lat, it.lng) } ?: LatLng(23.7561, 90.3872)
    }

    val dropLatLng = remember(dropLocation) {
        dropLocation?.let { LatLng(it.lat, it.lng) } ?: LatLng(23.7465, 90.3760)
    }

    // Interpolate driver position between pickup and drop
    val driverLatLng = remember(pickupLatLng, dropLatLng, driverProgress) {
        val lat = pickupLatLng.latitude + (dropLatLng.latitude - pickupLatLng.latitude) * driverProgress
        val lng = pickupLatLng.longitude + (dropLatLng.longitude - pickupLatLng.longitude) * driverProgress
        LatLng(lat, lng)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            if (isLiveTracking) driverLatLng else pickupLatLng,
            13.5f
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // Smoothly update camera bounds when locations change
    LaunchedEffect(pickupLatLng, dropLatLng) {
        try {
            val builder = LatLngBounds.builder()
            builder.include(pickupLatLng)
            builder.include(dropLatLng)
            val bounds = builder.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 120),
                durationMs = 800
            )
        } catch (_: Exception) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(pickupLatLng, 13f)
            )
        }
    }

    val mapUiSettings by remember(interactive, showControls) {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = interactive,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                scrollGesturesEnabled = interactive,
                zoomGesturesEnabled = interactive,
                tiltGesturesEnabled = interactive,
                rotationGesturesEnabled = interactive
            )
        )
    }

    val mapProperties by remember(mapType, isTrafficEnabled) {
        mutableStateOf(
            MapProperties(
                mapType = mapType,
                isTrafficEnabled = isTrafficEnabled
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE2E8F0))
    ) {
        if (viewMode == MapViewMode.GOOGLE_MAP) {
            // Google Maps Compose View
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("google_map_view"),
                cameraPositionState = cameraPositionState,
                uiSettings = mapUiSettings,
                properties = mapProperties,
                onMapClick = { latLng ->
                    onMapClick?.invoke(latLng)
                }
            ) {
                // Pickup Location Marker (Green)
                Marker(
                    state = rememberMarkerState(position = pickupLatLng),
                    title = "Pickup: ${pickupLocation?.nameEn ?: "Farmgate"}",
                    snippet = pickupLocation?.areaEn ?: "Dhaka",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )

                // Drop Location Marker (Red)
                Marker(
                    state = rememberMarkerState(position = dropLatLng),
                    title = "Drop: ${dropLocation?.nameEn ?: "Dhanmondi"}",
                    snippet = dropLocation?.areaEn ?: "Dhaka",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )

                // Driver Truck Marker if in tracking
                if (isLiveTracking) {
                    Marker(
                        state = rememberMarkerState(position = driverLatLng),
                        title = "Driver Truck",
                        snippet = "Moving towards destination",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                // Route Polyline connecting Pickup and Drop
                Polyline(
                    points = listOf(pickupLatLng, driverLatLng, dropLatLng),
                    color = TruckEmerald,
                    width = 12f,
                    geodesic = true
                )
            }
        } else {
            // Fallback Vector map canvas
            StylizedInteractiveMap(
                modifier = Modifier.fillMaxSize(),
                pickupName = pickupLocation?.nameEn ?: "Pickup",
                dropName = dropLocation?.nameEn ?: "Drop",
                animatedProgress = driverProgress
            )
        }

        // Top Control Bar: Mode Toggle (Google Map / Vector) + Map Type & Traffic
        if (showModeToggle || showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Mode Pill (Google Map vs Vector)
                if (showModeToggle) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        shadowElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MapModeTab(
                                label = "Google Map",
                                isSelected = viewMode == MapViewMode.GOOGLE_MAP,
                                onClick = { viewMode = MapViewMode.GOOGLE_MAP }
                            )
                            MapModeTab(
                                label = "2D Vector",
                                isSelected = viewMode == MapViewMode.VECTOR_MAP,
                                onClick = { viewMode = MapViewMode.VECTOR_MAP }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Map Layers & Zoom Quick Action Buttons
                if (showControls && viewMode == MapViewMode.GOOGLE_MAP) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Traffic Toggle
                        Surface(
                            shape = CircleShape,
                            color = if (isTrafficEnabled) TruckEmerald else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(
                                onClick = { isTrafficEnabled = !isTrafficEnabled },
                                modifier = Modifier.fillMaxSize().testTag("map_traffic_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Traffic,
                                    contentDescription = "Toggle Traffic",
                                    tint = if (isTrafficEnabled) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Map Type (Normal / Satellite)
                        Surface(
                            shape = CircleShape,
                            color = if (mapType == MapType.SATELLITE) TruckEmerald else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    mapType = if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                                },
                                modifier = Modifier.fillMaxSize().testTag("map_layer_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Map Layers",
                                    tint = if (mapType == MapType.SATELLITE) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Recenter Button
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val builder = LatLngBounds.builder()
                                            builder.include(pickupLatLng)
                                            builder.include(dropLatLng)
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngBounds(builder.build(), 120)
                                            )
                                        } catch (_: Exception) {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(pickupLatLng, 13f)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize().testTag("map_recenter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Recenter",
                                    tint = TruckEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom status badge on Google Map
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isLiveTracking) Color(0xFF0284C7) else Color(0xFF10B981))
                )
                Text(
                    text = if (isLiveTracking) "Live GPS Active" else "Dhaka Real-Time Map",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MapModeTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) TruckEmerald else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
