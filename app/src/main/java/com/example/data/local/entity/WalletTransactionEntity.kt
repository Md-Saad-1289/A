package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.WalletTransaction

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleBn: String,
    val date: String,
    val amount: Int,
    val isCredit: Boolean,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toWalletTransaction(): WalletTransaction {
        return WalletTransaction(
            id = id,
            titleEn = titleEn,
            titleBn = titleBn,
            date = date,
            amount = amount,
            isCredit = isCredit
        )
    }

    companion object {
        fun fromWalletTransaction(tx: WalletTransaction): WalletTransactionEntity {
            return WalletTransactionEntity(
                id = tx.id,
                titleEn = tx.titleEn,
                titleBn = tx.titleBn,
                date = tx.date,
                amount = tx.amount,
                isCredit = tx.isCredit,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
