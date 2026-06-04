package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

sealed class Screen(val title: String) {
    object Home : Screen("Home")
    object Portfolio : Screen("Portfolio")
    object Booking : Screen("Reserve")
    object Dashboard : Screen("Dashboard")
    object AiAssistant : Screen("AI Growth")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: ProductionViewModel,
    modifier: Modifier = Modifier
) {
    var activeScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val currentRole by viewModel.currentRole.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeadingBlack)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CREATIVE TEAM",
                            color = BackgroundIvory,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PRODUCTION",
                            color = AccentLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }

                    // --- Custom Role Switcher: Client Panel vs Founder Desk ---
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardCharcoal)
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (currentRole == "Client") AccentLime else Color.Transparent)
                                .clickable { viewModel.currentRole.value = "Client" }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("role_client_toggle")
                        ) {
                            Text(
                                text = "CLIENT",
                                color = if (currentRole == "Client") HeadingBlack else BackgroundIvory.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (currentRole == "Admin") AccentLime else Color.Transparent)
                                .clickable { viewModel.currentRole.value = "Admin" }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("role_admin_toggle")
                        ) {
                            Text(
                                text = "FOUNDER",
                                color = if (currentRole == "Admin") HeadingBlack else BackgroundIvory.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Divider(color = CardCharcoal.copy(alpha = 0.15f))
            }
        },
        bottomBar = {
            Column {
                Divider(color = SoftGray)
                NavigationBar(
                    containerColor = DarkPremium,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    val items = listOf(
                        Triple(Screen.Home, "HOME", Icons.Default.Home),
                        Triple(Screen.Portfolio, "PORTFOLIO", Icons.Default.Star),
                        Triple(Screen.Booking, "BOOK", Icons.Default.Add),
                        Triple(Screen.Dashboard, "DASH", Icons.Default.Settings),
                        Triple(Screen.AiAssistant, "AI CO-CREATOR", Icons.Default.Person)
                    )

                    items.forEach { (screen, label, icon) ->
                        val selected = activeScreen == screen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { activeScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HeadingBlack,
                                unselectedIconColor = SoftGray,
                                selectedTextColor = AccentLime,
                                unselectedTextColor = SoftGray,
                                indicatorColor = AccentLime
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeScreen) {
                Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToBooking = {
                            viewModel.setBookingStep(1)
                            activeScreen = Screen.Booking
                        },
                        onNavigateToPortfolio = { activeScreen = Screen.Portfolio },
                        onNavigateToAi = { activeScreen = Screen.AiAssistant }
                    )
                }
                Screen.Portfolio -> {
                    PortfolioScreen(viewModel = viewModel)
                }
                Screen.Booking -> {
                    BookingScreen(
                        viewModel = viewModel,
                        onNavigateToDashboard = { activeScreen = Screen.Dashboard }
                    )
                }
                Screen.Dashboard -> {
                    if (currentRole == "Admin") {
                        AdminDashboard(viewModel = viewModel)
                    } else {
                        ClientDashboard(
                            viewModel = viewModel,
                            onNavigateToBooking = {
                                viewModel.setBookingStep(1)
                                activeScreen = Screen.Booking
                            }
                        )
                    }
                }
                Screen.AiAssistant -> {
                    AiAssistantScreen(viewModel = viewModel)
                }
            }

            // Always visible Floating Action WhatsApp widget matching specifications, except on the main home screen
            if (activeScreen != Screen.Home) {
                FloatingWhatsappButton()
            }
        }
    }
}
