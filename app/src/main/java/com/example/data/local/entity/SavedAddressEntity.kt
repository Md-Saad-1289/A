package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AddressType
import com.example.model.SavedAddress

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey val id: String,
    val type: String,
    val titleEn: String,
    val titleBn: String,
    val addressEn: String,
    val addressBn: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toSavedAddress(): SavedAddress {
        val addressType = try {
            AddressType.valueOf(type)
        } catch (e: Exception) {
            AddressType.OTHER
        }
        return SavedAddress(
            id = id,
            type = addressType,
            titleEn = titleEn,
            titleBn = titleBn,
            addressEn = addressEn,
            addressBn = addressBn
        )
    }

    companion object {
        fun fromSavedAddress(address: SavedAddress): SavedAddressEntity {
            return SavedAddressEntity(
                id = address.id,
                type = address.type.name,
                titleEn = address.titleEn,
                titleBn = address.titleBn,
                addressEn = address.addressEn,
                addressBn = address.addressBn,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
