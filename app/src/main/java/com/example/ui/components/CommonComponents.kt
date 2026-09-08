package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun TruckMateLogo(
    modifier: Modifier = Modifier,
    showTagline: Boolean = true,
    isDark: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Stylized vector Truck Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TruckEmerald),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "TruckMate Logo",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Truck",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else TruckEmeraldDark,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Mate",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TruckOrange,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
        }
        if (showTagline) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your Load   Our Ride",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFF94A3B8) else TextSecondaryLight,
                letterSpacing = 1.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruckMateTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun StylizedInteractiveMap(
    modifier: Modifier = Modifier,
    pickupName: String = "Farmgate",
    dropName: String = "Mirpur 10",
    animatedProgress: Float = 0.5f,
    onRecenter: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEBF1ED))
    ) {
        // Map Canvas with Roads, Grids, and Route
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Background subtle grid
            val gridSpacing = 40.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0xFFDFE7E2),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0xFFDFE7E2),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // Stylized River / Water body
            val riverPath = Path().apply {
                moveTo(0f, height * 0.25f)
                cubicTo(
                    width * 0.3f, height * 0.2f,
                    width * 0.5f, height * 0.45f,
                    width, height * 0.35f
                )
                lineTo(width, height * 0.45f)
                cubicTo(
                    width * 0.5f, height * 0.55f,
                    width * 0.3f, height * 0.3f,
                    0f, height * 0.35f
                )
                close()
            }
            drawPath(path = riverPath, color = Color(0xFFCCE3DE))

            // Main Road lines (Gray background roads)
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(0f, height * 0.65f),
                end = Offset(width, height * 0.3f),
                strokeWidth = 12f
            )
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(width * 0.25f, 0f),
                end = Offset(width * 0.75f, height),
                strokeWidth = 14f
            )
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(width * 0.1f, height * 0.1f),
                end = Offset(width * 0.9f, height * 0.85f),
                strokeWidth = 10f
            )

            // Polyline connecting Pickup to Drop (Blue/Teal gradient look)
            val startPoint = Offset(width * 0.18f, height * 0.3f)
            val endPoint = Offset(width * 0.82f, height * 0.72f)
            val midControl1 = Offset(width * 0.35f, height * 0.45f)
            val midControl2 = Offset(width * 0.65f, height * 0.55f)

            val routePath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                cubicTo(midControl1.x, midControl1.y, midControl2.x, midControl2.y, endPoint.x, endPoint.y)
            }

            // Draw route line
            drawPath(
                path = routePath,
                color = Color(0xFF0284C7),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Pulse around pickup
            drawCircle(
                color = Color(0x4410B981),
                radius = pulseRadius,
                center = startPoint
            )

            // Start Pickup Pin (Green)
            drawCircle(
                color = Color(0xFF10B981),
                radius = 7.dp.toPx(),
                center = startPoint
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = startPoint
            )

            // End Drop Pin (Red)
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 7.dp.toPx(),
                center = endPoint
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = endPoint
            )

            // Moving truck along route
            val truckProgress = animatedProgress.coerceIn(0f, 1f)
            val currentTruckPos = Offset(
                x = startPoint.x + (endPoint.x - startPoint.x) * truckProgress,
                y = startPoint.y + (endPoint.y - startPoint.y) * truckProgress
            )
            drawCircle(
                color = TruckEmeraldDark,
                radius = 9.dp.toPx(),
                center = currentTruckPos
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = currentTruckPos
            )
        }

        // Recenter button overlay
        IconButton(
            onClick = onRecenter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(36.dp)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .testTag("map_recenter_button")
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Recenter Map",
                tint = TruckEmerald,
                modifier = Modifier.size(20.dp)
            )
        }

        // Top labels
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .shadow(2.dp, RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF10B981), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = pickupName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
            Text(text = " → ", fontSize = 11.sp, color = TextSecondaryLight)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFEF4444), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = dropName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    statusColor: Color = StatusGreen,
    backgroundColor: Color = StatusGreenLight
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            color = statusColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TruckMateBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TruckEmerald,
                selectedTextColor = TruckEmerald,
                indicatorColor = TruckMint
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = { onNavigate("history") },
            icon = {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Bookings"
                )
            },
            label = { Text("Bookings", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TruckEmerald,
                selectedTextColor = TruckEmerald,
                indicatorColor = TruckMint
            ),
            modifier = Modifier.testTag("nav_item_bookings")
        )

        NavigationBarItem(
            selected = currentRoute == "wallet",
            onClick = { onNavigate("wallet") },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Wallet"
                )
            },
            label = { Text("Wallet", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TruckEmerald,
                selectedTextColor = TruckEmerald,
                indicatorColor = TruckMint
            ),
            modifier = Modifier.testTag("nav_item_wallet")
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TruckEmerald,
                selectedTextColor = TruckEmerald,
                indicatorColor = TruckMint
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}

