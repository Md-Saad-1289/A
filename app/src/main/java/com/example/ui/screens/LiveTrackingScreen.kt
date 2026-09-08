package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Trip
import com.example.model.TripStatus
import com.example.ui.components.StylizedInteractiveMap
import com.example.ui.components.TruckMateGoogleMap
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.*
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    language: AppLanguage,
    trip: Trip,
    onAdvanceStatus: () -> Unit,
    onCancelTrip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showChatSheet by remember { mutableStateOf(false) }
    var chatMessageInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            Pair("Driver", "Hello Sir, I am on my way to Farmgate."),
            Pair("Driver", "Traffic is clear, should reach in 8 minutes.")
        )
    }

    var showCancelDialog by remember { mutableStateOf(false) }

    val statusText = when (trip.status) {
        TripStatus.SEARCHING -> AppStrings.t("status_searching", language)
        TripStatus.ASSIGNED -> AppStrings.t("driver_assigned", language)
        TripStatus.ARRIVING -> AppStrings.t("status_arriving", language)
        TripStatus.IN_TRANSIT -> AppStrings.t("status_in_transit", language)
        TripStatus.COMPLETED -> AppStrings.t("status_completed", language)
        TripStatus.CANCELLED -> "Cancelled"
    }

    val progressValue = when (trip.status) {
        TripStatus.SEARCHING -> 0.1f
        TripStatus.ASSIGNED -> 0.35f
        TripStatus.ARRIVING -> 0.65f
        TripStatus.IN_TRANSIT -> 0.85f
        TripStatus.COMPLETED -> 1.0f
        TripStatus.CANCELLED -> 0.0f
    }

    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = "Trip #${trip.id}",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = TruckMintSubtle,
                border = BorderStroke(1.dp, TruckMint)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StatusGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = statusText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TruckEmeraldDark
                        )
                        Text(
                            text = AppStrings.t("driver_on_way", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Driver Profile Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Driver Avatar",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) trip.driver.nameBn else trip.driver.nameEn,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${trip.driver.rating}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${trip.driver.tripsCount} ${AppStrings.t("trips", language)})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = trip.driver.vehiclePlate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TruckEmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: Call, Chat, Cancel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Call
                        DriverActionButton(
                            icon = Icons.Default.Phone,
                            label = AppStrings.t("call", language),
                            tint = Color(0xFF10B981),
                            testTag = "driver_action_call",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${trip.driver.phone}")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )

                        // Chat
                        DriverActionButton(
                            icon = Icons.Default.Chat,
                            label = AppStrings.t("chat", language),
                            tint = Color(0xFF3B82F6),
                            testTag = "driver_action_chat",
                            onClick = { showChatSheet = true }
                        )

                        // Cancel
                        DriverActionButton(
                            icon = Icons.Default.Close,
                            label = AppStrings.t("cancel", language),
                            tint = Color(0xFFEF4444),
                            testTag = "driver_action_cancel",
                            onClick = { showCancelDialog = true }
                        )
                    }
                }
            }

            // Google Map Route & Live GPS Tracking View
            TruckMateGoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                pickupLocation = trip.booking.pickup,
                dropLocation = trip.booking.drop,
                driverProgress = progressValue,
                isLiveTracking = true,
                interactive = true,
                showControls = true,
                showModeToggle = true
            )

            // ETA and Distance Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppStrings.t("arriving_in", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when (trip.status) {
                                TripStatus.SEARCHING -> "--"
                                TripStatus.ASSIGNED -> "8 min"
                                TripStatus.ARRIVING -> "3 min"
                                TripStatus.IN_TRANSIT -> "In Transit"
                                TripStatus.COMPLETED -> "Arrived"
                                TripStatus.CANCELLED -> "Cancelled"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TruckEmeraldDark
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AppStrings.t("distance", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${trip.booking.distanceKm} km",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Timeline Stepper (Searching -> Assigned -> Arriving -> In Transit -> Completed)
            TripStatusTimeline(
                currentStatus = trip.status,
                language = language
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Simulation Helper Button
            Button(
                onClick = onAdvanceStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("simulate_trip_status_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TruckOrange)
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${AppStrings.t("simulate_next", language)} (Next: ${trip.status.name})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // In-app Driver Chat Bottom Sheet
    if (showChatSheet) {
        ModalBottomSheet(onDismissRequest = { showChatSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = TruckEmerald
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Chat with ${trip.driver.nameEn}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { msg ->
                        val isUser = msg.first == "User"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isUser) TruckEmerald else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = msg.second,
                                    fontSize = 14.sp,
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatMessageInput,
                        onValueChange = { chatMessageInput = it },
                        placeholder = { Text("Type message...") },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("driver_chat_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatMessageInput.isNotBlank()) {
                                chatMessages.add(Pair("User", chatMessageInput))
                                chatMessageInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TruckEmerald)
                            .testTag("driver_chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Cancel Trip Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Trip?") },
            text = { Text("Are you sure you want to cancel this booking? A cancellation fee may apply if the driver has arrived.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelTrip()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Yes, Cancel", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Trip")
                }
            }
        )
    }
}

@Composable
fun DriverActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TripStatusTimeline(
    currentStatus: TripStatus,
    language: AppLanguage
) {
    val steps = listOf(
        Pair(TripStatus.SEARCHING, AppStrings.t("status_searching", language)),
        Pair(TripStatus.ASSIGNED, AppStrings.t("status_assigned", language)),
        Pair(TripStatus.ARRIVING, AppStrings.t("status_arriving", language)),
        Pair(TripStatus.IN_TRANSIT, AppStrings.t("status_in_transit", language)),
        Pair(TripStatus.COMPLETED, AppStrings.t("status_completed", language))
    )

    val currentIndex = steps.indexOfFirst { it.first == currentStatus }.coerceAtLeast(0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, pair ->
            val isPassed = index <= currentIndex
            val isCurrent = index == currentIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> TruckOrange
                                isPassed -> TruckEmerald
                                else -> Color(0xFFCBD5E1)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed && !isCurrent) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = pair.second,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) TruckEmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
