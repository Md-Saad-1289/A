package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.TruckMateRepository
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.model.Trip
import com.example.model.Vehicle
import com.example.ui.components.TruckMateGoogleMap
import com.example.ui.theme.*
import com.example.util.AppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    language: AppLanguage,
    vehicles: List<Vehicle>,
    activeTrip: Trip?,
    unreadNotificationsCount: Int,
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onOpenMap: () -> Unit = {},
    onSelectVehicleCategory: (String) -> Unit,
    onVehicleClick: (Vehicle) -> Unit,
    onTrackActiveTrip: () -> Unit,
    onQuickBookRoute: (LocationPoint, LocationPoint, Vehicle?) -> Unit = { _, _, _ -> },
    onSelectSolution: (String, Vehicle) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf("Dhaka") }
    var cityMenuExpanded by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var copiedPromoNotification by remember { mutableStateOf<String?>(null) }

    // Instant Fare Calculator State
    val allLocations = remember { TruckMateRepository.locations }
    var calcPickup by remember { mutableStateOf(allLocations[0]) } // Farmgate
    var calcDrop by remember { mutableStateOf(allLocations[2]) }   // Dhanmondi
    var calcVehicle by remember { mutableStateOf(vehicles.firstOrNull() ?: TruckMateRepository.vehicles[0]) }
    var pickupDropdownExpanded by remember { mutableStateOf(false) }
    var dropDropdownExpanded by remember { mutableStateOf(false) }

    // Distance calculation for calculator
    val estimatedDistanceKm = remember(calcPickup, calcDrop) {
        val dLat = Math.toRadians(calcDrop.lat - calcPickup.lat)
        val dLng = Math.toRadians(calcDrop.lng - calcPickup.lng)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(calcPickup.lat)) * cos(Math.toRadians(calcDrop.lat)) *
                sin(dLng / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val rawDist = 6371.0 * c * 1.35 // Dhaka city routing factor
        max(2.5, round(rawDist * 10.0) / 10.0)
    }

    val estimatedFare = remember(calcVehicle, estimatedDistanceKm) {
        calcVehicle.baseFare + (calcVehicle.perKmRate * estimatedDistanceKm).toInt()
    }

    val estimatedMinutes = remember(estimatedDistanceKm) {
        (estimatedDistanceKm * 4.2).toInt().coerceIn(12, 60)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Hamburger menu
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.testTag("home_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Center: City dropdown selector
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.clickable { cityMenuExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TruckEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedCity,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select City",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = cityMenuExpanded,
                            onDismissRequest = { cityMenuExpanded = false }
                        ) {
                            listOf("Dhaka", "Chittagong", "Sylhet", "Gazipur", "Narayanganj").forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        selectedCity = city
                                        cityMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Right action icons: 24/7 Helpline, Google Map, Notifications
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 24/7 Helpline Phone Button
                        IconButton(
                            onClick = { showSupportDialog = true },
                            modifier = Modifier.testTag("home_helpline_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = "24/7 Helpline",
                                tint = Color(0xFF0284C7)
                            )
                        }

                        // Google Map Explorer Button
                        IconButton(
                            onClick = onOpenMap,
                            modifier = Modifier.testTag("home_map_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Open Google Map",
                                tint = TruckEmerald
                            )
                        }

                        // Notifications Bell with Badge
                        Box {
                            IconButton(
                                onClick = onNotificationsClick,
                                modifier = Modifier.testTag("home_notifications_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            if (unreadNotificationsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 8.dp)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TruckOrange)
                                )
                            }
                        }
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 32.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Welcome User Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "শুভ দিন, ${TruckMateRepository.userName} 👋" else "Hello, ${TruckMateRepository.userName} 👋",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "সহজ ও নিরাপদ পণ্য পরিবহনের বিশ্বস্ত সঙ্গী" else "Reliable cargo and moving partner",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TruckEmerald.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(TruckEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ফ্লিট সক্রিয়" else "Fleet Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TruckEmeraldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Trip Banner (if any active trip)
                if (activeTrip != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { onTrackActiveTrip() }
                            .testTag("home_active_trip_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = TruckEmeraldDark,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "চলমান ট্রিপ (${activeTrip.status.name})" else "Active Trip (${activeTrip.status.name})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${activeTrip.driver.nameEn} • ${activeTrip.booking.vehicle.nameEn}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onTrackActiveTrip,
                                colors = ButtonDefaults.buttonColors(containerColor = TruckOrange),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ট্র্যাক" else "Track",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Big Hero Banner Card: "Need a Truck or Pickup?"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectVehicleCategory("truck") }
                        .testTag("home_hero_banner"),
                    color = TruckEmerald,
                    shadowElevation = 2.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1.2f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "⚡ দ্রুত বুকিং ও বিডিং" else "⚡ Fast Booking & Bidding",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = AppStrings.t("need_truck", language),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 26.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = AppStrings.t("book_minutes", language),
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "এখনই বুক করুন" else "Book Now",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TruckEmeraldDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Start Booking",
                                        tint = TruckEmeraldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Banner Truck Image
                            Box(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(145.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_truck_banner),
                                    contentDescription = "Truck Banner",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Quick Services Row (4 circles: Truck, Pickup, Bidding, Fast Booking)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ServiceCategoryItem(
                        title = AppStrings.t("truck", language),
                        icon = Icons.Default.LocalShipping,
                        containerColor = TruckMint,
                        iconTint = TruckEmerald,
                        testTag = "category_truck",
                        onClick = { onSelectVehicleCategory("truck") }
                    )
                    ServiceCategoryItem(
                        title = AppStrings.t("pickup", language),
                        icon = Icons.Default.DirectionsCar,
                        containerColor = TruckOrangeLight,
                        iconTint = TruckOrange,
                        testTag = "category_pickup",
                        onClick = { onSelectVehicleCategory("pickup") }
                    )
                    ServiceCategoryItem(
                        title = AppStrings.t("bidding", language),
                        icon = Icons.Default.Gavel,
                        containerColor = TruckBlueLight,
                        iconTint = TruckBlue,
                        testTag = "category_bidding",
                        onClick = { onSelectVehicleCategory("bidding") }
                    )
                    ServiceCategoryItem(
                        title = AppStrings.t("fast_booking", language),
                        icon = Icons.Default.Bolt,
                        containerColor = TruckPurpleLight,
                        iconTint = TruckPurple,
                        testTag = "category_fast_booking",
                        onClick = { onSelectVehicleCategory("fast") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Popular Vehicles Section Header (Right after Hero & Quick Categories)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.t("popular_vehicles", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${AppStrings.t("see_all", language)} >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TruckEmerald,
                        modifier = Modifier
                            .clickable { onSelectVehicleCategory("all") }
                            .testTag("see_all_vehicles_link")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Popular Vehicles Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    vehicles.take(2).forEach { vehicle ->
                        PopularVehicleCard(
                            vehicle = vehicle,
                            language = language,
                            modifier = Modifier.weight(1f),
                            onClick = { onVehicleClick(vehicle) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // INSTANT FARE ESTIMATOR WIDGET (তাৎক্ষণিক ভাড়া পরিমাপক)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_fare_calculator_card"),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TruckEmerald.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = TruckEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.t("fare_calculator", language),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "যেকোনো রুটের আনুমানিক দূরত্ব ও ভাড়া দেখুন" else "Estimate trip distance & fare instantly",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pickup Location Field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .clickable { pickupDropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "পিকআপ লোকেশন" else "Pickup Location",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) calcPickup.nameBn else calcPickup.nameEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            DropdownMenu(
                                expanded = pickupDropdownExpanded,
                                onDismissRequest = { pickupDropdownExpanded = false }
                            ) {
                                allLocations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) loc.nameBn else loc.nameEn) },
                                        onClick = {
                                            calcPickup = loc
                                            pickupDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Drop Location Field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                .clickable { dropDropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ড্রপ লোকেশন" else "Drop Location",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) calcDrop.nameBn else calcDrop.nameEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            DropdownMenu(
                                expanded = dropDropdownExpanded,
                                onDismissRequest = { dropDropdownExpanded = false }
                            ) {
                                allLocations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) loc.nameBn else loc.nameEn) },
                                        onClick = {
                                            calcDrop = loc
                                            dropDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Vehicle Selection Chips for Calculator
                        Text(
                            text = if (language == AppLanguage.BANGLA) "যানবাহন নির্বাচন করুন" else "Select Vehicle",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(vehicles) { v ->
                                val isSelected = v.id == calcVehicle.id
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) TruckEmerald else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) TruckEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.clickable { calcVehicle = v }
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) v.nameBn else v.nameEn,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Calculation Result Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "দূরত্ব ও আনুমানিক সময়" else "Distance & Est. Time",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${estimatedDistanceKm} km • ~${estimatedMinutes} mins",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "আনুমানিক ভাড়া" else "Estimated Fare",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "৳ ${estimatedFare}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TruckEmeraldDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Book for this route button
                        Button(
                            onClick = {
                                onQuickBookRoute(calcPickup, calcDrop, calcVehicle)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("calculator_book_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TruckEmerald)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "এই রুটে এখনই বুক করুন" else "Book for this Route",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // POPULAR QUICK ROUTES (জনপ্রিয় রুটসমূহ - এক ট্যাপে বুকিং)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = TruckEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.t("quick_routes", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BANGLA) "দ্রুত বুকিং" else "Instant Pick",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TruckEmerald
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val quickRoutes = listOf(
                        Triple(allLocations[0], allLocations[2], 800), // Farmgate -> Dhanmondi
                        Triple(allLocations[0], allLocations[1], 950), // Farmgate -> Mirpur 10
                        Triple(allLocations[3], allLocations[5], 1500), // Uttara -> Motijheel
                        Triple(allLocations[4], allLocations[2], 1100), // Gulshan 2 -> Dhanmondi
                        Triple(allLocations[1], allLocations[3], 900)   // Mirpur 10 -> Uttara
                    )

                    items(quickRoutes) { (from, to, fare) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .clickable {
                                    calcPickup = from
                                    calcDrop = to
                                    onQuickBookRoute(from, to, calcVehicle)
                                }
                                .testTag("quick_route_${from.nameEn}_${to.nameEn}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) from.nameBn else from.nameEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = TruckEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) to.nameBn else to.nameEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "৳ $fare",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TruckEmerald
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "• ট্যাপ করে বুক করুন" else "• Tap to book",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // SPECIAL OFFERS & PROMOS BANNER (আকর্ষণীয় অফার ও ডিসকাউন্ট)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Discount,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.t("special_offers", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BANGLA) "৩টি অফার সক্রিয়" else "3 Active Offers",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Offers Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Promo 1: FIRST50
                    item {
                        PromoOfferCard(
                            badgeText = if (language == AppLanguage.BANGLA) "নতুন গ্রাহক" else "New User",
                            promoCode = "FIRST50",
                            title = if (language == AppLanguage.BANGLA) "৫০% পর্যন্ত ছাড় প্রথম ট্রিপে" else "Up to 50% Off First Trip",
                            subtitle = if (language == AppLanguage.BANGLA) "সর্বোচ্চ ৫০ টাকা ছাড় উপভোগ করুন" else "Enjoy instant discount up to ৳ 50",
                            badgeColor = Color(0xFF10B981),
                            onCopy = {
                                TruckMateRepository.applyPromoCode("FIRST50")
                                copiedPromoNotification = if (language == AppLanguage.BANGLA) "FIRST50 প্রোমো কোড প্রয়োগ করা হয়েছে!" else "Promo code FIRST50 applied!"
                            }
                        )
                    }

                    // Promo 2: SHIFTHOME
                    item {
                        PromoOfferCard(
                            badgeText = if (language == AppLanguage.BANGLA) "বাসা বদল" else "Home Move",
                            promoCode = "SHIFTHOME",
                            title = if (language == AppLanguage.BANGLA) "বাসা বদলে ফ্ল্যাট ৳ ৩০০ ছাড়" else "Flat ৳ 300 Off House Shifting",
                            subtitle = if (language == AppLanguage.BANGLA) "৭ ও ১২ ফুট ট্রাকে প্রযোজ্য" else "Valid on 7ft & 12ft trucks",
                            badgeColor = Color(0xFFF97316),
                            onCopy = {
                                TruckMateRepository.applyPromoCode("SHIFTHOME")
                                copiedPromoNotification = if (language == AppLanguage.BANGLA) "SHIFTHOME প্রোমো কোড প্রয়োগ করা হয়েছে!" else "Promo code SHIFTHOME applied!"
                            }
                        )
                    }

                    // Promo 3: CORPORATE
                    item {
                        PromoOfferCard(
                            badgeText = if (language == AppLanguage.BANGLA) "ব্যবসা ও কার্গো" else "Cargo Business",
                            promoCode = "CORPORATE",
                            title = if (language == AppLanguage.BANGLA) "কমার্শিয়াল কার্গোতে ৳ ২০০ ছাড়" else "৳ 200 Cashback on Business Cargo",
                            subtitle = if (language == AppLanguage.BANGLA) "ভারী মালামাল ও ফ্যাক্টরি চালান" else "Factory freight & wholesale move",
                            badgeColor = Color(0xFF3B82F6),
                            onCopy = {
                                TruckMateRepository.applyPromoCode("CORPORATE")
                                copiedPromoNotification = if (language == AppLanguage.BANGLA) "CORPORATE প্রোমো কোড প্রয়োগ করা হয়েছে!" else "Promo code CORPORATE applied!"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // LOGISTICS SOLUTIONS BY NEED (আপনার প্রয়োজন অনুযায়ী সেবা)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.t("logistics_solutions", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Solution 1: Home Shifting
                    SolutionUseCaseCard(
                        icon = Icons.Default.HomeWork,
                        title = AppStrings.t("home_shifting", language),
                        description = AppStrings.t("home_shifting_sub", language),
                        recommendedVehicle = if (language == AppLanguage.BANGLA) "সুপারিশ: ৭/১২ ফুট ট্রাক" else "Recommended: 7ft/12ft Truck",
                        accentColor = Color(0xFF10B981),
                        onClick = {
                            val v = vehicles.find { it.id == "v_pickup_7ft" } ?: vehicles[0]
                            onSelectSolution("Household Moving", v)
                        }
                    )

                    // Solution 2: Commercial Cargo
                    SolutionUseCaseCard(
                        icon = Icons.Default.Business,
                        title = AppStrings.t("commercial_cargo", language),
                        description = AppStrings.t("commercial_cargo_sub", language),
                        recommendedVehicle = if (language == AppLanguage.BANGLA) "সুপারিশ: ১৪ ফুট ভারী ট্রাক" else "Recommended: 14ft Truck",
                        accentColor = Color(0xFF0284C7),
                        onClick = {
                            val v = vehicles.find { it.id == "v_truck_14ft" } ?: vehicles.lastOrNull() ?: vehicles[0]
                            onSelectSolution("Commercial Freight", v)
                        }
                    )

                    // Solution 3: Construction Materials
                    SolutionUseCaseCard(
                        icon = Icons.Default.Architecture,
                        title = AppStrings.t("construction_materials", language),
                        description = AppStrings.t("construction_materials_sub", language),
                        recommendedVehicle = if (language == AppLanguage.BANGLA) "সুপারিশ: ওপেন ট্রাক" else "Recommended: Open Truck",
                        accentColor = Color(0xFFF59E0B),
                        onClick = {
                            val v = vehicles.find { it.id == "v_truck_12ft" } ?: vehicles[0]
                            onSelectSolution("Construction Cargo", v)
                        }
                    )

                    // Solution 4: Express Parcel
                    SolutionUseCaseCard(
                        icon = Icons.Default.Inventory2,
                        title = AppStrings.t("express_parcel", language),
                        description = AppStrings.t("express_parcel_sub", language),
                        recommendedVehicle = if (language == AppLanguage.BANGLA) "সুপারিশ: ৭ ফুট পিকআপ" else "Recommended: 7ft Pickup",
                        accentColor = Color(0xFF8B5CF6),
                        onClick = {
                            val v = vehicles.find { it.id == "v_pickup_7ft" } ?: vehicles[0]
                            onSelectSolution("Express Parcel", v)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // LIVE TEJGAON STAND & FLEET STATUS
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF064E3B),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34D399))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.t("fleet_status", language),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = AppStrings.t("trucks_ready", language),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE6FFFA)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = AppStrings.t("avg_arrival", language),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Button(
                                onClick = onOpenMap,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ম্যাপে দেখুন" else "View Map",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Google Maps Logistics Coverage Section
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "গুগল ম্যাপ ও লাইভ কভারেজ" else "Google Maps & Live Coverage",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পূর্ণ ম্যাপ >" else "Full Map >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TruckEmerald,
                        modifier = Modifier
                            .clickable { onOpenMap() }
                            .testTag("home_explore_map_link")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Embedded Google Map
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                ) {
                    TruckMateGoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        pickupLocation = calcPickup,
                        dropLocation = calcDrop,
                        driverProgress = 0.6f,
                        isLiveTracking = false,
                        interactive = true,
                        showControls = true,
                        showModeToggle = true,
                        onMapClick = {
                            onOpenMap()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // WHY CHOOSE TRUCKMATE? (কেন ট্রাকমেট সেরা?)
                Text(
                    text = AppStrings.t("why_truckmate", language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TrustBadgeCard(
                        icon = Icons.Default.VerifiedUser,
                        title = AppStrings.t("verified_drivers", language),
                        subtitle = AppStrings.t("verified_drivers_sub", language),
                        tint = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    TrustBadgeCard(
                        icon = Icons.Default.GpsFixed,
                        title = AppStrings.t("live_gps", language),
                        subtitle = AppStrings.t("live_gps_sub", language),
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TrustBadgeCard(
                        icon = Icons.Default.Handshake,
                        title = AppStrings.t("direct_bidding", language),
                        subtitle = AppStrings.t("direct_bidding_sub", language),
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                    TrustBadgeCard(
                        icon = Icons.Default.SupportAgent,
                        title = AppStrings.t("support_24_7", language),
                        subtitle = AppStrings.t("support_24_7_sub", language),
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 24/7 HELPLINE ACTION BANNER
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSupportDialog = true }
                        .testTag("home_bottom_support_bar"),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HeadsetMic,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সাহায্য প্রয়োজন? হেল্পলাইন সক্রিয়" else "Need Help? 24/7 Helpline Active",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "১৬২৪৭ নম্বরে কল করুন অথবা চ্যাট করুন" else "Call 16247 or chat on WhatsApp",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Notification Snackbar if promo copied
        copiedPromoNotification?.let { msg ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TruckEmeraldDark,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            LaunchedEffect(msg) {
                delay(2400)
                copiedPromoNotification = null
            }
        }

        // 24/7 Helpline & Customer Support Dialog
        if (showSupportDialog) {
            AlertDialog(
                onDismissRequest = { showSupportDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "২৪/৭ জরুরি হেল্পলাইন" else "24/7 Customer Support",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "আপনার বুকিং, মালামাল পরিবহন বা ড্রাইভার সংক্রান্ত যেকোনো সমস্যায় আমাদের সাথে যোগাযোগ করুন।"
                            else
                                "Reach out directly for booking assistance, driver queries, or logistics tracking.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Call 16247 Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    copiedPromoNotification = if (language == AppLanguage.BANGLA) "হেল্পলাইন ১৬২৪৭ এ কল করা হচ্ছে..." else "Calling Helpline 16247..."
                                    showSupportDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF10B981))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = AppStrings.t("call_helpline", language),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "সকাল ৭টা - রাত ১২টা (টোল ফ্রি)" else "7:00 AM - 12:00 AM (Toll Free)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // WhatsApp Support Button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    copiedPromoNotification = if (language == AppLanguage.BANGLA) "WhatsApp সাপোর্ট খোলা হচ্ছে..." else "Opening WhatsApp support..."
                                    showSupportDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = AppStrings.t("whatsapp_support", language),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "+880 1812 998877",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSupportDialog = false }) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "বন্ধ করুন" else "Close",
                            fontWeight = FontWeight.Bold,
                            color = TruckEmerald
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun PromoOfferCard(
    badgeText: String,
    promoCode: String,
    title: String,
    subtitle: String,
    badgeColor: Color,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = promoCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCopy,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "প্রয়োগ করুন ($promoCode)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SolutionUseCaseCard(
    icon: ImageVector,
    title: String,
    description: String,
    recommendedVehicle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recommendedVehicle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TrustBadgeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ServiceCategoryItem(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
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
                .size(60.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun PopularVehicleCard(
    vehicle: Vehicle,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("vehicle_card_${vehicle.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Vehicle Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = vehicle.imageRes),
                    contentDescription = vehicle.nameEn,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == AppLanguage.BANGLA) vehicle.nameBn else vehicle.nameEn,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (language == AppLanguage.BANGLA) vehicle.subtitleBn else vehicle.subtitleEn,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "৳ ${vehicle.baseFare}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TruckEmerald
                )
                Text(
                    text = "${vehicle.capacity}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
