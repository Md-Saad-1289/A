package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.backend.*
import com.example.data.local.TruckMateDatabase
import com.example.data.local.entity.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object TruckMateRepository {

    private const val TAG = "TruckMateRepository"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var database: TruckMateDatabase? = null

    // DB & Backend Status indicators
    private val _isDbInitialized = MutableStateFlow(false)
    val isDbInitialized: StateFlow<Boolean> = _isDbInitialized.asStateFlow()

    private val _backendStatus = MutableStateFlow("Online (REST API v1)")
    val backendStatus: StateFlow<String> = _backendStatus.asStateFlow()

    // Localization & Theme
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userPhone = MutableStateFlow("+880 1712 345678")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    val userName: String = "Tariq Rahman"

    // Vehicles Catalog
    val vehicles = listOf(
        Vehicle(
            id = "v_pickup_7ft",
            nameEn = "7ft Pickup",
            nameBn = "৭ ফুট পিকআপ",
            subtitleEn = "Small goods • Home move",
            subtitleBn = "ছোট পণ্য • বাসা বদল",
            capacity = "1.5 ton",
            volume = "1-2 m³",
            baseFare = 500,
            perKmRate = 35,
            category = VehicleCategory.PICKUP,
            imageRes = R.drawable.img_pickup_7ft
        ),
        Vehicle(
            id = "v_truck_12ft",
            nameEn = "12ft Truck",
            nameBn = "১২ ফুট ট্রাক",
            subtitleEn = "Large goods • Business",
            subtitleBn = "ভারী পণ্য • বাণিজ্যিক",
            capacity = "3 ton",
            volume = "6-8 m³",
            baseFare = 1000,
            perKmRate = 60,
            category = VehicleCategory.TRUCK,
            imageRes = R.drawable.img_truck_12ft
        ),
        Vehicle(
            id = "v_truck_14ft",
            nameEn = "14ft Truck",
            nameBn = "১৪ ফুট ট্রাক",
            subtitleEn = "Heavy cargo • Factory",
            subtitleBn = "ভারী কার্গো • ফ্যাক্টরি",
            capacity = "5 ton",
            volume = "10-14 m³",
            baseFare = 1600,
            perKmRate = 85,
            category = VehicleCategory.TRUCK,
            imageRes = R.drawable.img_truck_12ft
        ),
        Vehicle(
            id = "v_covered_van",
            nameEn = "Covered Van",
            nameBn = "কাভার্ড ভ্যান",
            subtitleEn = "Fragile goods • Weatherproof",
            subtitleBn = "সংবেদনশীল পণ্য • রোদ-বৃষ্টি প্রতিরোধী",
            capacity = "2 ton",
            volume = "4-6 m³",
            baseFare = 850,
            perKmRate = 50,
            category = VehicleCategory.COVERED,
            imageRes = R.drawable.img_pickup_7ft
        )
    )

    // Dhaka City Locations
    val locations = listOf(
        LocationPoint(
            id = "loc_farmgate",
            nameEn = "Farmgate",
            nameBn = "ফার্মগেট",
            areaEn = "Farmgate, Dhaka",
            areaBn = "ফার্মগেট, ঢাকা",
            lat = 23.7561,
            lng = 90.3872
        ),
        LocationPoint(
            id = "loc_mirpur10",
            nameEn = "Mirpur 10",
            nameBn = "মিরপুর ১০",
            areaEn = "Mirpur 10, Dhaka",
            areaBn = "মিরপুর ১০, ঢাকা",
            lat = 23.8071,
            lng = 90.3686
        ),
        LocationPoint(
            id = "loc_dhanmondi",
            nameEn = "Dhanmondi",
            nameBn = "ধানমন্ডি",
            areaEn = "Road 27, Dhanmondi, Dhaka",
            areaBn = "রোড ২৭, ধানমন্ডি, ঢাকা",
            lat = 23.7465,
            lng = 90.3760
        ),
        LocationPoint(
            id = "loc_uttara",
            nameEn = "Uttara",
            nameBn = "উত্তরা",
            areaEn = "Sector 7, Uttara, Dhaka",
            areaBn = "সেক্টর ৭, উত্তরা, ঢাকা",
            lat = 23.8759,
            lng = 90.3795
        ),
        LocationPoint(
            id = "loc_gulshan",
            nameEn = "Gulshan 2",
            nameBn = "গুলশান ২",
            areaEn = "Gulshan Circle 2, Dhaka",
            areaBn = "গুলশান সার্কেল ২, ঢাকা",
            lat = 23.7925,
            lng = 90.4078
        ),
        LocationPoint(
            id = "loc_motijheel",
            nameEn = "Motijheel",
            nameBn = "মতিঝিল",
            areaEn = "Motijheel C/A, Dhaka",
            areaBn = "মতিঝিল বা/এ, ঢাকা",
            lat = 23.7330,
            lng = 90.4172
        )
    )

    // Current Booking Draft
    private val _currentBooking = MutableStateFlow(
        BookingRequest(
            vehicle = vehicles[0],
            pickup = locations[0], // Farmgate
            drop = locations[1],   // Mirpur 10
            dateString = "25 Apr 2025",
            timeString = "10:00 AM",
            goodsCategory = "Household Items",
            serviceType = "Open Pickup",
            specialInstruction = "Handle with care",
            promoCode = "",
            discount = 0,
            distanceKm = 4.2
        )
    )
    val currentBooking: StateFlow<BookingRequest> = _currentBooking.asStateFlow()

    // Drivers
    val sampleDrivers = listOf(
        Driver(
            id = "drv_1",
            nameEn = "Rahim Hossain",
            nameBn = "রহিম হোসেন",
            phone = "+880 1812 998877",
            rating = 4.8f,
            tripsCount = 120,
            vehicleId = "v_pickup_7ft",
            vehiclePlate = "Dhaka Metro - 11-2345",
            vehicleNameEn = "7ft Pickup • Dhaka Metro",
            vehicleNameBn = "৭ ফুট পিকআপ • ঢাকা মেট্রো",
            etaMinutes = 12
        ),
        Driver(
            id = "drv_2",
            nameEn = "Karim Sheikh",
            nameBn = "করিম শেখ",
            phone = "+880 1711 445566",
            rating = 4.6f,
            tripsCount = 98,
            vehicleId = "v_pickup_7ft",
            vehiclePlate = "Dhaka Metro - 13-8941",
            vehicleNameEn = "7ft Pickup • Dhaka Metro",
            vehicleNameBn = "৭ ফুট পিকআপ • ঢাকা মেট্রো",
            etaMinutes = 16
        ),
        Driver(
            id = "drv_3",
            nameEn = "Sabbir Ahmed",
            nameBn = "সাব্বির আহমেদ",
            phone = "+880 1913 223344",
            rating = 4.5f,
            tripsCount = 75,
            vehicleId = "v_pickup_7ft",
            vehiclePlate = "Dhaka Metro - 15-5678",
            vehicleNameEn = "7ft Pickup • Dhaka Metro",
            vehicleNameBn = "৭ ফুট পিকআপ • ঢাকা মেট্রো",
            etaMinutes = 22
        ),
        Driver(
            id = "drv_4",
            nameEn = "Tanvir Islam",
            nameBn = "তানভীর ইসলাম",
            phone = "+880 1610 889900",
            rating = 4.9f,
            tripsCount = 210,
            vehicleId = "v_pickup_7ft",
            vehiclePlate = "Dhaka Metro - 18-9012",
            vehicleNameEn = "7ft Pickup • Dhaka Metro",
            vehicleNameBn = "৭ ফুট পিকআপ • ঢাকা মেট্রো",
            etaMinutes = 8
        )
    )

    // Bids List
    private val _driverBids = MutableStateFlow(
        listOf(
            DriverBid("bid_1", sampleDrivers[0], 620, 12),
            DriverBid("bid_2", sampleDrivers[1], 640, 16),
            DriverBid("bid_3", sampleDrivers[2], 660, 22),
            DriverBid("bid_4", sampleDrivers[3], 600, 8)
        )
    )
    val driverBids: StateFlow<List<DriverBid>> = _driverBids.asStateFlow()

    // Active Live Trip
    private val _activeTrip = MutableStateFlow<Trip?>(null)
    val activeTrip: StateFlow<Trip?> = _activeTrip.asStateFlow()

    // Trip History (Synchronized with Room DB)
    private val _tripHistory = MutableStateFlow<List<Trip>>(emptyList())
    val tripHistory: StateFlow<List<Trip>> = _tripHistory.asStateFlow()

    // Wallet Balance
    private val _walletBalance = MutableStateFlow(1250)
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Saved Addresses
    private val _savedAddresses = MutableStateFlow<List<SavedAddress>>(emptyList())
    val savedAddresses: StateFlow<List<SavedAddress>> = _savedAddresses.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    /**
     * Initialize Local Room Database and connect Reactive Flows & Backend Client
     */
    fun initialize(context: Context) {
        if (database != null) return
        val db = TruckMateDatabase.getDatabase(context)
        database = db

        scope.launch {
            try {
                // Seed initial data if DB is fresh
                seedDatabaseIfEmpty(db)

                _isDbInitialized.value = true

                // Collect Reactive Room Flows into StateFlows
                launch {
                    db.tripDao().getAllTrips().collect { entities ->
                        val domainTrips = entities.map { it.toTrip() }
                        _tripHistory.value = domainTrips
                    }
                }

                launch {
                    db.tripDao().getActiveTrip().collect { activeEntity ->
                        if (activeEntity != null) {
                            _activeTrip.value = activeEntity.toTrip()
                        }
                    }
                }

                launch {
                    db.savedAddressDao().getAllAddresses().collect { entities ->
                        _savedAddresses.value = entities.map { it.toSavedAddress() }
                    }
                }

                launch {
                    db.notificationDao().getAllNotifications().collect { entities ->
                        _notifications.value = entities.map { it.toNotificationItem() }
                    }
                }

                launch {
                    db.walletDao().getAllTransactions().collect { entities ->
                        _transactions.value = entities.map { it.toWalletTransaction() }
                    }
                }

                launch {
                    db.userDao().getUser().collect { user ->
                        if (user != null) {
                            _walletBalance.value = user.walletBalance
                            _userPhone.value = user.phone
                            _isLoggedIn.value = user.isLoggedIn
                            _isDarkMode.value = user.isDarkMode
                            _language.value = if (user.language == "BANGLA") AppLanguage.BANGLA else AppLanguage.ENGLISH
                        }
                    }
                }

                // Check and confirm Backend API health
                try {
                    val fleetResponse = TruckMateBackendClient.apiService.getFleetStatus()
                    if (fleetResponse.isSuccessful) {
                        _backendStatus.value = "Online • 52 Trucks Live"
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Backend sync ping: ${e.message}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Room DB and backend: ${e.message}", e)
            }
        }
    }

    private suspend fun seedDatabaseIfEmpty(db: TruckMateDatabase) {
        if (db.userDao().getUserCount() == 0) {
            db.userDao().insertOrUpdateUser(
                UserEntity(
                    id = "current_user",
                    name = "Tariq Rahman",
                    phone = "+880 1712 345678",
                    walletBalance = 1250,
                    isLoggedIn = true,
                    language = "ENGLISH",
                    isDarkMode = false
                )
            )
        }

        if (db.tripDao().getTripCount() == 0) {
            val initialTrips = listOf(
                TripEntity(
                    id = "TRIP-8921",
                    vehicleId = "v_pickup_7ft",
                    vehicleNameEn = "7ft Pickup",
                    vehicleNameBn = "৭ ফুট পিকআপ",
                    pickupId = "loc_farmgate",
                    pickupNameEn = "Farmgate",
                    pickupNameBn = "ফার্মগেট",
                    dropId = "loc_mirpur10",
                    dropNameEn = "Mirpur 10",
                    dropNameBn = "মিরপুর ১০",
                    dateString = "25 Apr 2025",
                    timeString = "10:00 AM",
                    goodsCategory = "Household Items",
                    serviceType = "Open Pickup",
                    distanceKm = 4.2,
                    driverId = "drv_1",
                    driverNameEn = "Rahim Hossain",
                    driverNameBn = "রহিম হোসেন",
                    driverPhone = "+880 1812 998877",
                    driverRating = 4.8f,
                    driverVehiclePlate = "Dhaka Metro - 11-2345",
                    status = TripStatus.COMPLETED.name,
                    paymentMethod = "Cash on Delivery",
                    fare = 650,
                    dateFormatted = "25 Apr 2025, 10:00 AM",
                    rating = 5.0f,
                    review = "Great driver! Arrived right on time."
                ),
                TripEntity(
                    id = "TRIP-8142",
                    vehicleId = "v_pickup_7ft",
                    vehicleNameEn = "7ft Pickup",
                    vehicleNameBn = "৭ ফুট পিকআপ",
                    pickupId = "loc_dhanmondi",
                    pickupNameEn = "Dhanmondi",
                    pickupNameBn = "ধানমন্ডি",
                    dropId = "loc_uttara",
                    dropNameEn = "Uttara",
                    dropNameBn = "উত্তরা",
                    dateString = "20 Apr 2025",
                    timeString = "03:15 PM",
                    goodsCategory = "Furniture",
                    serviceType = "House Moving",
                    distanceKm = 14.5,
                    driverId = "drv_2",
                    driverNameEn = "Karim Sheikh",
                    driverNameBn = "করিম শেখ",
                    driverPhone = "+880 1711 445566",
                    driverRating = 4.6f,
                    driverVehiclePlate = "Dhaka Metro - 13-8941",
                    status = TripStatus.COMPLETED.name,
                    paymentMethod = "bKash",
                    fare = 1050,
                    dateFormatted = "20 Apr 2025, 03:15 PM",
                    rating = 4.5f,
                    review = "Helpful in loading goods."
                ),
                TripEntity(
                    id = "TRIP-7629",
                    vehicleId = "v_truck_12ft",
                    vehicleNameEn = "12ft Truck",
                    vehicleNameBn = "১২ ফুট ট্রাক",
                    pickupId = "loc_gulshan",
                    pickupNameEn = "Gulshan 2",
                    pickupNameBn = "গুলশান ২",
                    dropId = "loc_motijheel",
                    dropNameEn = "Motijheel",
                    dropNameBn = "মতিঝিল",
                    dateString = "12 Apr 2025",
                    timeString = "11:30 AM",
                    goodsCategory = "Commercial Goods",
                    serviceType = "Open Pickup",
                    distanceKm = 8.0,
                    driverId = "drv_3",
                    driverNameEn = "Sabbir Ahmed",
                    driverNameBn = "সাব্বির আহমেদ",
                    driverPhone = "+880 1913 223344",
                    driverRating = 4.5f,
                    driverVehiclePlate = "Dhaka Metro - 15-5678",
                    status = TripStatus.CANCELLED.name,
                    paymentMethod = "Cash on Delivery",
                    fare = 1450,
                    dateFormatted = "12 Apr 2025, 11:30 AM"
                )
            )
            db.tripDao().insertTrips(initialTrips)
        }

        if (db.savedAddressDao().getAddressCount() == 0) {
            val initialAddresses = listOf(
                SavedAddressEntity(
                    id = "addr_1",
                    type = AddressType.HOME.name,
                    titleEn = "Home",
                    titleBn = "বাসা",
                    addressEn = "Rahim Hasan 12, Road 5, Dhanmondi",
                    addressBn = "রহিম হাসান ১২, রোড ৫, ধানমন্ডি"
                ),
                SavedAddressEntity(
                    id = "addr_2",
                    type = AddressType.OFFICE.name,
                    titleEn = "Office",
                    titleBn = "অফিস",
                    addressEn = "House 45, Road 11, Banani, Dhaka",
                    addressBn = "বাড়ি ৪৫, রোড ১১, বনানী, ঢাকা"
                ),
                SavedAddressEntity(
                    id = "addr_3",
                    type = AddressType.WAREHOUSE.name,
                    titleEn = "Warehouse",
                    titleBn = "গুদাম",
                    addressEn = "Plot 8, Tejgaon Industrial Area, Dhaka",
                    addressBn = "প্লট ৮, তেজগাঁও শিল্প এলাকা, ঢাকা"
                )
            )
            db.savedAddressDao().insertAddresses(initialAddresses)
        }

        if (db.notificationDao().getNotificationCount() == 0) {
            val initialNotifs = listOf(
                NotificationEntity(
                    id = "notif_1",
                    titleEn = "Driver Assigned",
                    titleBn = "ড্রাইভার নির্ধারিত",
                    messageEn = "Rahim Hossain is on the way for your trip Farmgate to Mirpur 10.",
                    messageBn = "রহিম হোসেন আপনার ট্রিপের জন্য পথে আছেন।",
                    timeAgo = "10m ago",
                    isRead = false
                ),
                NotificationEntity(
                    id = "notif_2",
                    titleEn = "Special Discount ৳ 100",
                    titleBn = "বিশেষ ছাড় ১০০ টাকা",
                    messageEn = "Use promo code FIRST50 or SAVE100 to get instant discount on your next ride.",
                    messageBn = "আপনার পরবর্তী রাইডে তাৎক্ষণিক ছাড়ের জন্য প্রমো কোড ব্যবহার করুন।",
                    timeAgo = "2h ago",
                    isRead = false
                ),
                NotificationEntity(
                    id = "notif_3",
                    titleEn = "Trip Completed Successfully",
                    titleBn = "ট্রিপ সম্পন্ন হয়েছে",
                    messageEn = "Your trip with Karim Sheikh was completed. Thank you for using TruckMate!",
                    messageBn = "করিম শেখের সাথে আপনার ট্রিপ সফল হয়েছে। ধন্যবাদ!",
                    timeAgo = "2d ago",
                    isRead = true
                )
            )
            db.notificationDao().insertNotifications(initialNotifs)
        }

        if (db.walletDao().getTransactionCount() == 0) {
            val initialTransactions = listOf(
                WalletTransactionEntity("tx_1", "Added via bKash", "বিকাশ থেকে যোগ", "24 Apr 2025", 1000, true),
                WalletTransactionEntity("tx_2", "Trip Payment - TRIP-8142", "ট্রিপ পেমেন্ট", "20 Apr 2025", 1050, false),
                WalletTransactionEntity("tx_3", "Promo Cashback", "প্রমো ক্যাশব্যাক", "15 Apr 2025", 150, true),
                WalletTransactionEntity("tx_4", "Added via Nagad", "নগদ থেকে যোগ", "10 Apr 2025", 500, true)
            )
            db.walletDao().insertTransactions(initialTransactions)
        }
    }

    // Actions
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        scope.launch {
            database?.userDao()?.updateLanguage(lang.name)
        }
    }

    fun toggleDarkMode() {
        val newVal = !_isDarkMode.value
        _isDarkMode.value = newVal
        scope.launch {
            database?.userDao()?.updateDarkMode(newVal)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        scope.launch {
            database?.userDao()?.updateDarkMode(enabled)
        }
    }

    fun updatePhone(phone: String) {
        _userPhone.value = phone
        scope.launch {
            database?.userDao()?.updatePhone(phone)
        }
    }

    fun login() {
        _isLoggedIn.value = true
        scope.launch {
            database?.userDao()?.updateLoginState(true)
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        scope.launch {
            database?.userDao()?.updateLoginState(false)
        }
    }

    fun selectVehicle(vehicle: Vehicle) {
        _currentBooking.update { it.copy(vehicle = vehicle) }
    }

    fun setPickup(location: LocationPoint) {
        _currentBooking.update { it.copy(pickup = location) }
    }

    fun setDrop(location: LocationPoint) {
        _currentBooking.update { it.copy(drop = location) }
    }

    fun swapLocations() {
        _currentBooking.update {
            val oldPickup = it.pickup
            val oldDrop = it.drop
            it.copy(pickup = oldDrop, drop = oldPickup)
        }
    }

    fun updateBookingDetails(
        dateTime: String? = null,
        goodsCategory: String? = null,
        serviceType: String? = null,
        specialInstruction: String? = null
    ) {
        _currentBooking.update {
            it.copy(
                dateString = dateTime ?: it.dateString,
                goodsCategory = goodsCategory ?: it.goodsCategory,
                serviceType = serviceType ?: it.serviceType,
                specialInstruction = specialInstruction ?: it.specialInstruction
            )
        }
    }

    fun applyPromoCode(code: String): Boolean {
        return if (code.equals("FIRST50", ignoreCase = true) || code.equals("MATE50", ignoreCase = true)) {
            _currentBooking.update { it.copy(promoCode = code.uppercase(), discount = 50) }
            true
        } else if (code.equals("SAVE100", ignoreCase = true)) {
            _currentBooking.update { it.copy(promoCode = code.uppercase(), discount = 100) }
            true
        } else if (code.equals("SHIFTHOME", ignoreCase = true)) {
            _currentBooking.update { it.copy(promoCode = code.uppercase(), discount = 300) }
            true
        } else if (code.equals("CORPORATE", ignoreCase = true)) {
            _currentBooking.update { it.copy(promoCode = code.uppercase(), discount = 200) }
            true
        } else {
            false
        }
    }

    fun startBookingWithDriver(driver: Driver, customFare: Int? = null) {
        val booking = _currentBooking.value
        val finalFare = customFare ?: booking.totalFare
        val tripId = "TRIP-${(1000..9999).random()}"
        val newTrip = Trip(
            id = tripId,
            booking = booking,
            driver = driver,
            status = TripStatus.ASSIGNED,
            paymentMethod = "Cash on Delivery",
            fare = finalFare,
            dateFormatted = "${booking.dateString}, ${booking.timeString}"
        )
        _activeTrip.value = newTrip

        // Sync with Backend and persist in Room DB
        scope.launch {
            try {
                // 1. Call Backend API
                TruckMateBackendClient.apiService.createBooking(
                    CreateBookingRequestDto(
                        vehicleId = booking.vehicle.id,
                        pickupId = booking.pickup.id,
                        dropId = booking.drop.id,
                        pickupName = booking.pickup.nameEn,
                        dropName = booking.drop.nameEn,
                        dateString = booking.dateString,
                        timeString = booking.timeString,
                        goodsCategory = booking.goodsCategory,
                        serviceType = booking.serviceType,
                        specialInstruction = booking.specialInstruction,
                        promoCode = booking.promoCode
                    )
                )

                // 2. Persist to Room Database
                database?.tripDao()?.insertTrip(TripEntity.fromTrip(newTrip))
            } catch (e: Exception) {
                Log.e(TAG, "Backend booking sync error: ${e.message}")
            }
        }
    }

    fun startFastBooking() {
        val driver = sampleDrivers[0]
        startBookingWithDriver(driver)
    }

    fun advanceTripStatus() {
        val current = _activeTrip.value ?: return
        val nextStatus = when (current.status) {
            TripStatus.SEARCHING -> TripStatus.ASSIGNED
            TripStatus.ASSIGNED -> TripStatus.ARRIVING
            TripStatus.ARRIVING -> TripStatus.IN_TRANSIT
            TripStatus.IN_TRANSIT -> TripStatus.COMPLETED
            TripStatus.COMPLETED -> TripStatus.COMPLETED
            TripStatus.CANCELLED -> TripStatus.CANCELLED
        }
        val updated = current.copy(status = nextStatus)
        _activeTrip.value = updated

        // Backend Sync & Room Database Update
        scope.launch {
            try {
                TruckMateBackendClient.apiService.advanceTripStatus(current.id)
                database?.tripDao()?.updateTripStatus(current.id, nextStatus.name)
            } catch (e: Exception) {
                Log.e(TAG, "Backend advance error: ${e.message}")
            }
        }
    }

    fun cancelActiveTrip() {
        val current = _activeTrip.value ?: return
        val cancelled = current.copy(status = TripStatus.CANCELLED)
        _activeTrip.value = null

        scope.launch {
            try {
                TruckMateBackendClient.apiService.cancelTrip(current.id)
                database?.tripDao()?.updateTripStatus(current.id, TripStatus.CANCELLED.name)
            } catch (e: Exception) {
                Log.e(TAG, "Backend cancel error: ${e.message}")
            }
        }
    }

    fun rateTrip(rating: Float, review: String) {
        val current = _activeTrip.value ?: return
        _activeTrip.value = null

        scope.launch {
            try {
                TruckMateBackendClient.apiService.rateTrip(current.id, RateTripRequestDto(rating, review))
                database?.tripDao()?.rateTrip(current.id, rating, review)
            } catch (e: Exception) {
                Log.e(TAG, "Backend rate error: ${e.message}")
            }
        }
    }

    fun addMoneyToWallet(amount: Int) {
        val newTx = WalletTransaction(
            id = "tx_${System.currentTimeMillis()}",
            titleEn = "Added via bKash",
            titleBn = "বিকাশ থেকে যোগ",
            date = "Today",
            amount = amount,
            isCredit = true
        )

        scope.launch {
            try {
                TruckMateBackendClient.apiService.topUpWallet(TopUpRequestDto(amount, "bKash"))
                database?.walletDao()?.insertTransaction(WalletTransactionEntity.fromWalletTransaction(newTx))
                database?.userDao()?.addWalletBalance(amount)
            } catch (e: Exception) {
                Log.e(TAG, "Backend wallet topup error: ${e.message}")
            }
        }
    }

    fun addSavedAddress(type: AddressType, title: String, address: String) {
        val newAddress = SavedAddress(
            id = "addr_${System.currentTimeMillis()}",
            type = type,
            titleEn = title,
            titleBn = title,
            addressEn = address,
            addressBn = address
        )

        scope.launch {
            try {
                database?.savedAddressDao()?.insertAddress(SavedAddressEntity.fromSavedAddress(newAddress))
            } catch (e: Exception) {
                Log.e(TAG, "DB insert address error: ${e.message}")
            }
        }
    }

    fun markAllNotificationsRead() {
        scope.launch {
            try {
                database?.notificationDao()?.markAllAsRead()
            } catch (e: Exception) {
                Log.e(TAG, "DB mark notifications error: ${e.message}")
            }
        }
    }

    fun rebookTrip(trip: Trip) {
        _currentBooking.value = trip.booking
    }
}
