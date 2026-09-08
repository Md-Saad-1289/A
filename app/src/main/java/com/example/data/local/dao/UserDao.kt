package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 'current_user' LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = walletBalance + :amount, updatedAt = :timestamp WHERE id = 'current_user'")
    suspend fun addWalletBalance(amount: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE users SET phone = :phone, updatedAt = :timestamp WHERE id = 'current_user'")
    suspend fun updatePhone(phone: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE users SET isLoggedIn = :isLoggedIn, updatedAt = :timestamp WHERE id = 'current_user'")
    suspend fun updateLoginState(isLoggedIn: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE users SET language = :language, updatedAt = :timestamp WHERE id = 'current_user'")
    suspend fun updateLanguage(language: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE users SET isDarkMode = :isDarkMode, updatedAt = :timestamp WHERE id = 'current_user'")
    suspend fun updateDarkMode(isDarkMode: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
