package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.TruckMateRepository
import com.example.model.*

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val vehicleNameEn: String,
    val vehicleNameBn: String,
    val pickupId: String,
    val pickupNameEn: String,
    val pickupNameBn: String,
    val dropId: String,
    val dropNameEn: String,
    val dropNameBn: String,
    val dateString: String,
    val timeString: String,
    val goodsCategory: String,
    val serviceType: String,
    val distanceKm: Double,
    val driverId: String,
    val driverNameEn: String,
    val driverNameBn: String,
    val driverPhone: String,
    val driverRating: Float,
    val driverVehiclePlate: String,
    val status: String,
    val paymentMethod: String,
    val fare: Int,
    val dateFormatted: String,
    val rating: Float? = null,
    val review: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toTrip(): Trip {
        val matchedVehicle = TruckMateRepository.vehicles.find { it.id == vehicleId }
            ?: TruckMateRepository.vehicles[0]
        val pickupLocation = LocationPoint(
            id = pickupId,
            nameEn = pickupNameEn,
            nameBn = pickupNameBn,
            areaEn = "$pickupNameEn, Dhaka",
            areaBn = "$pickupNameBn, ঢাকা",
            lat = 23.7561,
            lng = 90.3872
        )
        val dropLocation = LocationPoint(
            id = dropId,
            nameEn = dropNameEn,
            nameBn = dropNameBn,
            areaEn = "$dropNameEn, Dhaka",
            areaBn = "$dropNameBn, ঢাকা",
            lat = 23.8071,
            lng = 90.3686
        )
        val booking = BookingRequest(
            vehicle = matchedVehicle,
            pickup = pickupLocation,
            drop = dropLocation,
            dateString = dateString,
            timeString = timeString,
            goodsCategory = goodsCategory,
            serviceType = serviceType,
            distanceKm = distanceKm
        )
        val driver = Driver(
            id = driverId,
            nameEn = driverNameEn,
            nameBn = driverNameBn,
            phone = driverPhone,
            rating = driverRating,
            tripsCount = 100,
            vehicleId = vehicleId,
            vehiclePlate = driverVehiclePlate,
            vehicleNameEn = vehicleNameEn,
            vehicleNameBn = vehicleNameBn,
            etaMinutes = 10
        )
        val tripStatus = try {
            TripStatus.valueOf(status)
        } catch (e: Exception) {
            TripStatus.COMPLETED
        }

        return Trip(
            id = id,
            booking = booking,
            driver = driver,
            status = tripStatus,
            paymentMethod = paymentMethod,
            fare = fare,
            dateFormatted = dateFormatted,
            rating = rating,
            review = review
        )
    }

    companion object {
        fun fromTrip(trip: Trip): TripEntity {
            return TripEntity(
                id = trip.id,
                vehicleId = trip.booking.vehicle.id,
                vehicleNameEn = trip.booking.vehicle.nameEn,
                vehicleNameBn = trip.booking.vehicle.nameBn,
                pickupId = trip.booking.pickup.id,
                pickupNameEn = trip.booking.pickup.nameEn,
                pickupNameBn = trip.booking.pickup.nameBn,
                dropId = trip.booking.drop.id,
                dropNameEn = trip.booking.drop.nameEn,
                dropNameBn = trip.booking.drop.nameBn,
                dateString = trip.booking.dateString,
                timeString = trip.booking.timeString,
                goodsCategory = trip.booking.goodsCategory,
                serviceType = trip.booking.serviceType,
                distanceKm = trip.booking.distanceKm,
                driverId = trip.driver.id,
                driverNameEn = trip.driver.nameEn,
                driverNameBn = trip.driver.nameBn,
                driverPhone = trip.driver.phone,
                driverRating = trip.driver.rating,
                driverVehiclePlate = trip.driver.vehiclePlate,
                status = trip.status.name,
                paymentMethod = trip.paymentMethod,
                fare = trip.fare,
                dateFormatted = trip.dateFormatted,
                rating = trip.rating,
                review = trip.review,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
