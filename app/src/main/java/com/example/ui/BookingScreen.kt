package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: ProductionViewModel,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step by viewModel.bookingStep.collectAsState()
    val activeService by viewModel.bookingService.collectAsState()
    
    val services = listOf(
        "🎬 Reel Shoots",
        "📸 Photography",
        "🎙 Podcasts",
        "🎥 YouTube Production",
        "📦 Product Shoots",
        "🎞 Animation Videos",
        "📢 Social Media Marketing"
    )
    
    val packages = when (activeService) {
        "🎬 Reel Shoots" -> listOf(
            "Basic Reel Package" to "₹1,500 — Up to 60 Sec Reel | 1 Location | Professional Edit | 24 Hours Delivery",
            "Standard Reel Package" to "₹2,000 — Creative Shots | Smooth Transitions | Professional Editing | 24 Hours Delivery",
            "Premium Reel Package" to "₹2,500 — Cinematic Storytelling | Advanced Editing | Premium Visual Look | 24 Hours Delivery",
            "Basic Multi-Angle Package" to "₹2,500 — Up to 1 Hour Shoot | Multi-Angle Coverage | Professional Editing",
            "Premium Multi-Angle Package" to "₹3,500 — Up to 2 Hours Shoot | Cinematic Transitions | Color Grading | 2 Reels Included",
            "Commercial Reel Package" to "₹5,000 — Up to 3 Hours Shoot | Cinematic Storytelling | Premium Editing | 3 Reels Included"
        )
        "📸 Photography" -> listOf(
            "Budget Shoot (Per Photo)" to "₹25/Photo — Shoot Only | Professional Lighting (Minimum 15 Photos)",
            "Prime Edit Shoot (Per Photo)" to "₹50/Photo — Shoot + Premium Editorial Retouching (Minimum 15 Photos)",
            "Hourly Content Shoot" to "₹1,199/Hour — Unlimited Photos & Videos | Multi-Angle | Google Drive Delivery"
        )
        "🎙 Podcasts" -> listOf(
            "Standard Podcast Shoot" to "₹5,000 — 1–3 Hour Shoot | Full Podcast Editing | 1 Reel & Thumbnail Included | 2–3 Days Delivery",
            "Podcast Creator Package" to "₹7,000 — 1–3 Hour Shoot | Multi-Angle Coverage | Full Podcast Editing | 2 Reels & Thumbnail Included"
        )
        "🎥 YouTube Production" -> listOf(
            "YouTube Starter Package" to "₹5,000 — Up to 1 Hour Shoot | Full Video Editing | Thumbnail & 1 Shorts Clip Included",
            "Podcast Creator Package" to "₹7,000 — 1–3 Hour Shoot | Multi-Angle | Full Podcast Editing | 2 Reels & Thumbnail Included",
            "Business & Brand Package" to "₹9,000 — Product or Brand Shoot | Marketing Content Creation | YouTube Editing | Thumbnail & 2 Reels Included",
            "Premium YouTube Growth Package" to "₹15,000+ — Channel Analysis | Market Research | Shoot + Edit + Thumbnail | Shorts Strategy | Branding Support | Growth Consultation"
        )
        "📦 Product Shoots" -> listOf(
            "Hourly Product Session" to "₹1,199/Hour — Unlimited Photos | Multi-angle Coverage | Google Drive Delivery",
            "Basic Product Photo" to "₹29/Photo — Basic Product Photography & Clean Framing",
            "Edited Product Photo" to "₹50/Photo — Shoot + Specialized High-End E-Commerce Editing"
        )
        "🎞 Animation Videos" -> listOf(
            "15 Sec Custom Animation" to "₹2,500 — 15 Second Custom Brand Animated Video / Story Outline"
        )
        "📢 Social Media Marketing" -> listOf(
            "Monthly Reel Package" to "₹15,000 — 2 Shoot Days | 10 Reels | Content Planning | Brand Guidance | Replace 2 Reels with 1 YouTube Video optional",
            "Growth Retainer" to "₹20,000 — 3 Shoot Days | 14 Reels | Content Planning | Strategic Brand Growth Engine",
            "Premium Brand Retainer" to "₹25,000 — 4 Shoot Days | 18 Reels | Market Analysis | Bespoke Content Strategy | Branding Support"
        )
        else -> listOf(
            "Custom Growth Retainer" to "Discuss tailored specifications suited to your budget"
        )
    }
    
    val dates = listOf("2026-06-10", "2026-06-11", "2026-06-12", "2026-06-13", "2026-06-14")
    val times = listOf("10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM", "6:00 PM")

    scaffoldLogic(viewModel, step, services, packages, dates, times, onNavigateToDashboard, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scaffoldLogic(
    viewModel: ProductionViewModel,
    step: Int,
    services: List<String>,
    packages: List<Pair<String, String>>,
    dates: List<String>,
    times: List<String>,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val integrationText by viewModel.integrationStatus.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "STUDIO RESERVATIONS",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Custom Progress Indicator ---
            if (step <= 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..4) {
                        val active = i <= step
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (active) AccentLime else SoftGray)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }
                Text(
                    text = "STEP $step OF 4: " + when (step) {
                        1 -> "SELECT SESSION TYPE"
                        2 -> "CHOOSE PRODUCTION PACKAGE"
                        3 -> "SELECT TIMING"
                        else -> "CLIENT REQUISITIONS"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MutedText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- Step Pages ---
            when (step) {
                1 -> {
                    // Step 1: Select Service
                    val activeService by viewModel.bookingService.collectAsState()
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        services.forEach { service ->
                            val isSelected = activeService == service
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) HeadingBlack else DarkPremium
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.bookingService.value = service }
                                    .testTag("service_card_$service")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = service,
                                        color = if (isSelected) AccentLime else BackgroundIvory,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.bookingService.value = service },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = AccentLime,
                                            unselectedColor = SoftGray
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Step 2: Choose Package
                    val activePackage by viewModel.bookingPackage.collectAsState()
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        packages.forEach { (pack, priceAndDesc) ->
                            val isSelected = activePackage == pack
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) HeadingBlack else DarkPremium
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.bookingPackage.value = pack }
                                    .testTag("package_card_$pack")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pack.uppercase(),
                                            color = if (isSelected) AccentLime else BackgroundIvory,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            letterSpacing = 1.sp
                                        )
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.bookingPackage.value = pack },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = AccentLime,
                                                unselectedColor = SoftGray
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = priceAndDesc,
                                        color = SoftGray,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Step 3: Date & Time
                    val activeDate by viewModel.bookingDate.collectAsState()
                    val activeTime by viewModel.bookingTime.collectAsState()
                    Column {
                        Text(
                            text = "AVAILABLE DATES (INDORE DESK LOCKS)",
                            fontWeight = FontWeight.Bold,
                            color = HeadingBlack,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dates.forEach { date ->
                                val isSelected = activeDate == date
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) HeadingBlack else SoftGray)
                                        .clickable { viewModel.bookingDate.value = date }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                        .testTag("date_chip_$date")
                                ) {
                                    Text(
                                        text = date,
                                        color = if (isSelected) AccentLime else HeadingBlack,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "AVAILABLE TIME SLOTS",
                            fontWeight = FontWeight.Bold,
                            color = HeadingBlack,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            times.forEach { t ->
                                val isSelected = activeTime == t
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) HeadingBlack else SoftGray)
                                        .clickable { viewModel.bookingTime.value = t }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                        .testTag("time_chip_$t")
                                ) {
                                    Text(
                                        text = t,
                                        color = if (isSelected) AccentLime else HeadingBlack,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Step 4: Details
                    var name by remember { mutableStateOf(viewModel.clientName.value) }
                    var phone by remember { mutableStateOf(viewModel.clientPhone.value) }
                    var email by remember { mutableStateOf(viewModel.clientEmail.value) }
                    var businessName by remember { mutableStateOf(viewModel.clientBusinessName.value) }
                    var instagramId by remember { mutableStateOf(viewModel.clientInstagramId.value) }
                    var locationText by remember { mutableStateOf(viewModel.clientLocation.value) }
                    var requirements by remember { mutableStateOf(viewModel.clientRequirements.value) }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                viewModel.clientName.value = it
                            },
                            label = { Text("Client Full Name *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_name")
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                viewModel.clientPhone.value = it
                            },
                            label = { Text("WhatsApp Contact Number *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_phone")
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                viewModel.clientEmail.value = it
                            },
                            label = { Text("Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_email")
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = businessName,
                                onValueChange = {
                                    businessName = it
                                    viewModel.clientBusinessName.value = it
                                },
                                label = { Text("Brand / Business Name") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("field_business_name")
                            )
                            OutlinedTextField(
                                value = instagramId,
                                onValueChange = {
                                    instagramId = it
                                    viewModel.clientInstagramId.value = it
                                },
                                label = { Text("Instagram ID Handle") },
                                placeholder = { Text("@username") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("field_instagram_id")
                            )
                        }
                        OutlinedTextField(
                            value = locationText,
                            onValueChange = {
                                locationText = it
                                viewModel.clientLocation.value = it
                            },
                            label = { Text("Shoot Location Address in Indore") },
                            placeholder = { Text("e.g. Cafe 10, Vijay Nagar, Indore") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_location")
                        )
                        OutlinedTextField(
                            value = requirements,
                            onValueChange = {
                                requirements = it
                                viewModel.clientRequirements.value = it
                            },
                            label = { Text("Production Requirements / Vision / Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("field_requirements")
                        )
                    }
                }
                else -> {
                    // Step 5: Completed Ticket Screen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = AccentLime,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "RESERVATION COMMITTED",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = 1.sp,
                            color = HeadingBlack,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Mohit Dewda's production desk has received your request. To confirm this date-lock, please execute your 50% contract advance.",
                            color = MutedText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trigger visual showing actual Google Calendar / Sheets integrations
                        integrationText?.let { logs ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("integration_success_panel")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = logs,
                                        color = AccentLime,
                                        fontSize = 12.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { viewModel.clearIntegrationStatus() }) {
                                        Text("ACKNOWLEDGE SPREADSHEEET INTEGRATIONS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                val url = "https://wa.me/919755550380?text=Hi%20Mohit,%20I%20have%20submitted%20my%20Reserve%20request%20in%20the%20app%20and%20want%20to%20confirm%20my%20date%20lock%20advance!"
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply { data = android.net.Uri.parse(url) }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_whatsapp_confirm_order")
                        ) {
                            Text("CONFIRM DATE-LOCK VIA WHATSAPP", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onNavigateToDashboard,
                            colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_go_to_dashboard")
                        ) {
                            Text("PROCEED TO MY DASHBOARD", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Steering Navigation Actions ---
            if (step <= 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { viewModel.setBookingStep(step - 1) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("btn_prev_step")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Before")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BACK", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    if (step < 4) {
                        Button(
                            onClick = { viewModel.setBookingStep(step + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("btn_next_step")
                        ) {
                            Text("CONTINUE", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (viewModel.clientName.value.isNotEmpty() && viewModel.clientPhone.value.isNotEmpty()) {
                                    viewModel.submitBookingFlow()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = HeadingBlack),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("btn_submit_booking")
                        ) {
                            Text("SUBMIT BOOKING", fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done")
                        }
                    }
                }
            }
        }
    }
}
