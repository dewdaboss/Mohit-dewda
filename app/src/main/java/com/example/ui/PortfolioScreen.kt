package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PortfolioItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: ProductionViewModel,
    modifier: Modifier = Modifier
) {
    val portfolioList by viewModel.portfolioItems.collectAsStateWithLifecycle()
    val categories = listOf("All", "Photography", "Reels", "Cars", "Commercial", "Products", "Podcast", "YouTube", "Brand Content")
    var selectedCategory by remember { mutableStateOf("All") }

    // Dialog state for adding a project
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state for editing an existing project
    var editingItem by remember { mutableStateOf<PortfolioItem?>(null) }

    val filteredList = if (selectedCategory == "All") {
        portfolioList
    } else {
        portfolioList.filter { it.category == selectedCategory }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundIvory),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "STUDIO PORTFOLIO",
                        fontWeight = FontWeight.ExtraBold,
                        color = HeadingBlack,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                },
                actions = {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("portfolio_add_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add portfolio item", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("UPLOAD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
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
            // --- Category Filter Chips Horizontal Scroll ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentLime,
                            selectedLabelColor = HeadingBlack,
                            containerColor = SoftGray,
                            labelColor = HeadingBlack.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
                }
            }

            // --- Custom Info Badge: No Coding Required Auto Updates ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentLime)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE CLOUD REFRESH: Portfolio updates instantly on state-sync. No rebuild required.",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No projects uploaded under this category yet.",
                            color = MutedText,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(onClick = { showAddDialog = true }) {
                            Text("Create First Project Now", color = HeadingBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkPremium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("portfolio_item_${item.id}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Mock Media Asset Space with Accent Line Graphics
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(CardCharcoal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom visual visual for creative project
                                    Luxury3DCanvas(type = "Film", modifier = Modifier.size(90.dp))
                                    
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                            .background(AccentLime, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(
                                            text = item.category.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HeadingBlack,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Mock play button overlay for reels/podcast/commercials
                                    if (item.category.contains("Reel") || item.category.contains("Podcast") || item.category.contains("YouTube") || item.category.contains("Commercial")) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(Color.White.copy(alpha = 0.9f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Trigger video player",
                                                tint = HeadingBlack,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Client: ${item.clientName}",
                                            fontSize = 12.sp,
                                            color = AccentLime,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = item.dateString,
                                            fontSize = 11.sp,
                                            color = SoftGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SoftGray,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = SolidWhite.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Admin Action Controls (Edit and Delete options)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { editingItem = item },
                                            modifier = Modifier.testTag("btn_edit_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit project",
                                                tint = SoftGray
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { viewModel.removePortfolioItem(item.id) },
                                            modifier = Modifier.testTag("btn_delete_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete project",
                                                tint = Color.Red.copy(alpha = 0.8f)
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

        // --- ADD DIALOG FORM ---
        if (showAddDialog) {
            var inputTitle by remember { mutableStateOf("") }
            var inputClient by remember { mutableStateOf("") }
            var inputDesc by remember { mutableStateOf("") }
            var inputCat by remember { mutableStateOf("Photography") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("UPLOAD WORK", fontWeight = FontWeight.Bold, color = HeadingBlack) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = inputTitle,
                            onValueChange = { inputTitle = it },
                            label = { Text("Project Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputClient,
                            onValueChange = { inputClient = it },
                            label = { Text("Client Business Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputDesc,
                            onValueChange = { inputDesc = it },
                            label = { Text("Project Description & Specs") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Selector
                        Text("CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.filter { it != "All" }.forEach { c ->
                                val selected = inputCat == c
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (selected) AccentLime else SoftGray)
                                        .clickable { inputCat = c }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = c,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeadingBlack
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inputTitle.isNotEmpty() && inputClient.isNotEmpty()) {
                                viewModel.uploadTitle.value = inputTitle
                                viewModel.uploadClient.value = inputClient
                                viewModel.uploadDesc.value = inputDesc
                                viewModel.uploadCategory.value = inputCat
                                viewModel.submitPortfolioItem()
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White)
                    ) {
                        Text("SUBMIT", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("CANCEL", color = HeadingBlack)
                    }
                }
            )
        }

        // --- EDIT DIALOG FORM ---
        editingItem?.let { item ->
            var editTitle by remember { mutableStateOf(item.title) }
            var editClient by remember { mutableStateOf(item.clientName) }
            var editDesc by remember { mutableStateOf(item.description) }
            var editCat by remember { mutableStateOf(item.category) }

            AlertDialog(
                onDismissRequest = { editingItem = null },
                title = { Text("EDIT PORTFOLIO PROJECT", fontWeight = FontWeight.Bold, color = HeadingBlack) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Project Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editClient,
                            onValueChange = { editClient = it },
                            label = { Text("Client Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editDesc,
                            onValueChange = { editDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Selector
                        Text("CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.filter { it != "All" }.forEach { c ->
                                val selected = editCat == c
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (selected) AccentLime else SoftGray)
                                        .clickable { editCat = c }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = c,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HeadingBlack
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editTitle.isNotEmpty() && editClient.isNotEmpty()) {
                                // Save edit
                                val updated = item.copy(
                                    title = editTitle,
                                    clientName = editClient,
                                    description = editDesc,
                                    category = editCat
                                )
                                viewModel.updatePortfolioItem(updated)
                                editingItem = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HeadingBlack, contentColor = Color.White)
                    ) {
                        Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingItem = null }) {
                        Text("CANCEL", color = HeadingBlack)
                    }
                }
            )
        }
    }
}
