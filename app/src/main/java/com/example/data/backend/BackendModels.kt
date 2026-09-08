package com.example.data.backend

data class VehicleDto(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val baseFare: Int,
    val perKmRate: Int,
    val capacity: String,
    val category: String
)

data class VehiclesResponseDto(
    val status: String,
    val count: Int,
    val vehicles: List<VehicleDto>
)

data class FareEstimateRequestDto(
    val pickupLat: Double,
    val pickupLng: Double,
    val dropLat: Double,
    val dropLng: Double,
    val vehicleId: String,
    val promoCode: String? = null
)

data class FareEstimateResponseDto(
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val baseFare: Int,
    val distanceFare: Int,
    val discount: Int,
    val totalFare: Int,
    val currency: String = "BDT"
)

data class CreateBookingRequestDto(
    val vehicleId: String,
    val pickupId: String,
    val dropId: String,
    val pickupName: String,
    val dropName: String,
    val dateString: String,
    val timeString: String,
    val goodsCategory: String,
    val serviceType: String,
    val specialInstruction: String,
    val promoCode: String? = null
)

data class CreateBookingResponseDto(
    val bookingId: String,
    val status: String,
    val message: String,
    val createdAt: Long
)

data class DriverBidDto(
    val bidId: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val rating: Float,
    val tripsCount: Int,
    val vehiclePlate: String,
    val bidAmount: Int,
    val etaMinutes: Int
)

data class DriverBidsResponseDto(
    val bookingId: String,
    val bidsCount: Int,
    val bids: List<DriverBidDto>
)

data class AcceptBidRequestDto(
    val bidId: String,
    val driverId: String,
    val agreedFare: Int
)

data class TripStatusResponseDto(
    val tripId: String,
    val currentStatus: String,
    val driverId: String?,
    val driverLat: Double?,
    val driverLng: Double?,
    val message: String
)

data class RateTripRequestDto(
    val rating: Float,
    val review: String
)

data class TopUpRequestDto(
    val amount: Int,
    val gateway: String // "bKash", "Nagad", etc.
)

data class TopUpResponseDto(
    val transactionId: String,
    val amount: Int,
    val newBalance: Int,
    val status: String
)

data class FleetStatusResponseDto(
    val activeStand: String,
    val activeDriversCount: Int,
    val availableTrucks: Int,
    val avgEtaMinutes: Int,
    val timestamp: Long
)
