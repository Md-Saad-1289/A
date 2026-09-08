package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.SavedAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedAddressDao {
    @Query("SELECT * FROM saved_addresses ORDER BY createdAt DESC")
    fun getAllAddresses(): Flow<List<SavedAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: SavedAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<SavedAddressEntity>)

    @Query("DELETE FROM saved_addresses WHERE id = :addressId")
    suspend fun deleteAddress(addressId: String)

    @Query("SELECT COUNT(*) FROM saved_addresses")
    suspend fun getAddressCount(): Int
}
