package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String = "Tariq Rahman",
    val phone: String = "+880 1712 345678",
    val email: String = "tariq.rahman@truckmate.bd",
    val walletBalance: Int = 1250,
    val isLoggedIn: Boolean = true,
    val language: String = "BANGLA",
    val isDarkMode: Boolean = false,
    val totalTrips: Int = 14,
    val memberSince: String = "March 2024",
    val updatedAt: Long = System.currentTimeMillis()
)
