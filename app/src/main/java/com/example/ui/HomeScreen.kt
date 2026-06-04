package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.animation.core.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star

@Composable
fun HomeScreen(
    viewModel: ProductionViewModel,
    onNavigateToBooking: () -> Unit,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedSectionTab by remember { mutableStateOf(0) }
    val sectionTabs = listOf("ATTENTION DECK", "HIGH-END PLANS", "CONVERSION LAB", "OPERATIONS")

    // Infinite animation transition for the high-end border hover-glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "btn_border_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    val glowThicknessFraction by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_thickness_fraction"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkPremium)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- Studio Location Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Indore Headquarters",
                    tint = AccentLime,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "INDORE, MP",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = AccentLime),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "GROWTH AGENCY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPremium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // --- Agency Identity Billboard ---
        Text(
            text = "CREATIVE TEAM",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 44.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.sp
            ),
            color = Color.White,
        )
        Text(
            text = "PRODUCTION",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 44.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            ),
            color = AccentLime,
            modifier = Modifier.offset(y = (-4).dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Floating Cinematic Particles Panel ---
        FloatingParticlesContainer(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        // --- Grand Tagline Hero Section ---
        Surface(
            color = CardCharcoal,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("hero_section")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "THE POSITIONING",
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentLime,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("“We Don't Just ")
                        withStyle(
                            style = SpanStyle(
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Serif,
                                color = AccentLime
                            )
                        ) {
                            append("Capture")
                        }
                        append(" Moments.\nWe Create ")
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Stories")
                        }
                        append(" That Last.”")
                    },
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 32.sp
                    ),
                    color = Color.White,
                    lineHeight = 38.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "We are NOT a local photography studio. We are a Content Creation Agency, Branding Agency, Marketing Partner, and Growth-focused Production House.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftGray,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "We don't sell cameras or shoots. We sell brand attention, visibility, leads, customer acquisition, and actual sales results.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftGray.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGING PARTNER: MOHIT DEWDA",
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "EST. 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        // --- Outstanding Prominent 'Book a Shoot' CTA Block ---
        Button(
            onClick = onNavigateToBooking,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentLime,
                contentColor = DarkPremium
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(64.dp)
                .border(
                    width = glowThicknessFraction.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AccentLime,
                            Color.White.copy(alpha = glowAlpha),
                            AccentLime.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .testTag("btn_book_shoot")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "BOOK A COMMERCIAL SHOOT",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward to book",
                    tint = DarkPremium,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // --- Core Action Buttons Underneath ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigateToPortfolio,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardCharcoal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_view_portfolio")
            ) {
                Text(
                    text = "VIEW CASE STUDIES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Button(
                onClick = onNavigateToAi,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = AccentLime
                ),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_ai_co_creator")
            ) {
                Text(
                    text = "AI CO-CREATOR STUDIO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SUB-NAVIGATION TAB SYSTEM (PREMIUM SALES PRESENTATION / WEBSITE DECK) ---
        Text(
            text = "INTERACTIVE SALES PRESENTATION & SERVICES CATALOG",
            style = MaterialTheme.typography.labelSmall,
            color = SoftGray.copy(alpha = 0.6f),
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sectionTabs.forEachIndexed { index, title ->
                val active = selectedSectionTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(if (active) AccentLime else CardCharcoal)
                        .clickable { selectedSectionTab = index }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("tab_button_$index")
                ) {
                    Text(
                        text = title,
                        color = if (active) DarkPremium else SoftGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // --- TAB BODY RENDERERS ---
        when (selectedSectionTab) {
            0 -> {
                // --- Tab 0: ATTENTION DECK (Positioning Pitch / Mindshift Slide) ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkPremium),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AccentLime, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ATTENTION IS THE CURRENCY",
                                color = AccentLime,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Traditional Camera crews shoot frames. We capture attention.",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "A beautiful aesthetic video achieves NOTHING if nobody watches it. Creative Team Production strategically wires every reel, commercial, and photo-set for high cognitive retention, algorithmic discovery, and conversions.",
                            color = SoftGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "HOW WE SCALE BRANDS vs PHOTO STUDIOS:",
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Compare Grid Row 1
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "❌ LOCAL PHOTOGRAPHY", color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = "Focus: Raw settings and megapixels\nResult: Ghost towns, zero engagement.", color = SoftGray, fontSize = 11.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "✓ CREATIVE TEAM AGENCY", color = AccentLime, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = "Focus: Hook structures & consumer actions\nResult: Consistent customer acquisition.", color = SoftGray, fontSize = 11.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "OUR MARKETING FRAMEWORKS:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        listOf(
                            "Brand Growth & Attention Acquisition" to "We construct high-tempo transitions and visual indicators to retain scrolling users within the critical first 3 seconds.",
                            "Audience Reach & Algorithmic Hooking" to "We write and film with proven social media hook frameworks that naturally prompt the algorithms to reward your brand in Indore and beyond.",
                            "Conversion Tracking & Customer Action" to "We construct active, natural, and highly urgent calls-to-action that send users from the Reels directly to your DMs, inquiries, and checkout desks."
                        ).forEach { (title, description) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = AccentLime, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = title.uppercase(), color = AccentLime, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = description, color = SoftGray, fontSize = 12.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // --- Tab 1: HIGH-END PLANS & SERVICES CATALOG (Bespoke Brochure) ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "COMPREHENSIVE PRICING BROCHURE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Choose result-defined packages mapped to your business scales. Instant lock-ins route your chosen tier directly into the Reservation Portal.",
                        color = MutedText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    // Card Group 1: Reels Packages
                    BrochureSectionCard(
                        title = "🎬 STRATEGIC REELS & BRAND VIDEOS",
                        intro = "Crafted for high organic reach, extreme visual retention, and brand narrative acceleration in Indore."
                    ) {
                        listOf(
                            Triple("Basic Reel Package", "₹1,500", "Up to 60 Sec Reel | 1 Location | Professional Edit | 24 Hours Turnaround"),
                            Triple("Standard Reel Package", "₹2,000", "Creative Shots | Smooth High-Conversion Transitions | Professional Color Editorial Editing | 24 Hours Turnaround"),
                            Triple("Premium Reel Package", "₹2,500", "High-Cinematic Storytelling | Advanced Custom Sound Design & Transitions | Premium Visual Brand Look | 24 Hours Turnaround"),
                            Triple("Basic Multi-Angle Package", "₹2,500", "Up to 1 Hour Shoot | Multi-Angle Camera setups | Professional Editing"),
                            Triple("Premium Multi-Angle Package", "₹3,500", "Up to 2 Hours Shoot | High Cinematic Transitions | Color Grading | Includes 2 Complete Reels"),
                            Triple("Commercial Reel Package", "₹5,000", "Up to 3 Hours Shoot | Premium brand Cinematic Storytelling | Full High-End Editing & Grading | Includes 3 Reels")
                        ).forEach { (name, price, specs) ->
                            PlanCatalogItem(
                                name = name,
                                price = price,
                                specs = specs,
                                onBookNow = {
                                    viewModel.bookingService.value = "🎬 Reel Shoots"
                                    viewModel.bookingPackage.value = "$name ($price)"
                                    viewModel.bookingStep.value = 3
                                    onNavigateToBooking()
                                    Toast.makeText(context, "Selected $name. Select date & time to lock!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // Card Group 2: YouTube Production
                    BrochureSectionCard(
                        title = "🎥 YOUTUBE AUTOPILOT CHANNEL PLANS",
                        intro = "Full operational YouTube workflows. We script, manage, edit, and optimize, so you just speak."
                    ) {
                        listOf(
                            Triple("YouTube Starter Package", "₹5,000", "Up to 1 Hour Shoot | Full Video Editing | Thumbnail Included | 1 Shorts Clip Extracted & Included"),
                            Triple("Podcast Creator Package", "₹7,000", "1–3 Hour Shoot | Multi-Angle Coverage | Full Podcast Acoustic Editing | 2 Reels Included | Custom Thumbnail"),
                            Triple("Business & Brand Package", "₹9,000", "Product or Brand Setup | Marketing Content Scripting | full YouTube Master Editing | Custom Thumbnail | 2 Reels Included"),
                            Triple("Premium YouTube Growth Package", "₹15,000+", "Competitor Channel Analysis | Indore Market Research | Complete Shoot + Edit + Thumbnail | Viral Shorts Content Strategy | Branding Support | Growth Consultations")
                        ).forEach { (name, price, specs) ->
                            PlanCatalogItem(
                                name = name,
                                price = price,
                                specs = specs,
                                onBookNow = {
                                    viewModel.bookingService.value = "🎥 YouTube Production"
                                    viewModel.bookingPackage.value = "$name ($price)"
                                    viewModel.bookingStep.value = 3
                                    onNavigateToBooking()
                                    Toast.makeText(context, "Selected YouTube $name. Route complete!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // Card Group 3: Podcasts
                    BrochureSectionCard(
                        title = "🎙 PROFESSIONAL PODCAST PRODUCTION",
                        intro = "Turn ideas into elite acoustic content. Multi-cameras, studio grade audio, and high conversion cut-downs."
                    ) {
                        PlanCatalogItem(
                            name = "Podcast Studio Shoot",
                            price = "₹5,000",
                            specs = "1–3 Hour Shoot | Full Podcast Multi-track Editing | 1 Custom Reel Included for Promotion | Custom CTR-optimized Thumbnail Included | 2–3 Days Delivery",
                            onBookNow = {
                                viewModel.bookingService.value = "🎙 Podcasts"
                                viewModel.bookingPackage.value = "Podcast Studio Shoot (₹5,000)"
                                viewModel.bookingStep.value = 3
                                onNavigateToBooking()
                                Toast.makeText(context, "Podcast shoot selected!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Card Group 4: Photography & Products
                    BrochureSectionCard(
                        title = "📸 BRAND PHOTOGRAPHY & PRODUCT SHOOTS",
                        intro = "Clean e-commerce and personal branding photography that positions you as the premium choice."
                    ) {
                        listOf(
                            Triple("Budget Brand Shoot", "₹25/Photo", "Shoot only, minimum 15 photos. High settings camera lighting configuration. Suitable for fashion/products."),
                            Triple("Prime Edited Photo Shoot", "₹50/Photo", "Professional high-end capture + professional premium editorial editing. Minimum 15 Photos."),
                            Triple("Hourly Content Shoot", "₹1,199/Hour", "Ideal for ongoing monthly content. Unlimited Photos & Videos | Multi-Angle configurations | Google Drive folder delivery"),
                            Triple("Basic Product Photo", "₹29/Photo", "Ideal for bulk catalogues. Basic product framing & clean edits."),
                            Triple("15s Custom Animation Video", "₹2,500", "Professional 15 Second high-end Custom Graphic animated brand teaser structure.")
                        ).forEach { (name, price, specs) ->
                            PlanCatalogItem(
                                name = name,
                                price = price,
                                specs = specs,
                                onBookNow = {
                                    val isProduct = name.contains("Product") || name.contains("Animation")
                                    viewModel.bookingService.value = if (isProduct) "📦 Product Shoots" else "📸 Photography"
                                    viewModel.bookingPackage.value = "$name ($price)"
                                    viewModel.bookingStep.value = 3
                                    onNavigateToBooking()
                                    Toast.makeText(context, "Selected photography tier!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // Card Group 5: Monthly Plans
                    BrochureSectionCard(
                        title = "📢 MONTHLY CONTENT PLANS & RETAINERS",
                        intro = "Elite brand autopilot retentions. Ongoing strategic content pipelines keeping your sales funnels forever flowing."
                    ) {
                        listOf(
                            Triple("Monthly Reel Package", "₹15,000", "2 Dedicated Shoot Days | 10 Strategic Reels | Comprehensive Content Planning | Indore Brand Guidance. Optional: Replace 2 Reels with 1 Full YouTube Video."),
                            Triple("Growth retainer Package", "₹20,000", "3 Dedicated Shoot Days | 14 Reels | Content Planning | Indore Regional Competitor Strategy. Optional: Replace 3 Reels with 1 Full YouTube Video."),
                            Triple("Premium Brand Autopilot Plan", "₹25,000", "4 Dedicated Shoot Days | 18 Reels | Deeper Competitor & Market Analysis | Bespoke Creative Content Strategy | 100% Brand Growth Onboarding. Optional: Replace 4 Reels with 1 Full YouTube Video.")
                        ).forEach { (name, price, specs) ->
                            PlanCatalogItem(
                                name = name,
                                price = price,
                                specs = specs,
                                onBookNow = {
                                    viewModel.bookingService.value = "📢 Social Media Marketing"
                                    viewModel.bookingPackage.value = "$name ($price)"
                                    viewModel.bookingStep.value = 3
                                    onNavigateToBooking()
                                    Toast.makeText(context, "Premium growth Retainer selected!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            2 -> {
                // --- Tab 2: CONVERSION LAB / BLUEPRINTS (Strategic Value / Social Presentation) ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkPremium),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "CONVERSION FRAMEWORKS VALUED AT ₹50k",
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "VIRAL HOOKS THAT STOP THE SCROLL:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        listOf(
                            "“The Indore Secret...”" to "Why [Category] businesses in Indore are losing customers to [Competitor Option] and how to fix it in 24 hours.",
                            "“Stop doing this...”" to "If you are still shooting your products like this, you are throwing away at least 80% of your warm online leads.",
                            "“I spent ₹10k so...”" to "We tested five different content angles in Indore, so you do not have to. Here is the exact template that generated 50+ inbound sales."
                        ).forEach { (hook, copy) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = hook, color = AccentLime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = copy, color = SoftGray, fontSize = 12.sp, lineHeight = 16.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "HIGH-CONVERTING CAPTION STRUCTURE:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("1. ")
                                withStyle(SpanStyle(color = AccentLime, fontWeight = FontWeight.Bold)) { append("SCROLL-STOPPING STATEMENT: ") }
                                append("Summarize the ultimate positive outcome in 1 sentence.\n\n")
                                append("2. ")
                                withStyle(SpanStyle(color = AccentLime, fontWeight = FontWeight.Bold)) { append("BULLET OF REAL VALUE: ") }
                                append("Provide 3 actionable, quick-to-digest reasons or secrets.\n\n")
                                append("3. ")
                                withStyle(SpanStyle(color = AccentLime, fontWeight = FontWeight.Bold)) { append("DM ME TRIGGERS (CTA): ") }
                                append("“Do not tell people to follow. Tell them: Comment [BRAND] below and our auto-responder will DM you our WhatsApp onboarding line!”")
                            },
                            color = SoftGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            3 -> {
                // --- Tab 3: OPERATIONS & TRAVEL DECK ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "TERMS OF OPERATION",
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "INDORE TRAVEL & CONTRACT POLICIES",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "✓ Indore Wide Coverage: We fully service all districts of Indore, MP.",
                            color = SoftGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Travel Policy: First 10 Kilometers of travel from our Indore headquarters is 100% FREE. Any additional distance beyond 10 KM incurs nominal transit travel charges discussed beforehand.",
                            color = SoftGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = SolidWhite.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "CASHFLOW CONDITIONS & DATE-LOCKS:",
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• 50% Contract Advance is required immediately to lock down calendar dates. Booking slot is open to others until advance clearance.\n• 50% Post-Production Balance is cleared immediately upon edit approval, prior to final high-resolution Google Drive folder releases.",
                            color = SoftGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "CONTACT DETAILS:",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "📞 Phone: +91 9755550380", color = SoftGray, fontSize = 12.sp)
                        Text(text = "✉ Email: creative.team.production.official@gmail.com", color = SoftGray, fontSize = 12.sp)
                        Text(text = "📍 Base: Indore, Madhya Pradesh", color = SoftGray, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3D INTERACTIVE CAROUSEL ---
        Text(
            text = "3D CAMERAS & PRODUCTION GEAR INFRASTRUCTURE",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf(
                "Camera" to "3D Cinematic Rig",
                "Lens" to "Macro Anamorphic",
                "Drone" to "Carbon DJI Quadcopter",
                "StudioLights" to "3D LED Softbox Grid"
            ).forEach { (type, description) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Luxury3DCanvas(type = type, modifier = Modifier.size(90.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (type == "StudioLights") "STUDIO LIGHTS" else type.uppercase(),
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = description,
                            color = SoftGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Core Info & Mission Badge ---
        Card(
            colors = CardDefaults.cardColors(containerColor = CardCharcoal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Core info",
                    tint = AccentLime,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "OUR REVENUE OBJECTIVE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = "To engineer highly optimized visual systems that capture attention, build trust, and drive continuous sales volume.",
                        fontSize = 13.sp,
                        color = SoftGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Testimonials (Trust) & Payment Guidelines ---
        ClientTrustSection()
        PaymentPolicySection()

        // --- SEO Indore Directory Section ---
        Surface(
            color = HeadingBlack,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "INDORE COMMERCIAL PARTNER DIRECTORY",
                    color = AccentLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Indore Regional Branding • Indore Digital Marketing • Full-Suite Content Creation Agency Indore • YouTube Channel Management Indore • Corporate Film Indore • Commercial Reels Indore",
                    color = BackgroundIvory.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Leave space for float WhatsApp widget and navigation bottom bar
    }
}

// Helper Components for Brochure Renderers
@Composable
fun BrochureSectionCard(
    title: String,
    intro: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkPremium),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = AccentLime,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = intro,
                color = SoftGray,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Divider(color = SolidWhite.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PlanCatalogItem(
    name: String,
    price: String,
    specs: String,
    onBookNow: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = price,
                    color = AccentLime,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = specs,
                color = SoftGray,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onBookNow,
                colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = HeadingBlack),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("BOOK PACKAGE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Forward to lock", modifier = Modifier.size(12.dp))
            }
        }
    }
}
