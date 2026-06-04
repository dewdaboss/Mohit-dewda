package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: ProductionViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("CONSULT") } // "CONSULT" vs "STRATEGY"
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    var rawInputText by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Suggest 5 Reels ideas for an Indore cafe",
        "Generate a 30-day fitness creator content plan",
        "Draft a premium caption for a BMW car commercial",
        "Explain Creative Team's Starter package specs"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI PARTNER & GROWTH DESK",
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
        ) {
            // --- Custom Tab Bar switcher ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(SoftGray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { activeTab = "CONSULT" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "CONSULT") HeadingBlack else Color.Transparent,
                        contentColor = if (activeTab == "CONSULT") AccentLime else HeadingBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_consult_ai")
                ) {
                    Text("CONSULT DESK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { activeTab = "STRATEGY" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "STRATEGY") HeadingBlack else Color.Transparent,
                        contentColor = if (activeTab == "STRATEGY") AccentLime else HeadingBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_strategy_ai")
                ) {
                    Text("STRATEGY BOARD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            if (activeTab == "CONSULT") {
                // CONSULT TAB (AI Chat Desk)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    // Quick-action prompt pills
                    Text(
                        text = "QUICK CREATOR INQUIRIES",
                        color = MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPrompts.forEach { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardCharcoal)
                                    .clickable {
                                        viewModel.sendChatMessage(prompt)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("quick_prompt_pill_$prompt")
                            ) {
                                Text(
                                    text = prompt,
                                    color = AccentLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Chat messages list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { (text, isUser) ->
                            val alignment = if (isUser) Alignment.End else Alignment.Start
                            val containerColor = if (isUser) HeadingBlack else DarkPremium
                            val textColor = if (isUser) AccentLime else BackgroundIvory

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = alignment
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.85f),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = containerColor),
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isUser) 12.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 12.dp
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = if (isUser) "CLIENT" else "CREATIVE PARTNER",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = if (isUser) Color.LightGray else AccentLime,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = text,
                                                color = textColor,
                                                fontSize = 13.sp,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = HeadingBlack, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating campaign strategy...", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Bottom message text input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = { rawInputText = it },
                            placeholder = { Text("Ask about scripts, reels, plans...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        IconButton(
                            onClick = {
                                if (rawInputText.isNotEmpty()) {
                                    viewModel.sendChatMessage(rawInputText)
                                    rawInputText = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(HeadingBlack)
                                .size(48.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Consultation request",
                                tint = AccentLime,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // STRATEGY BOARD TAB (SMM Module)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Instagram algorithm strategy cards
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkPremium),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "INSTAGRAM SCALE PLAYBOOK",
                                style = MaterialTheme.typography.labelLarge,
                                color = AccentLime,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            listOf(
                                "01. Transition Hooks" to "Begin reels with swift visual offsets (1.2 seconds) to hook the scroll.",
                                "02. Micro-Niche Authority" to "Produce educational commentary for elite Cafes & Gyms in Indore.",
                                "03. Audio Synchronization" to "Sync multi-cam slow motion transitions directly to fast-trending beats."
                            ).forEach { (stepName, detail) ->
                                Text(text = stepName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = detail, color = SoftGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                            }
                        }
                    }

                    // Trending local Indore tags selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "LOCAL OUTREACH SEO HASHTAGS",
                                style = MaterialTheme.typography.labelLarge,
                                color = AccentLime
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "#indorephotographer #indorediaries #indorebistro #indoreinfluencers #indorefitness #creativeteampending #mohitdewdaproductions #indorestartup",
                                color = BackgroundIvory,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Content monthly calendar board
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SoftGray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "CREATIVE CONTENT SCHEDULE",
                                style = MaterialTheme.typography.labelLarge,
                                color = HeadingBlack,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            listOf(
                                "WEEK 1: Brand Authenticity" to "Shoot founder story / Behind-The-Scenes setups.",
                                "WEEK 2: High-Dynamics Commercials" to "Showcase macro product captures & cinematic vehicle sweeps.",
                                "WEEK 3: Educational Podcasting" to "Record local Indore influencer insights on high end multi mikes.",
                                "WEEK 4: Growth Conversions" to "Post client outcome testimonials with a CTA to book in-app!"
                            ).forEach { (weekName, conceptPlan) ->
                                Text(text = weekName, color = HeadingBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = conceptPlan, color = HeadingBlack.copy(alpha = 0.7f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
