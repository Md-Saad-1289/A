package com.example.data.backend

import retrofit2.Response
import retrofit2.http.*

interface TruckMateApiService {

    @GET("api/v1/vehicles")
    suspend fun getVehicles(): Response<VehiclesResponseDto>

    @POST("api/v1/fare/estimate")
    suspend fun estimateFare(
        @Body request: FareEstimateRequestDto
    ): Response<FareEstimateResponseDto>

    @POST("api/v1/bookings/create")
    suspend fun createBooking(
        @Body request: CreateBookingRequestDto
    ): Response<CreateBookingResponseDto>

    @GET("api/v1/bookings/{bookingId}/bids")
    suspend fun getDriverBids(
        @Path("bookingId") bookingId: String
    ): Response<DriverBidsResponseDto>

    @POST("api/v1/bookings/{bookingId}/accept-bid")
    suspend fun acceptBid(
        @Path("bookingId") bookingId: String,
        @Body request: AcceptBidRequestDto
    ): Response<TripStatusResponseDto>

    @POST("api/v1/trips/{tripId}/advance-status")
    suspend fun advanceTripStatus(
        @Path("tripId") tripId: String
    ): Response<TripStatusResponseDto>

    @POST("api/v1/trips/{tripId}/cancel")
    suspend fun cancelTrip(
        @Path("tripId") tripId: String
    ): Response<TripStatusResponseDto>

    @POST("api/v1/trips/{tripId}/rate")
    suspend fun rateTrip(
        @Path("tripId") tripId: String,
        @Body request: RateTripRequestDto
    ): Response<TripStatusResponseDto>

    @POST("api/v1/wallet/topup")
    suspend fun topUpWallet(
        @Body request: TopUpRequestDto
    ): Response<TopUpResponseDto>

    @GET("api/v1/fleet/status")
    suspend fun getFleetStatus(): Response<FleetStatusResponseDto>
}
