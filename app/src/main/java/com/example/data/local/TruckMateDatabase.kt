package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        TripEntity::class,
        SavedAddressEntity::class,
        NotificationEntity::class,
        WalletTransactionEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TruckMateDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun savedAddressDao(): SavedAddressDao
    abstract fun notificationDao(): NotificationDao
    abstract fun walletDao(): WalletDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: TruckMateDatabase? = null

        fun getDatabase(context: Context): TruckMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TruckMateDatabase::class.java,
                    "truckmate_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
