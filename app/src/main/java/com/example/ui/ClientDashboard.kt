package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Booking
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    viewModel: ProductionViewModel,
    onNavigateToBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bookingsList by viewModel.bookings.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CLIENT COCKPIT",
                        fontWeight = FontWeight.ExtraBold,
                        color = HeadingBlack,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundIvory)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundIvory)
                .padding(16.dp)
        ) {
            // Header stats
            Card(
                colors = CardDefaults.cardColors(containerColor = HeadingBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "INDORE PIPELINE",
                            style = MaterialTheme.typography.labelLarge,
                            color = AccentLime
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track deliverables, invoices & calendars",
                            color = BackgroundIvory,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MY COMMITTED BOOKINGS",
                style = MaterialTheme.typography.titleLarge,
                color = HeadingBlack,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (bookingsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "You haven't initiated any shoots yet.",
                            color = MutedText,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToBooking,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = HeadingBlack)
                        ) {
                            Text("BOOK INTRO SHOOT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookingsList) { booking ->
                        BookingStatusCard(booking, context)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct WhatsApp Support Desk card
            Card(
                colors = CardDefaults.cardColors(containerColor = AccentLime.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentLime.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val url = "https://wa.me/919755550380?text=Hi%20Mohit,%20I%20am%20using%20the%20Client%20Portal%20and%20need%20to%20discuss%20my%20ongoing%20brand%20shoots!"
                        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                        context.startActivity(intent)
                    }
                    .testTag("card_client_whatsapp_support")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning, // Standard fallback icon
                        contentDescription = "WhatsApp Coordinator",
                        tint = HeadingBlack,
                        modifier = Modifier
                            .size(28.dp)
                            .background(AccentLime, RoundedCornerShape(percent = 50))
                            .padding(6.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DIRECT WHATSAPP COORDINATOR",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = HeadingBlack,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Ping Mohit Dewda directly for high-priority timeline changes or custom pricing details.",
                            fontSize = 10.sp,
                            color = HeadingBlack.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingStatusCard(booking: Booking, context: Context) {
    val statusColor = when (booking.status) {
        "Confirmed" -> AccentLime
        "In Progress" -> Color.Cyan
        "Completed" -> Color.Green
        else -> Color.LightGray // Pending / Cancelled
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkPremium),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_status_card_${booking.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Classy status indicator row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.serviceName.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "DATE", color = SoftGray, fontSize = 10.sp)
                    Text(text = booking.shootDate, color = BackgroundIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "TIME", color = SoftGray, fontSize = 10.sp)
                    Text(text = booking.shootTime, color = BackgroundIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "PACKAGE", color = SoftGray, fontSize = 10.sp)
                    Text(text = booking.packageName.uppercase(), color = AccentLime, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = SolidWhite.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            // Google Drive Delivery Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "DELIVERY VAULT", color = SoftGray, fontSize = 10.sp)
                    if (booking.googleDriveLink.isNotEmpty()) {
                        Text(
                            text = "✓ READY IN GOOGLE DRIVE",
                            color = Color.Green,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(booking.googleDriveLink)
                                    }
                                    context.startActivity(intent)
                                }
                                .testTag("btn_drive_${booking.id}")
                        )
                    } else {
                        Text(text = "⏳ Processing edit renders...", color = SoftGray, fontSize = 13.sp)
                    }
                }

                if (booking.googleDriveLink.isNotEmpty()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(booking.googleDriveLink)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = HeadingBlack),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("LAUNCH DRIVE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fees, Advances, Paytm/PhonePe reminders
            Card(
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Contract Value", color = SoftGray, fontSize = 12.sp)
                        Text(text = "₹${booking.invoiceAmount}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "50% Advance Lock", color = SoftGray, fontSize = 11.sp)
                        Text(
                            text = if (booking.isAdvancePaid) "✓ PAID & CONFIRMED" else "⏳ DUE: APPROVED AT BANK",
                            color = if (booking.isAdvancePaid) AccentLime else Color.Yellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "50% Delivery Balance", color = SoftGray, fontSize = 11.sp)
                        Text(
                            text = if (booking.isBalancePaid) "✓ SETTLED COMPLETELY" else "DUE POST-SHOOT",
                            color = if (booking.isBalancePaid) AccentLime else SoftGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
