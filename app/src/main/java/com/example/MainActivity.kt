package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TruckMateRepository
import com.example.model.AppLanguage
import com.example.model.Trip
import com.example.model.TripStatus
import com.example.model.VehicleCategory
import com.example.ui.components.TruckMateBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.TruckEmerald
import com.example.ui.theme.TruckEmeraldDark
import com.example.ui.theme.TruckMateTheme
import com.example.ui.theme.TruckMint
import com.example.util.AppStrings
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TruckMateRepository.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            TruckMateApp()
        }
    }
}

@Composable
fun TruckMateApp() {
    val language by TruckMateRepository.language.collectAsStateWithLifecycle()
    val isDarkMode by TruckMateRepository.isDarkMode.collectAsStateWithLifecycle()
    val isLoggedIn by TruckMateRepository.isLoggedIn.collectAsStateWithLifecycle()
    val userPhone by TruckMateRepository.userPhone.collectAsStateWithLifecycle()
    val isDbInitialized by TruckMateRepository.isDbInitialized.collectAsStateWithLifecycle()
    val backendStatus by TruckMateRepository.backendStatus.collectAsStateWithLifecycle()

    val currentBooking by TruckMateRepository.currentBooking.collectAsStateWithLifecycle()
    val activeTrip by TruckMateRepository.activeTrip.collectAsStateWithLifecycle()
    val driverBids by TruckMateRepository.driverBids.collectAsStateWithLifecycle()
    val tripHistory by TruckMateRepository.tripHistory.collectAsStateWithLifecycle()
    val walletBalance by TruckMateRepository.walletBalance.collectAsStateWithLifecycle()
    val transactions by TruckMateRepository.transactions.collectAsStateWithLifecycle()
    val savedAddresses by TruckMateRepository.savedAddresses.collectAsStateWithLifecycle()
    val notifications by TruckMateRepository.notifications.collectAsStateWithLifecycle()

    val unreadNotificationsCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    var currentRoute by remember { mutableStateOf("splash") }
    var backStack by remember { mutableStateOf(listOf<String>()) }

    fun navigateTo(route: String) {
        if (currentRoute != route) {
            backStack = backStack + currentRoute
            currentRoute = route
        }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            val prev = backStack.last()
            backStack = backStack.dropLast(1)
            currentRoute = prev
        } else if (currentRoute != "home") {
            currentRoute = "home"
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle Android system back press
    BackHandler(enabled = currentRoute != "home" && currentRoute != "splash") {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            navigateBack()
        }
    }

    val showBottomNav = currentRoute in listOf("home", "history", "wallet", "profile")

    TruckMateTheme(darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = showBottomNav,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        // User Profile Box in Drawer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(TruckEmerald),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = TruckMateRepository.userName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = userPhone,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        // Navigation Items
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text(AppStrings.t("home", language)) },
                            selected = currentRoute == "home",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                currentRoute = "home"
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                            label = { Text(AppStrings.t("trip_history", language)) },
                            selected = currentRoute == "history",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                currentRoute = "history"
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                            label = { Text(AppStrings.t("wallet", language)) },
                            selected = currentRoute == "wallet",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                currentRoute = "wallet"
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Map, contentDescription = null, tint = TruckEmerald) },
                            label = { Text(if (language == AppLanguage.BANGLA) "গুগল ম্যাপ ও ঢাকা ট্র্যাকিং" else "Google Maps & Coverage") },
                            selected = currentRoute == "map_explorer",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                navigateTo("map_explorer")
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                            label = { Text(AppStrings.t("saved_addresses", language)) },
                            selected = currentRoute == "saved_addresses",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                navigateTo("saved_addresses")
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                            label = { Text(AppStrings.t("notifications", language)) },
                            selected = currentRoute == "notifications",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                navigateTo("notifications")
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.Language, contentDescription = null) },
                            label = { Text("${AppStrings.t("language", language)} (${if (language == AppLanguage.BANGLA) "বাংলা" else "English"})") },
                            selected = currentRoute == "language",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                navigateTo("language")
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Backend & Database Status Badge
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isDbInitialized) TruckEmerald else Color(0xFFF59E0B))
                                    )
                                    Text(
                                        text = if (isDbInitialized) "Room DB: Connected (SQLite)" else "Room DB: Initializing...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(TruckEmerald)
                                    )
                                    Text(
                                        text = "REST API: $backendStatus",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Drawer footer: Dark mode switch & logout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { TruckMateRepository.toggleDarkMode() }
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isDarkMode) AppStrings.t("dark_mode", language) else AppStrings.t("light_mode", language),
                                    fontSize = 14.sp
                                )
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { TruckMateRepository.toggleDarkMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = TruckEmerald)
                            )
                        }

                        TextButton(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                TruckMateRepository.logout()
                                currentRoute = "login"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppStrings.t("logout", language), color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) {
            Scaffold(
                bottomBar = {
                    if (showBottomNav) {
                        TruckMateBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { destination ->
                                currentRoute = destination
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp)
                ) {
                    when (currentRoute) {
                        "splash" -> {
                            SplashScreen(
                                language = language,
                                onGetStarted = {
                                    currentRoute = if (isLoggedIn) "home" else "login"
                                }
                            )
                        }

                        "login" -> {
                            LoginScreen(
                                language = language,
                                onLoginSuccess = {
                                    TruckMateRepository.login()
                                    currentRoute = "home"
                                },
                                onBack = {
                                    if (isLoggedIn) navigateBack()
                                }
                            )
                        }

                        "home" -> {
                            HomeScreen(
                                language = language,
                                vehicles = TruckMateRepository.vehicles,
                                activeTrip = activeTrip,
                                unreadNotificationsCount = unreadNotificationsCount,
                                onMenuClick = {
                                    coroutineScope.launch { drawerState.open() }
                                },
                                onNotificationsClick = {
                                    navigateTo("notifications")
                                },
                                onOpenMap = {
                                    navigateTo("map_explorer")
                                },
                                onSelectVehicleCategory = { category ->
                                    if (category == "bidding") {
                                        navigateTo("bidding")
                                    } else if (category == "fast") {
                                        TruckMateRepository.startFastBooking()
                                        navigateTo("live_tracking")
                                    } else {
                                        navigateTo("vehicle_select")
                                    }
                                },
                                onVehicleClick = { vehicle ->
                                    TruckMateRepository.selectVehicle(vehicle)
                                    navigateTo("location_select")
                                },
                                onTrackActiveTrip = {
                                    if (activeTrip != null) {
                                        if (activeTrip?.status == TripStatus.COMPLETED) {
                                            navigateTo("trip_completed")
                                        } else {
                                            navigateTo("live_tracking")
                                        }
                                    }
                                },
                                onQuickBookRoute = { pickup, drop, vehicle ->
                                    TruckMateRepository.setPickup(pickup)
                                    TruckMateRepository.setDrop(drop)
                                    if (vehicle != null) {
                                        TruckMateRepository.selectVehicle(vehicle)
                                    }
                                    navigateTo("booking_details")
                                },
                                onSelectSolution = { goodsCategory, vehicle ->
                                    TruckMateRepository.selectVehicle(vehicle)
                                    TruckMateRepository.updateBookingDetails(goodsCategory = goodsCategory)
                                    navigateTo("location_select")
                                }
                            )
                        }

                        "vehicle_select" -> {
                            VehicleSelectionScreen(
                                language = language,
                                vehicles = TruckMateRepository.vehicles,
                                selectedVehicle = currentBooking.vehicle,
                                onVehicleSelect = { v ->
                                    TruckMateRepository.selectVehicle(v)
                                },
                                onNextClick = {
                                    navigateTo("location_select")
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "location_select" -> {
                            LocationSelectionScreen(
                                language = language,
                                locations = TruckMateRepository.locations,
                                pickupLocation = currentBooking.pickup,
                                dropLocation = currentBooking.drop,
                                onPickupSelect = { loc ->
                                    TruckMateRepository.setPickup(loc)
                                },
                                onDropSelect = { loc ->
                                    TruckMateRepository.setDrop(loc)
                                },
                                onSwapLocations = {
                                    TruckMateRepository.swapLocations()
                                },
                                onNextClick = {
                                    navigateTo("booking_details")
                                },
                                onOpenFullMap = {
                                    navigateTo("map_explorer")
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "booking_details" -> {
                            BookingDetailsScreen(
                                language = language,
                                booking = currentBooking,
                                onChangeVehicle = {
                                    navigateTo("vehicle_select")
                                },
                                onChangeLocations = {
                                    navigateTo("location_select")
                                },
                                onUpdateDetails = { dateTime, goods, service, instruction ->
                                    TruckMateRepository.updateBookingDetails(dateTime, goods, service, instruction)
                                },
                                onGetFareEstimate = {
                                    navigateTo("fare_estimate")
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "fare_estimate" -> {
                            FareEstimateScreen(
                                language = language,
                                booking = currentBooking,
                                onBookNow = {
                                    // Assign first driver directly
                                    TruckMateRepository.startBookingWithDriver(TruckMateRepository.sampleDrivers[0])
                                    navigateTo("live_tracking")
                                },
                                onTryBidding = {
                                    navigateTo("bidding")
                                },
                                onApplyPromo = { code ->
                                    TruckMateRepository.applyPromoCode(code)
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "bidding" -> {
                            BiddingScreen(
                                language = language,
                                bids = driverBids,
                                onSelectBid = { driver, fare ->
                                    TruckMateRepository.startBookingWithDriver(driver, fare)
                                    navigateTo("live_tracking")
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "live_tracking" -> {
                            val trip = activeTrip ?: Trip(
                                id = "TRIP-8921",
                                booking = currentBooking,
                                driver = TruckMateRepository.sampleDrivers[0],
                                status = TripStatus.ASSIGNED,
                                paymentMethod = "Cash on Delivery",
                                fare = currentBooking.totalFare,
                                dateFormatted = "${currentBooking.dateString}, ${currentBooking.timeString}"
                            )

                            LiveTrackingScreen(
                                language = language,
                                trip = trip,
                                onAdvanceStatus = {
                                    TruckMateRepository.advanceTripStatus()
                                    if (TruckMateRepository.activeTrip.value?.status == TripStatus.COMPLETED) {
                                        navigateTo("trip_completed")
                                    }
                                },
                                onCancelTrip = {
                                    TruckMateRepository.cancelActiveTrip()
                                    currentRoute = "home"
                                },
                                onBack = {
                                    currentRoute = "home"
                                }
                            )
                        }

                        "trip_completed" -> {
                            val trip = activeTrip ?: tripHistory.firstOrNull() ?: Trip(
                                id = "TRIP-8921",
                                booking = currentBooking,
                                driver = TruckMateRepository.sampleDrivers[0],
                                status = TripStatus.COMPLETED,
                                paymentMethod = "Cash on Delivery",
                                fare = currentBooking.totalFare,
                                dateFormatted = "${currentBooking.dateString}, ${currentBooking.timeString}"
                            )

                            TripCompletedScreen(
                                language = language,
                                trip = trip,
                                onRateDriver = { rating, review ->
                                    TruckMateRepository.rateTrip(rating, review)
                                    currentRoute = "home"
                                },
                                onHomeClick = {
                                    currentRoute = "home"
                                }
                            )
                        }

                        "history" -> {
                            TripHistoryScreen(
                                language = language,
                                trips = tripHistory,
                                onRebookTrip = { trip ->
                                    TruckMateRepository.rebookTrip(trip)
                                    navigateTo("booking_details")
                                },
                                onBack = {
                                    currentRoute = "home"
                                }
                            )
                        }

                        "wallet" -> {
                            WalletScreen(
                                language = language,
                                balance = walletBalance,
                                transactions = transactions,
                                onAddMoney = { amount ->
                                    TruckMateRepository.addMoneyToWallet(amount)
                                },
                                onBack = {
                                    currentRoute = "home"
                                }
                            )
                        }

                        "profile" -> {
                            ProfileScreen(
                                language = language,
                                userName = TruckMateRepository.userName,
                                userPhone = userPhone,
                                isDarkMode = isDarkMode,
                                isDbConnected = isDbInitialized,
                                backendStatus = backendStatus,
                                onToggleDarkMode = {
                                    TruckMateRepository.toggleDarkMode()
                                },
                                onSavedAddressesClick = {
                                    navigateTo("saved_addresses")
                                },
                                onNotificationsClick = {
                                    navigateTo("notifications")
                                },
                                onLanguageClick = {
                                    navigateTo("language")
                                },
                                onHelpSupportClick = {
                                    // Help & Support dialog/sheet or redirect to FAQ
                                    navigateTo("notifications")
                                },
                                onLogoutClick = {
                                    TruckMateRepository.logout()
                                    currentRoute = "login"
                                }
                            )
                        }

                        "saved_addresses" -> {
                            SavedAddressesScreen(
                                language = language,
                                addresses = savedAddresses,
                                onAddAddress = { type, title, address ->
                                    TruckMateRepository.addSavedAddress(type, title, address)
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "notifications" -> {
                            NotificationsScreen(
                                language = language,
                                notifications = notifications,
                                onNotificationClick = {
                                    TruckMateRepository.markAllNotificationsRead()
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "language" -> {
                            LanguageScreen(
                                currentLanguage = language,
                                onLanguageSelected = { selected ->
                                    TruckMateRepository.setLanguage(selected)
                                    navigateBack()
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        "map_explorer" -> {
                            GoogleMapExplorerScreen(
                                language = language,
                                locations = TruckMateRepository.locations,
                                currentPickup = currentBooking.pickup,
                                currentDrop = currentBooking.drop,
                                onSetPickup = { loc ->
                                    TruckMateRepository.setPickup(loc)
                                },
                                onSetDrop = { loc ->
                                    TruckMateRepository.setDrop(loc)
                                },
                                onProceedToBooking = {
                                    navigateTo("vehicle_select")
                                },
                                onBack = { navigateBack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
