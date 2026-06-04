package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// --- Floating Particle Elements ---
@Composable
fun FloatingParticlesContainer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "particles")
    val translationY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translate"
    )

    Box(modifier = modifier.fillMaxWidth().height(180.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Render modern cinematic grid lines
            drawLine(
                color = SolidWhite.copy(alpha = 0.12f),
                start = Offset(0f, height / 2 + translationY),
                end = Offset(width, height / 2 + translationY),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = SolidWhite.copy(alpha = 0.12f),
                start = Offset(width / 3, 0f),
                end = Offset(width / 3, height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = SolidWhite.copy(alpha = 0.2f * 1.2f),
                start = Offset(2 * width / 3, 0f),
                end = Offset(2 * width / 3, height),
                strokeWidth = 1.dp.toPx()
            )

            // Dynamic float nodes
            drawCircle(
                color = AccentLime.copy(alpha = 0.7f),
                radius = 8.dp.toPx(),
                center = Offset(width * 0.15f, height * 0.3f + translationY)
            )
            drawCircle(
                color = SolidWhite.copy(alpha = 0.25f),
                radius = 12.dp.toPx(),
                center = Offset(width * 0.85f, height * 0.7f - translationY)
            )
            drawCircle(
                color = AccentLime.copy(alpha = 0.5f),
                radius = 5.dp.toPx(),
                center = Offset(width * 0.5f, height * 0.8f + translationY * 0.5f)
            )
        }
    }
}

