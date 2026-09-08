package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.NotificationItem

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleBn: String,
    val messageEn: String,
    val messageBn: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toNotificationItem(): NotificationItem {
        return NotificationItem(
            id = id,
            titleEn = titleEn,
            titleBn = titleBn,
            messageEn = messageEn,
            messageBn = messageBn,
            timeAgo = timeAgo,
            isRead = isRead
        )
    }

    companion object {
        fun fromNotificationItem(item: NotificationItem): NotificationEntity {
            return NotificationEntity(
                id = item.id,
                titleEn = item.titleEn,
                titleBn = item.titleBn,
                messageEn = item.messageEn,
                messageBn = item.messageBn,
                timeAgo = item.timeAgo,
                isRead = item.isRead,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
