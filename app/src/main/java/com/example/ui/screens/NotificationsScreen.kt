package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.NotificationItem
import com.example.ui.components.TruckMateTopBar
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun NotificationsScreen(
    language: AppLanguage,
    notifications: List<NotificationItem>,
    onNotificationClick: (NotificationItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TruckMateTopBar(
                title = AppStrings.t("notifications", language),
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(notifications) { notif ->
                    NotificationCard(
                        notif = notif,
                        language = language,
                        onClick = { onNotificationClick(notif) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: NotificationItem,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_card_${notif.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (notif.isRead) MaterialTheme.colorScheme.surface else TruckMintSubtle,
        border = BorderStroke(
            1.dp,
            if (notif.isRead) MaterialTheme.colorScheme.outline.copy(alpha = 0.35f) else TruckMint
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant else TruckMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        notif.titleEn.contains("Driver", ignoreCase = true) -> Icons.Default.LocalShipping
                        notif.titleEn.contains("Promo", ignoreCase = true) -> Icons.Default.LocalOffer
                        notif.titleEn.contains("Payment", ignoreCase = true) -> Icons.Default.Payments
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (notif.isRead) MaterialTheme.colorScheme.onSurfaceVariant else TruckEmerald,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) notif.titleBn else notif.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (!notif.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TruckOrange)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (language == AppLanguage.BANGLA) notif.messageBn else notif.messageEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notif.timeAgo,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