// --- Cinematic 3D Element Renderers ---
@Composable
fun Luxury3DCanvas(type: String, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "luxury_3d")
    val animatedRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )
    val floatOffset by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = modifier
            .size(140.dp)
            .graphicsLayer {
                translationY = floatOffset
                rotationY = animatedRotation * 0.15f
            },
        contentAlignment = Alignment.Center
    ) {
        // Draw elegant mathematical models resembling high-end camera, drone, or studio equipment
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = size.width * 0.35f

            when (type) {
                "Lens" -> {
                    // Outer Camera Ring shadow & flare
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentLime.copy(alpha = 0.25f), Color.Transparent),
                            center = center,
                            radius = baseRadius * 1.5f
                        )
                    )
                    drawCircle(
                        color = HeadingBlack,
                        radius = baseRadius,
                        style = Stroke(width = 8.dp.toPx())
                    )
                    drawCircle(
                        color = CardCharcoal,
                        radius = baseRadius * 0.85f,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    // Inner lens glass reflections
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentLime, Color.Transparent, DarkPremium.copy(alpha = 0.5f))
                        ),
                        radius = baseRadius * 0.6f
                    )
                    // Aperture Blades
                    val angleStep = Math.PI / 3
                    for (i in 0..5) {
                        val angle = i * angleStep + Math.toRadians(animatedRotation.toDouble())
                        drawLine(
                            color = BackgroundIvory.copy(alpha = 0.8f),
                            start = center,
                            end = Offset(
                                (center.x + baseRadius * 0.6f * cos(angle)).toFloat(),
                                (center.y + baseRadius * 0.6f * sin(angle)).toFloat()
                            ),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
                "Drone" -> {
                    // Rotor paths
                    drawCircle(
                        color = HeadingBlack.copy(alpha = 0.1f),
                        radius = baseRadius * 1.3f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Drone Core Body representation
                    val dronePath = Path().apply {
                        val hWidth = size.width / 2
                        val hHeight = size.height / 2
                        moveTo(hWidth - 25f, hHeight - 25f)
                        lineTo(hWidth + 25f, hHeight - 25f)
                        lineTo(hWidth + 40f, hHeight + 35f)
                        lineTo(hWidth - 40f, hHeight + 35f)
                        close()
                    }
                    drawPath(path = dronePath, color = HeadingBlack)
                    
                    // Propellers rotating
                    val angleRad = Math.toRadians(animatedRotation.toDouble() * 3)
                    val propX1 = (center.x + baseRadius * cos(angleRad)).toFloat()
                    val propY1 = (center.y + baseRadius * sin(angleRad)).toFloat()
                    val propX2 = (center.x - baseRadius * cos(angleRad)).toFloat()
                    val propY2 = (center.y - baseRadius * sin(angleRad)).toFloat()
                    
                    drawLine(
                        color = AccentLime,
                        start = Offset(propX1, propY1),
                        end = Offset(propX2, propY2),
                        strokeWidth = 5.dp.toPx()
                    )
                    drawCircle(color = HeadingBlack, radius = 8.dp.toPx(), center = center)
                }
                "Camera" -> {
                    // Camera housing body
                    drawRect(
                        color = CardCharcoal,
                        topLeft = Offset(center.x - baseRadius, center.y - baseRadius * 0.6f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 2, baseRadius * 1.2f)
                    )
                    // Top flash mount
                    drawRect(
                        color = HeadingBlack,
                        topLeft = Offset(center.x - baseRadius * 0.3f, center.y - baseRadius * 0.9f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 0.6f, baseRadius * 0.3f)
                    )
                    // Dynamic rotating lens mount
                    drawCircle(
                        color = AccentLime,
                        radius = baseRadius * 0.45f,
                        center = center,
                        style = Stroke(width = 6.dp.toPx())
                    )
                    drawCircle(
                        color = HeadingBlack,
                        radius = baseRadius * 0.3f,
                        center = center
                    )
                }
                "Microphone" -> {
                    // Podcast condenser mic capsule
                    drawRoundRect(
                        color = CardCharcoal,
                        topLeft = Offset(center.x - baseRadius * 0.5f, center.y - baseRadius),
                        size = androidx.compose.ui.geometry.Size(baseRadius, baseRadius * 1.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                    )
                    // Metallic grill lines
                    drawLine(
                        color = AccentLime,
                        start = Offset(center.x - baseRadius * 0.4f, center.y - baseRadius * 0.5f),
                        end = Offset(center.x + baseRadius * 0.4f, center.y - baseRadius * 0.5f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = AccentLime,
                        start = Offset(center.x - baseRadius * 0.4f, center.y - baseRadius * 0.1f),
                        end = Offset(center.x + baseRadius * 0.4f, center.y - baseRadius * 0.1f),
                        strokeWidth = 2.dp.toPx()
                    )
                    // Desk Stand
                    drawLine(
                        color = HeadingBlack,
                        start = Offset(center.x, center.y + baseRadius * 0.5f),
                        end = Offset(center.x, center.y + baseRadius * 1.4f),
                        strokeWidth = 8.dp.toPx()
                    )
                    drawLine(
                        color = HeadingBlack,
                        start = Offset(center.x - baseRadius * 0.6f, center.y + baseRadius * 1.4f),
                        end = Offset(center.x + baseRadius * 0.6f, center.y + baseRadius * 1.4f),
                        strokeWidth = 10.dp.toPx()
                    )
                }
                "StudioLights", "Studio Lights" -> {
                    // Studio Light Stand
                    drawLine(
                        color = HeadingBlack,
                        start = center,
                        end = Offset(center.x, center.y + baseRadius * 1.3f),
                        strokeWidth = 4.dp.toPx()
                    )
                    // Stand Base Legs
                    drawLine(
                        color = HeadingBlack,
                        start = Offset(center.x, center.y + baseRadius * 1.1f),
                        end = Offset(center.x - baseRadius * 0.5f, center.y + baseRadius * 1.4f),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = HeadingBlack,
                        start = Offset(center.x, center.y + baseRadius * 1.1f),
                        end = Offset(center.x + baseRadius * 0.5f, center.y + baseRadius * 1.4f),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Glowing Light Source Ambient backdrop
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentLime.copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(center.x, center.y - baseRadius * 0.3f),
                            radius = baseRadius * 1.3f
                        )
                    )

                    // LED Softbox Panel (Angled rectangular shape)
                    val softboxWidth = baseRadius * 1.1f
                    val softboxHeight = baseRadius * 0.8f
                    val angleRad = Math.toRadians(animatedRotation.toDouble() * 0.3)
                    
                    val cosA = cos(angleRad).toFloat()
                    val sinA = sin(angleRad).toFloat()

                    val p1 = Offset(center.x - softboxWidth * 0.5f, center.y - softboxHeight * 0.5f)
                    val p2 = Offset(center.x + softboxWidth * 0.5f, center.y - softboxHeight * 0.4f)
                    val p3 = Offset(center.x + softboxWidth * 0.5f, center.y + softboxHeight * 0.4f)
                    val p4 = Offset(center.x - softboxWidth * 0.5f, center.y + softboxHeight * 0.5f)

                    // Helper to rotate point about center
                    val rp1 = Offset(
                        center.x + (p1.x - center.x) * cosA - (p1.y - center.y) * sinA,
                        center.y + (p1.x - center.x) * sinA + (p1.y - center.y) * cosA
                    )
                    val rp2 = Offset(
                        center.x + (p2.x - center.x) * cosA - (p2.y - center.y) * sinA,
                        center.y + (p2.x - center.x) * sinA + (p2.y - center.y) * cosA
                    )
                    val rp3 = Offset(
                        center.x + (p3.x - center.x) * cosA - (p3.y - center.y) * sinA,
                        center.y + (p3.x - center.x) * sinA + (p3.y - center.y) * cosA
                    )
                    val rp4 = Offset(
                        center.x + (p4.x - center.x) * cosA - (p4.y - center.y) * sinA,
                        center.y + (p4.x - center.x) * sinA + (p4.y - center.y) * cosA
                    )

                    val softboxPath = Path().apply {
                        moveTo(rp1.x, rp1.y)
                        lineTo(rp2.x, rp2.y)
                        lineTo(rp3.x, rp3.y)
                        lineTo(rp4.x, rp4.y)
                        close()
                    }

                    // Draw softbox back shell
                    drawPath(path = softboxPath, color = CardCharcoal)

                    // Draw glowing inner diffuser face
                    val dp1 = Offset(
                        center.x + (center.x - softboxWidth * 0.4f - center.x) * cosA - (center.y - softboxHeight * 0.35f - center.y) * sinA,
                        center.y + (center.x - softboxWidth * 0.4f - center.x) * sinA + (center.y - softboxHeight * 0.35f - center.y) * cosA
                    )
                    val dp2 = Offset(
                        center.x + (center.x + softboxWidth * 0.4f - center.x) * cosA - (center.y - softboxHeight * 0.25f - center.y) * sinA,
                        center.y + (center.x + softboxWidth * 0.4f - center.x) * sinA + (center.y - softboxHeight * 0.25f - center.y) * cosA
                    )
                    val dp3 = Offset(
                        center.x + (center.x + softboxWidth * 0.4f - center.x) * cosA - (center.y + softboxHeight * 0.25f - center.y) * sinA,
                        center.y + (center.x + softboxWidth * 0.4f - center.x) * sinA + (center.y + softboxHeight * 0.25f - center.y) * cosA
                    )
                    val dp4 = Offset(
                        center.x + (center.x - softboxWidth * 0.4f - center.x) * cosA - (center.y + softboxHeight * 0.35f - center.y) * sinA,
                        center.y + (center.x - softboxWidth * 0.4f - center.x) * sinA + (center.y + softboxHeight * 0.35f - center.y) * cosA
                    )

                    val diffuserPath = Path().apply {
                        moveTo(dp1.x, dp1.y)
                        lineTo(dp2.x, dp2.y)
                        lineTo(dp3.x, dp3.y)
                        lineTo(dp4.x, dp4.y)
                        close()
                    }
                    drawPath(path = diffuserPath, color = AccentLime.copy(alpha = 0.85f))

                    // Draw honeycomb lines/grid over diffuser
                    drawLine(color = CardCharcoal.copy(alpha = 0.6f), start = dp1, end = dp3, strokeWidth = 1.dp.toPx())
                    drawLine(color = CardCharcoal.copy(alpha = 0.6f), start = dp2, end = dp4, strokeWidth = 1.dp.toPx())
                }
                else -> {
                    // Default glowing production symbol (Film board)
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(AccentLime, HeadingBlack, AccentLime),
                            center = center
                        ),
                        radius = baseRadius,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
        }
    }
}

// --- Dynamic Parallax Tilt Card for Premium Touch Feedback ---
@Composable
fun PremiumGlassCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DarkPremium
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentLime.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                content = content
            )
        }
    )
}

