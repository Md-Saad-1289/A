package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class AppLanguage {
    BANGLA,
    ENGLISH
}

enum class VehicleCategory {
    ALL,
    PICKUP,
    TRUCK,
    COVERED
}

data class Vehicle(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val capacity: String,
    val volume: String,
    val baseFare: Int,
    val perKmRate: Int,
    val category: VehicleCategory,
    @DrawableRes val imageRes: Int = R.drawable.img_pickup_7ft
)

data class LocationPoint(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val areaEn: String,
    val areaBn: String,
    val lat: Double,
    val lng: Double
)

data class Driver(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val phone: String,
    val rating: Float,
    val tripsCount: Int,
    val vehicleId: String,
    val vehiclePlate: String,
    val vehicleNameEn: String,
    val vehicleNameBn: String,
    val etaMinutes: Int
)

data class DriverBid(
    val id: String,
    val driver: Driver,
    val bidAmount: Int,
    val etaMinutes: Int,
    val isSelected: Boolean = false
)

enum class TripStatus {
    SEARCHING,
    ASSIGNED,
    ARRIVING,
    IN_TRANSIT,
    COMPLETED,
    CANCELLED
}

data class BookingRequest(
    val vehicle: Vehicle,
    val pickup: LocationPoint,
    val drop: LocationPoint,
    val dateString: String = "25 Apr 2025",
    val timeString: String = "10:00 AM",
    val goodsCategory: String = "Household Items",
    val serviceType: String = "Open Pickup", // "Open Pickup" or "House Moving"
    val specialInstruction: String = "",
    val promoCode: String = "",
    val discount: Int = 0,
    val distanceKm: Double = 4.2
) {
    val distanceFare: Int
        get() = (distanceKm * vehicle.perKmRate).toInt()

    val totalFare: Int
        get() = (vehicle.baseFare + distanceFare - discount).coerceAtLeast(100)
}

data class Trip(
    val id: String,
    val booking: BookingRequest,
    val driver: Driver,
    val status: TripStatus,
    val paymentMethod: String = "Cash on Delivery",
    val fare: Int = 650,
    val dateFormatted: String = "25 Apr 2025, 10:00 AM",
    val rating: Float? = null,
    val review: String? = null
)

enum class AddressType {
    HOME,
    OFFICE,
    WAREHOUSE,
    OTHER
}

data class SavedAddress(
    val id: String,
    val type: AddressType,
    val titleEn: String,
    val titleBn: String,
    val addressEn: String,
    val addressBn: String
)

data class NotificationItem(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val messageEn: String,
    val messageBn: String,
    val timeAgo: String,
    val isRead: Boolean = false
)

data class WalletTransaction(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val date: String,
    val amount: Int,
    val isCredit: Boolean
)
