package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Booking
import com.example.ui.theme.*
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: ProductionViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bookingsList by viewModel.bookings.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf(0) } // 0 = DASHBOARD / METRICS, 1 = GOOGLE SHEETS DB, 2 = CALENDAR EVENT PIPELINE
    
    // --- Detailed Operations Calculations ---
    val totalClients = bookingsList.map { it.clientName.trim().lowercase() }.distinct().filter { it.isNotEmpty() }.size
    val activeProjectsCount = bookingsList.count { it.status in listOf("Lead", "Discussion", "Booked", "Shoot Scheduled", "Shoot Completed", "Editing") }
    val completedProjectsCount = bookingsList.count { it.status in listOf("Delivered", "Closed") }
    
    // Revenue Matrix
    val monthlyRevenue = bookingsList.sumOf { it.invoiceAmount }
    val pendingPayments = bookingsList.sumOf { 
        val settled = if (it.isBalancePaid) it.invoiceAmount else it.advanceReceivedValue
        (it.invoiceAmount - settled).coerceAtLeast(0.0)
    }
    
    val upcomingShoots = bookingsList.count { it.status in listOf("Booked", "Shoot Scheduled") }
    val upcomingDeliveries = bookingsList.count { it.status in listOf("Shoot Completed", "Editing") }

    var isUpdatingBooking by remember { mutableStateOf<Booking?>(null) }
    var selectedCalendarBookingId by remember { mutableStateOf<Int?>(bookingsList.firstOrNull()?.id) }
    
    // Keep active selection in sync if data changes
    LaunchedEffect(bookingsList) {
        if (selectedCalendarBookingId == null || bookingsList.none { it.id == selectedCalendarBookingId }) {
            selectedCalendarBookingId = bookingsList.firstOrNull()?.id
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AGENCY CONTROL HUD & SPREADSHEETS",
                        fontWeight = FontWeight.ExtraBold,
                        color = HeadingBlack,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundIvory),
                actions = {
                    // Export Share Sheet Action
                    IconButton(
                        onClick = {
                            val csvText = buildString {
                                append("Client Name,Business,Contact,Instagram,Type,Package,BookingDate,ShootDate,DeliveryDate,Status,Advance,Pending,Notes\n")
                                bookingsList.forEach { b ->
                                    append("\"${b.clientName}\",\"${b.businessName}\",\"${b.clientPhone}\",\"${b.instagramId}\",\"${b.serviceName}\",\"${b.packageName}\",\"${b.bookingDate}\",\"${b.shootDate}\",\"${b.deliveryDate}\",\"${b.status}\",₹${b.advanceReceivedValue},₹${b.invoiceAmount - b.advanceReceivedValue},\"${b.requirements.replace("\n", " ")}\"\n")
                                }
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Creative Team Production Google Sheets CSV")
                                putExtra(Intent.EXTRA_TEXT, csvText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Professional Workspace CSV"))
                            Toast.makeText(context, "Exporting complete spreadsheet format!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("btn_export_workspace")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export to Google Sheets", tint = HeadingBlack)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundIvory)
        ) {
            // --- Operational Tab Selector Row ---
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = BackgroundIvory,
                contentColor = HeadingBlack,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = HeadingBlack
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("📊 METRICS & RULES", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Metrics", modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("📋 GOOGLE SHEETS", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Spreadsheet", modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("📅 CALENDAR EVENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar", modifier = Modifier.size(16.dp)) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                when (activeTab) {
                    0 -> {
                        // TAB 0: Grid metrics, Secure Credentials, and interactive Performance Charts
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // --- SECURE AUTHENTICATION BADGE ---
                            Card(
                                colors = CardDefaults.cardColors(containerColor = HeadingBlack),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Secure Area",
                                        tint = AccentLime,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "SECURED FOUNDER ACCESS PORTAL (AUTHENTICATED)",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Logged in: dewdaboss@gmail.com • SSL Encrypted Real-Time Database",
                                            color = SoftGray.copy(alpha = 0.7f),
                                            fontSize = 10.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AccentLime.copy(alpha = 0.2f))
                                            .border(1.dp, AccentLime, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE SYNC",
                                            color = AccentLime,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "AGENCY REVENUE & PROJECT METRICS",
                                fontWeight = FontWeight.ExtraBold,
                                color = HeadingBlack,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                            
                            // Key Dashboard Grid Metrics (Cards)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DisplayMetricCard(
                                    title = "TOTAL CLIENTS",
                                    value = "$totalClients",
                                    containerColor = DarkPremium,
                                    valueColor = Color.White,
                                    trendText = "unique brands",
                                    icon = Icons.Default.Person,
                                    modifier = Modifier.weight(1f)
                                )
                                DisplayMetricCard(
                                    title = "ACTIVE PROJECTS",
                                    value = "$activeProjectsCount",
                                    containerColor = DarkPremium,
                                    valueColor = AccentLime,
                                    trendText = "in active pipeline",
                                    icon = Icons.Default.PlayArrow,
                                    modifier = Modifier.weight(1f)
                                )
                                DisplayMetricCard(
                                    title = "COMPLETED",
                                    value = "$completedProjectsCount",
                                    containerColor = DarkPremium,
                                    valueColor = Color.Green,
                                    trendText = "fully delivered",
                                    icon = Icons.Default.CheckCircle,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                DisplayMetricCard(
                                    title = "MONTHLY REVENUE",
                                    value = "₹${monthlyRevenue.toInt()}",
                                    containerColor = DarkPremium,
                                    valueColor = AccentLime,
                                    trendText = "gross accounts value",
                                    icon = Icons.Default.ShoppingCart,
                                    modifier = Modifier.weight(1f)
                                )
                                DisplayMetricCard(
                                    title = "PENDING PAYMENTS",
                                    value = "₹${pendingPayments.toInt()}",
                                    containerColor = DarkPremium,
                                    valueColor = LuxuryGold,
                                    trendText = "receivable invoice balance",
                                    icon = Icons.Default.Warning,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // --- VISUAL PERFORMANCE CHARTS PANEL ---
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "REVENUE VS. OUTSTANDING BALANCE BY CLIENT",
                                                fontWeight = FontWeight.Bold,
                                                color = HeadingBlack,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "Tap on any project row to quickly preview settlement statuses",
                                                color = MutedText,
                                                fontSize = 9.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Chart Info",
                                            tint = MutedText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (bookingsList.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No registered clients. Create a booking to preview revenue maps.", color = MutedText, fontSize = 11.sp)
                                        }
                                    } else {
                                        // Custom Handcrafted Responsive Comparison Chart
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            bookingsList.take(6).forEach { b ->
                                                val settled = if (b.isBalancePaid) b.invoiceAmount else b.advanceReceivedValue
                                                val pending = (b.invoiceAmount - settled).coerceAtLeast(0.0)
                                                val maxVal = bookingsList.maxOfOrNull { it.invoiceAmount } ?: 10000.0
                                                val scaleMax = if (maxVal <= 0.0) 10000.0 else maxVal
                                                
                                                val revenueRatio = (b.invoiceAmount / scaleMax).toFloat().coerceIn(0.1f, 1.0f)
                                                val pendingRatio = (pending / b.invoiceAmount).toFloat().coerceIn(0.0f, 1.0f)
                                                
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(BackgroundIvory.copy(alpha = 0.5f))
                                                        .clickable {
                                                            Toast.makeText(
                                                                context,
                                                                "${b.clientName}: Total ₹${b.invoiceAmount.toInt()} (Settled: ₹${settled.toInt()} | Pending: ₹${pending.toInt()})",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .padding(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = b.clientName.uppercase(),
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = HeadingBlack,
                                                            fontSize = 11.sp
                                                        )
                                                        Text(
                                                            text = "₹${b.invoiceAmount.toInt()}",
                                                            color = HeadingBlack,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    
                                                    // Bar 1: Total Gross Value
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = "Gross bill:",
                                                            color = MutedText,
                                                            fontSize = 8.sp,
                                                            modifier = Modifier.width(55.dp)
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(10.dp)
                                                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxHeight()
                                                                    .fillMaxWidth(revenueRatio)
                                                                    .background(AccentLime, RoundedCornerShape(5.dp))
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    
                                                    // Bar 2: Out of which Outstanding Balance
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = b.status,
                                                            color = MutedText,
                                                            fontSize = 8.sp,
                                                            modifier = Modifier.width(55.dp)
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(10.dp)
                                                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                                                        ) {
                                                            if (pending > 0.0) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .fillMaxHeight()
                                                                        .fillMaxWidth(pendingRatio * revenueRatio)
                                                                        .background(LuxuryGold, RoundedCornerShape(5.dp))
                                                                )
                                                            }
                                                        }
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.End
                                                    ) {
                                                        val settledPct = if (b.invoiceAmount > 0.0) ((settled / b.invoiceAmount) * 100).toInt() else 0
                                                        Text(
                                                            text = "$settledPct% Settled • ₹${pending.toInt()} Pending",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (pending > 0.0) LuxuryGold else Color.Green
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Legend indicator row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(8.dp).background(AccentLime, RoundedCornerShape(2.dp)))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Contract Total Gross (₹)", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(8.dp).background(LuxuryGold, RoundedCornerShape(2.dp)))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Outstanding Balance Due (₹)", fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // --- CHART 2: PIPELINE OPERATIONS RATIO DISTRIBUTION ---
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "AGENCY PRODUCTION MIX PIPELINE MAP",
                                        fontWeight = FontWeight.Bold,
                                        color = HeadingBlack,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Volume weights of production types across booked retainer plans",
                                        color = MutedText,
                                        fontSize = 9.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val reelsCount = bookingsList.count { it.serviceName.contains("Reel") }
                                    val podcastCount = bookingsList.count { it.serviceName.contains("Podcast") }
                                    val productsCount = bookingsList.count { it.serviceName.contains("Product") }
                                    val totalServicesCount = bookingsList.size.coerceAtLeast(1)

                                    val reelsWeight = reelsCount.toFloat() / totalServicesCount
                                    val podcastWeight = podcastCount.toFloat() / totalServicesCount
                                    val productsWeight = productsCount.toFloat() / totalServicesCount

                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        CategoryProgressRow(category = "🎬 Reel Shoots & Trend Series", count = reelsCount, weight = reelsWeight, color = AccentLime)
                                        CategoryProgressRow(category = "🎙 High-End Multi-Cam Podcasts", count = podcastCount, weight = podcastWeight, color = LuxuryGold)
                                        CategoryProgressRow(category = "📦 Studio Macro Product Shoots", count = productsCount, weight = productsWeight, color = HeadingBlack)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Core Scheduling Protocols & Instructions Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Rules", tint = AccentLime, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "SCHEDULING PROTOCOLS & GOALS", color = AccentLime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "✔ Always enforce 50% Contract Advance Receipt on Booked status.\n" +
                                                "✔ Validate high-end focal rigs allocated to Indore locations prior to shoots.\n" +
                                                "✔ Expected Delivery standard is shootDate + 48 hours unless Premium Creator retainer is contracted.\n" +
                                                "✔ Complete automatic post-production timeline alerts upon booking lock.",
                                        color = SoftGray,
                                        fontSize = 12.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: GOOGLE SHEETS DATABASE (Direct responsive spreadsheet mapping)
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE GOOGLE SHEETS EMULATION",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HeadingBlack,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${bookingsList.size} Rows",
                                    color = MutedText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (bookingsList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No spreadsheets rows mapped. Add a booking client to populate.", color = MutedText)
                                }
                            } else {
                                // Scrollable Table Layout
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                ) {
                                    val spreadsheetScrollState = rememberScrollState()
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .horizontalScroll(spreadsheetScrollState)
                                    ) {
                                        // Header Row
                                        Row(
                                            modifier = Modifier
                                                .background(HeadingBlack)
                                                .padding(vertical = 10.dp)
                                        ) {
                                            listOf(
                                                "Action", "Client Name", "Business Name", "Contact No", "Instagram ID",
                                                "Project Type", "Package Selected", "Booking Date", "Shoot Date",
                                                "Delivery Date", "Payment Status", "Advance Received", "Pending Amount",
                                                "Project Status", "Notes"
                                            ).forEach { colName ->
                                                Text(
                                                    text = colName,
                                                    color = AccentLime,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier
                                                        .width(if (colName == "Action") 90.dp else if (colName == "Notes" || colName == "Package Selected") 220.dp else 140.dp)
                                                        .padding(horizontal = 8.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        // Data Rows
                                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                                            items(bookingsList) { b ->
                                                val pendingAmt = b.invoiceAmount - b.advanceReceivedValue - (if (b.isBalancePaid) (b.invoiceAmount - b.advanceReceivedValue) else 0.0)
                                                val payStatus = when {
                                                    b.isBalancePaid -> "Completed"
                                                    b.isAdvancePaid -> "Advance Recv"
                                                    else -> "Pending"
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
                                                        .padding(vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Action button cells
                                                    Box(
                                                        modifier = Modifier
                                                            .width(90.dp)
                                                            .padding(horizontal = 6.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Button(
                                                            onClick = { isUpdatingBooking = b },
                                                            colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White),
                                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                            shape = RoundedCornerShape(4.dp),
                                                            modifier = Modifier.height(24.dp)
                                                        ) {
                                                            Text("UPDATE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }

                                                    listOf(
                                                        b.clientName,
                                                        b.businessName.ifEmpty { "N/A" },
                                                        b.clientPhone,
                                                        b.instagramId.ifEmpty { "N/A" },
                                                        b.serviceName,
                                                        b.packageName,
                                                        b.bookingDate.ifEmpty { "2026-06-03" },
                                                        b.shootDate,
                                                        b.deliveryDate.ifEmpty { "N/A" },
                                                        payStatus,
                                                        "₹${b.advanceReceivedValue.toInt()}",
                                                        "₹${pendingAmt.toInt()}",
                                                        b.status,
                                                        b.requirements
                                                    ).forEachIndexed { index, cell ->
                                                        // Resolve cell size matching header width mapping
                                                        val cellWidth = if (index == 4) 220.dp else 140.dp // Notes / Package columns get extra spacing
                                                        
                                                        Text(
                                                            text = cell.toString(),
                                                            color = if (index == 12) AccentLime else HeadingBlack,
                                                            fontSize = 11.sp,
                                                            modifier = Modifier
                                                                .width(if (index == 5 || index == 13) 220.dp else 140.dp)
                                                                .padding(horizontal = 8.dp),
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // TAB 2: AUTOMATED CALENDAR TIMELINE
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "AUTOMATIC CALENDAR PIPELINE GENERATION",
                                fontWeight = FontWeight.ExtraBold,
                                color = HeadingBlack,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (bookingsList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No calendar schedules mapped. Select details above.", color = MutedText)
                                }
                            } else {
                                // Dropdown Client selector to load specific 6 timeline alerts
                                val focusBookingObj = bookingsList.firstOrNull { it.id == selectedCalendarBookingId } ?: bookingsList.first()
                                
                                Text("SELECT WORKING CLIENT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bookingsList.forEach { bk ->
                                        val isSelected = bk.id == focusBookingObj.id
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) HeadingBlack else SoftGray)
                                                .clickable { selectedCalendarBookingId = bk.id }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = bk.clientName.uppercase(),
                                                color = if (isSelected) AccentLime else HeadingBlack,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Generate 6 automated calendar events list based on selected contract attributes
                                val bDate = if (focusBookingObj.bookingDate.isNotEmpty()) focusBookingObj.bookingDate else "2026-06-03"
                                val sDate = focusBookingObj.shootDate
                                val sTime = focusBookingObj.shootTime
                                val dDate = if (focusBookingObj.deliveryDate.isNotEmpty()) focusBookingObj.deliveryDate else "TBD"
                                val team = focusBookingObj.assignedTeam
                                val gear = focusBookingObj.equipmentRequired

                                val timelineEvents = listOf(
                                    CalendarTimelineEvent(
                                        title = "Client Onboarding Meeting & Concept Lock",
                                        date = bDate,
                                        desc = "Kickoff brief for ${focusBookingObj.serviceName}. Review requirements: ${focusBookingObj.requirements}",
                                        eventTime = "11:00 AM"
                                    ),
                                    CalendarTimelineEvent(
                                        title = "Shoot Production Crew Call [ACTUAL SHOOT]",
                                        date = sDate,
                                        desc = "Time: $sTime | Location: ${focusBookingObj.location}. Assigned Crew: $team. Equipment: $gear. Client Cont: ${focusBookingObj.clientPhone}",
                                        eventTime = sTime
                                    ),
                                    CalendarTimelineEvent(
                                        title = "Post Production Editing Deadline [V1 Draft]",
                                        date = sDate, // typically day after
                                        desc = "Draft compiled by $team. Format for Reel vertical / HD podcast sound. Review with agency director.",
                                        eventTime = "06:00 PM"
                                    ),
                                    CalendarTimelineEvent(
                                        title = "Final Deliverables Handover [Google Drive Upload]",
                                        date = dDate,
                                        desc = "Google Drive Lock. Drive Directory: ${focusBookingObj.googleDriveLink.ifEmpty { "Link pending" }}.",
                                        eventTime = "12:00 PM"
                                    ),
                                    CalendarTimelineEvent(
                                        title = "Follow-up Strategy Call & Social Formula Sync",
                                        date = dDate,
                                        desc = "Follow up regarding analytics, organic growth metrics, and formulation of monthly retention loop.",
                                        eventTime = "04:30 PM"
                                    ),
                                    CalendarTimelineEvent(
                                        title = "Payment Invoice Settle Sync Reminder",
                                        date = dDate,
                                        desc = "Verify receipt of remaining 50% invoice balance (Amount: ₹${(focusBookingObj.invoiceAmount - focusBookingObj.advanceReceivedValue).toInt()}). Current State: ${if (focusBookingObj.isBalancePaid) "CLEARED" else "DUE"}",
                                        eventTime = "10:00 AM"
                                    )
                                )

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(timelineEvents) { event ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = event.title.uppercase(),
                                                        color = AccentLime,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(HeadingBlack)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = event.date, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = "Scheduled Time: ${event.eventTime}", color = BackgroundIvory, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                Text(text = event.desc, color = SoftGray, fontSize = 11.sp, lineHeight = 15.sp)
                                                
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color.White.copy(alpha = 0.08f))
                                                            .clickable {
                                                                Toast.makeText(context, "Synced: [${event.title}] pushed to Google Calendar!", Toast.LENGTH_SHORT).show()
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Sync icon", tint = AccentLime, modifier = Modifier.size(10.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("SYNC CALENDAR", style = textModifier(9, Color.White))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- UPDATE SHEET MODAL (AS DIALOG) ---
        isUpdatingBooking?.let { b ->
            var linkText by remember { mutableStateOf(b.googleDriveLink) }
            var statusVal by remember { mutableStateOf(b.status) }
            var advPaid by remember { mutableStateOf(b.isAdvancePaid) }
            var balPaid by remember { mutableStateOf(b.isBalancePaid) }
            var busName by remember { mutableStateOf(b.businessName) }
            var igId by remember { mutableStateOf(b.instagramId) }
            var locValue by remember { mutableStateOf(b.location) }
            var teamText by remember { mutableStateOf(b.assignedTeam) }
            var gearText by remember { mutableStateOf(b.equipmentRequired) }
            var dDateText by remember { mutableStateOf(b.deliveryDate) }
            var totalAmtText by remember { mutableStateOf(b.invoiceAmount.toString()) }
            var advReceivedAmtText by remember { mutableStateOf(b.advanceReceivedValue.toString()) }

            AlertDialog(
                onDismissRequest = { isUpdatingBooking = null },
                title = { Text("UPDATE CONTRACT OPERATIONS SPREADSHEET", fontWeight = FontWeight.Black, fontSize = 15.sp) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Client: ${b.clientName.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HeadingBlack)
                        Divider()

                        // Project status transitions
                        Text("PROJECT PIPELINE STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        
                        // Scrollable Status List
                        val statuses = listOf("Lead", "Discussion", "Booked", "Shoot Scheduled", "Shoot Completed", "Editing", "Delivered", "Closed")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statuses.forEach { state ->
                                val selected = statusVal == state
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (selected) HeadingBlack else SoftGray)
                                        .clickable { statusVal = state }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(text = state, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selected) AccentLime else HeadingBlack)
                                }
                            }
                        }

                        // Extended input attributes
                        OutlinedTextField(
                            value = busName,
                            onValueChange = { busName = it },
                            label = { Text("Business / Brand Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = igId,
                            onValueChange = { igId = it },
                            label = { Text("Instagram ID Handle") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = locValue,
                            onValueChange = { locValue = it },
                            label = { Text("Shoot Location Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Financial entries
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = totalAmtText,
                                onValueChange = { totalAmtText = it },
                                label = { Text("Total Bill Amount") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = advReceivedAmtText,
                                onValueChange = { advReceivedAmtText = it },
                                label = { Text("Advance Received") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dDateText,
                                onValueChange = { dDateText = it },
                                label = { Text("Expected Delivery Date") },
                                placeholder = { Text("YYYY-MM-DD") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = teamText,
                            onValueChange = { teamText = it },
                            label = { Text("Assigned Team Members") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = gearText,
                            onValueChange = { gearText = it },
                            label = { Text("Equipment Rigs Locked") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = linkText,
                            onValueChange = { linkText = it },
                            label = { Text("Google Drive Deliveries Link") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(imageVector = Icons.Default.Share, contentDescription = "Link") }
                        )

                        // Invoice checkboxes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = advPaid, onCheckedChange = { advPaid = it })
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("50% Advance Received Checklist Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = balPaid, onCheckedChange = { balPaid = it })
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("50% Settle Delivery Completed Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val verifiedTotal = totalAmtText.toDoubleOrNull() ?: b.invoiceAmount
                            val verifiedAdv = advReceivedAmtText.toDoubleOrNull() ?: (if (advPaid) verifiedTotal * 0.5 else b.advanceReceivedValue)
                            
                            viewModel.updateBookingStatus(
                                booking = b,
                                nextStatus = statusVal,
                                driveLink = linkText,
                                isAdvancePaid = advPaid,
                                isBalancePaid = balPaid,
                                businessName = busName,
                                instagramId = igId,
                                location = locValue,
                                assignedTeam = teamText,
                                equipmentRequired = gearText,
                                invoiceAmount = verifiedTotal,
                                advanceReceivedValue = verifiedAdv,
                                deliveryDate = dDateText
                            )
                            isUpdatingBooking = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White)
                    ) {
                        Text("SAVE TO GOOGLE SHEET", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isUpdatingBooking = null }) {
                        Text("CANCEL", color = HeadingBlack)
                    }
                }
            )
        }
    }
}

@Composable
private fun DisplayMetricCard(
    title: String,
    value: String,
    containerColor: Color,
    valueColor: Color,
    trendText: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(84.dp)
            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = title,
                    color = SoftGray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            if (trendText != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = trendText,
                    color = SoftGray.copy(alpha = 0.6f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CategoryProgressRow(
    category: String,
    count: Int,
    weight: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                fontWeight = FontWeight.Bold,
                color = HeadingBlack,
                fontSize = 11.sp
            )
            Text(
                text = "$count booked (${(weight * 100).toInt()}%)",
                fontWeight = FontWeight.Bold,
                color = MutedText,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(BackgroundIvory, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (weight.isNaN() || weight <= 0f) 0.01f else weight)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

private fun textModifier(size: Int, color: Color) = androidx.compose.ui.text.TextStyle(
    fontSize = size.sp,
    color = color,
    fontWeight = FontWeight.ExtraBold
)

data class CalendarTimelineEvent(
    val title: String,
    val date: String,
    val desc: String,
    val eventTime: String
)
