package com.example.data.backend

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.UUID

class MockLogisticsBackendInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Simulate network latency (150ms)
        Thread.sleep(150)

        val jsonContentType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val responseJson = when {
            path.endsWith("/api/v1/vehicles") -> {
                """
                {
                    "status": "success",
                    "count": 4,
                    "vehicles": [
                        {"id": "v_pickup_7ft", "nameEn": "7ft Pickup", "nameBn": "৭ ফুট পিকআপ", "baseFare": 500, "perKmRate": 35, "capacity": "1.5 ton", "category": "PICKUP"},
                        {"id": "v_truck_12ft", "nameEn": "12ft Truck", "nameBn": "১২ ফুট ট্রাক", "baseFare": 1000, "perKmRate": 60, "capacity": "3 ton", "category": "TRUCK"},
                        {"id": "v_truck_14ft", "nameEn": "14ft Truck", "nameBn": "১৪ ফুট ট্রাক", "baseFare": 1600, "perKmRate": 85, "capacity": "5 ton", "category": "TRUCK"},
                        {"id": "v_covered_van", "nameEn": "Covered Van", "nameBn": "কাভার্ড ভ্যান", "baseFare": 850, "perKmRate": 50, "capacity": "2 ton", "category": "COVERED"}
                    ]
                }
                """.trimIndent()
            }

            path.endsWith("/api/v1/fare/estimate") -> {
                """
                {
                    "distanceKm": 6.8,
                    "estimatedMinutes": 24,
                    "baseFare": 500,
                    "distanceFare": 238,
                    "discount": 50,
                    "totalFare": 688,
                    "currency": "BDT"
                }
                """.trimIndent()
            }

            path.endsWith("/api/v1/bookings/create") -> {
                val bookingId = "BK-${UUID.randomUUID().toString().take(8).uppercase()}"
                """
                {
                    "bookingId": "$bookingId",
                    "status": "BROADCASTING_TO_DRIVERS",
                    "message": "Booking submitted successfully. Nearby drivers are placing competitive bids.",
                    "createdAt": ${System.currentTimeMillis()}
                }
                """.trimIndent()
            }

            path.contains("/bids") -> {
                """
                {
                    "bookingId": "BK-9841",
                    "bidsCount": 4,
                    "bids": [
                        {"bidId": "bid_1", "driverId": "drv_1", "driverName": "Rahim Hossain", "driverPhone": "+880 1812 998877", "rating": 4.8, "tripsCount": 120, "vehiclePlate": "Dhaka Metro - 11-2345", "bidAmount": 620, "etaMinutes": 12},
                        {"bidId": "bid_2", "driverId": "drv_2", "driverName": "Karim Sheikh", "driverPhone": "+880 1711 445566", "rating": 4.6, "tripsCount": 98, "vehiclePlate": "Dhaka Metro - 13-8941", "bidAmount": 640, "etaMinutes": 16},
                        {"bidId": "bid_3", "driverId": "drv_3", "driverName": "Sabbir Ahmed", "driverPhone": "+880 1913 223344", "rating": 4.5, "tripsCount": 75, "vehiclePlate": "Dhaka Metro - 15-5678", "bidAmount": 660, "etaMinutes": 22},
                        {"bidId": "bid_4", "driverId": "drv_4", "driverName": "Tanvir Islam", "driverPhone": "+880 1610 889900", "rating": 4.9, "tripsCount": 210, "vehiclePlate": "Dhaka Metro - 18-9012", "bidAmount": 600, "etaMinutes": 8}
                    ]
                }
                """.trimIndent()
            }

            path.contains("/accept-bid") -> {
                """
                {
                    "tripId": "TRIP-${(1000..9999).random()}",
                    "currentStatus": "ASSIGNED",
                    "driverId": "drv_1",
                    "driverLat": 23.7561,
                    "driverLng": 90.3872,
                    "message": "Driver accepted and dispatch confirmed."
                }
                """.trimIndent()
            }

            path.contains("/advance-status") -> {
                """
                {
                    "tripId": "TRIP-CURRENT",
                    "currentStatus": "IN_TRANSIT",
                    "driverId": "drv_1",
                    "driverLat": 23.7750,
                    "driverLng": 90.3800,
                    "message": "Trip status updated in backend dispatcher."
                }
                """.trimIndent()
            }

            path.contains("/cancel") -> {
                """
                {
                    "tripId": "TRIP-CURRENT",
                    "currentStatus": "CANCELLED",
                    "driverId": null,
                    "driverLat": null,
                    "driverLng": null,
                    "message": "Trip cancelled successfully in backend system."
                }
                """.trimIndent()
            }

            path.contains("/rate") -> {
                """
                {
                    "tripId": "TRIP-CURRENT",
                    "currentStatus": "COMPLETED",
                    "driverId": "drv_1",
                    "driverLat": null,
                    "driverLng": null,
                    "message": "Driver rating & feedback recorded in logistics backend."
                }
                """.trimIndent()
            }

            path.endsWith("/api/v1/wallet/topup") -> {
                val txId = "TX-${UUID.randomUUID().toString().take(8).uppercase()}"
                """
                {
                    "transactionId": "$txId",
                    "amount": 1000,
                    "newBalance": 2250,
                    "status": "SUCCESS"
                }
                """.trimIndent()
            }

            path.endsWith("/api/v1/fleet/status") -> {
                """
                {
                    "activeStand": "Tejgaon Truck Stand, Dhaka",
                    "activeDriversCount": 84,
                    "availableTrucks": 52,
                    "avgEtaMinutes": 9,
                    "timestamp": ${System.currentTimeMillis()}
                }
                """.trimIndent()
            }

            else -> {
                """{"status": "ok", "timestamp": ${System.currentTimeMillis()}}"""
            }
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseJson.toResponseBody(jsonContentType))
            .build()
    }
}