// --- Testimonials Trust Module ---
@Composable
fun ClientTrustSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "TRUST VALUE",
            style = MaterialTheme.typography.labelLarge,
            color = AccentLime,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Completed 180+ Elite Projects",
            style = MaterialTheme.typography.titleLarge,
            color = HeadingBlack,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val reviews = listOf(
            Triple("Aura Cafe Indore", "Mohit's team transformed our brand. Their Reels strategy achieved 1.2 million views within three weeks!", 5),
            Triple("Thar Motors showroom", "The commercial car videography is peerless. Raw, energetic, and extremely premium. Best in Indore.", 5),
            Triple("Dewda Wellness Podcast", "Outstanding acoustic mastering and multicar setup configuration. Fully elite experience.", 5)
        )

        reviews.forEach { (client, reviewValue, stars) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardCharcoal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = client,
                            fontWeight = FontWeight.SemiBold,
                            color = BackgroundIvory,
                            fontSize = 15.sp
                        )
                        Row {
                            repeat(stars) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = AccentLime,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"$reviewValue\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftGray
                    )
                }
            }
        }
    }
}

// --- Payment Policy Highlight Column ---
@Composable
fun PaymentPolicySection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardCharcoal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("payment_policy_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "COMMERCIAL CONTRACT TERMS",
                style = MaterialTheme.typography.labelLarge,
                color = AccentLime,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "50% ADVANCE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Required before booking dispatch or date lock.",
                        color = SoftGray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "50% DELIVERY",
                        color = AccentLime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Paid immediately post-shoot edit completions.",
                        color = SoftGray,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = SolidWhite.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Accepted Channels: PhonePe, Paytm QR, Bank Transfers.",
                color = BackgroundIvory,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Verified internally by Mohit Dewda. Contact +91 9755550380 for manual accounts validation.",
                color = SoftGray,
                fontSize = 11.sp
            )
        }
    }
}

// --- Sticky Floating WhatsApp Communications Bar ---
@Composable
fun FloatingWhatsappButton(context: Context = LocalContext.current) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        ExtendedFloatingActionButton(
            onClick = {
                val url = "https://wa.me/919755550380?text=Hi%20Creative%20Team%20Production%20Indore,%20I%20would%20like%20to%20discuss%20booking%20a%20shoot!"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                context.startActivity(intent)
            },
            containerColor = Color(0xFF25D366), // Official WhatsApp brand green
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .testTag("whatsapp_sticky_fab")
                .height(38.dp)
                .drawBehind {
                    val glowRadius = 4.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFF25D366).copy(alpha = 0.25f),
                        topLeft = Offset(-glowRadius, -glowRadius),
                        size = androidx.compose.ui.geometry.Size(size.width + glowRadius * 2, size.height + glowRadius * 2),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2 + glowRadius)
                    )
                }
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "WhatsApp representative link",
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "WHATSAPP",
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
