package ir.armaneleven.v2eleven.ui.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ir.armaneleven.v2eleven.R
import ir.armaneleven.v2eleven.core.CoreService
import ir.armaneleven.v2eleven.core.CoreService.VpnState
import ir.armaneleven.v2eleven.ui.main.theme.C
import kotlinx.coroutines.delay

@Composable
fun ConnectSection(
    vpnState: VpnState,
    onToggle: () -> Unit,
    onSpeedTest: ((Double?, String?) -> Unit) -> Unit,
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING
    val isError = vpnState == VpnState.ERROR

    val infiniteTransition = rememberInfiniteTransition(label = "connectionPulse")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            isConnected -> C.Green
            isConnecting -> C.PrimaryGlow
            isError -> C.Red
            else -> C.TextDim
        },
        animationSpec = tween(350),
        label = "iconColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isConnected -> C.Green
            isConnecting -> C.PrimaryGlow
            isError -> C.Red
            else -> C.Border
        },
        animationSpec = tween(400),
        label = "borderColor"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = when {
            isConnected -> 0.32f
            isConnecting -> 0.20f
            isError -> 0.14f
            else -> 0f
        },
        animationSpec = tween(500),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Main connection button
        Box(
            modifier = Modifier
                .size(154.dp)
                .drawBehind {
                    if (glowAlpha > 0f) {
                        val extra = if (isConnected || isConnecting) {
                            18.dp.toPx() * pulse
                        } else {
                            12.dp.toPx()
                        }

                        drawCircle(
                            color = when {
                                isConnected -> C.Green
                                isConnecting -> C.Primary
                                else -> C.Red
                            },
                            radius = size.minDimension / 2f + extra,
                            alpha = glowAlpha
                        )

                        if (isConnected || isConnecting) {
                            drawCircle(
                                color = when {
                                    isConnected -> C.Green
                                    else -> C.PrimaryGlow
                                },
                                radius = size.minDimension / 2f + extra * 1.8f,
                                alpha = glowAlpha * 0.22f
                            )
                        }
                    }
                }
                .clip(CircleShape)
                .border(
                    width = if (isConnected || isConnecting) 3.dp else 2.dp,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    brush = when {
                        isConnected -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF321014),
                                Color(0xFF18090C),
                                Color(0xFF090608)
                            )
                        )

                        isConnecting -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF3A0B10),
                                Color(0xFF19070A),
                                Color(0xFF080608)
                            )
                        )

                        isError -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2A080C),
                                Color(0xFF140609),
                                Color(0xFF080608)
                            )
                        )

                        else -> Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF211014),
                                Color(0xFF12090B),
                                Color(0xFF070608)
                            )
                        )
                    }
                )
                .clickable(enabled = !isConnecting) {
                    onToggle()
                },
            contentAlignment = Alignment.Center
        ) {
            PowerIcon(
                color = iconColor,
                modifier = Modifier.size(55.dp),
                glow = isConnected || isConnecting
            )
        }

        Spacer(Modifier.height(18.dp))

        // Connection state
        Text(
            text = when {
                isConnected -> stringResource(R.string.connected)
                isConnecting -> stringResource(R.string.connecting)
                isError -> stringResource(R.string.connection_failed)
                else -> stringResource(R.string.not_connected)
            },
            color = when {
                isConnected -> C.Green
                isConnecting -> C.PrimaryGlow
                isError -> C.Red
                else -> C.TextSecondary
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        if (isConnected) {
            ConnectedInfo(
                onSpeedTest = onSpeedTest
            )
        } else {
            if (!isConnecting && !isError) {
                Text(
                    text = stringResource(R.string.tap_to_connect),
                    color = C.TextDim,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            SpeedTestChip(
                onSpeedTest = onSpeedTest
            )
        }
    }
}

@Composable
private fun ConnectedInfo(
    onSpeedTest: ((Double?, String?) -> Unit) -> Unit,
) {
    val profileName = CoreService.activeProfileName ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (profileName.isNotBlank()) {
            Row(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(C.Surface.copy(alpha = 0.85f))
                    .border(
                        width = 1.dp,
                        color = C.Border,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(C.Green, CircleShape)
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = profileName,
                    color = C.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        ConnectionDuration()

        Spacer(Modifier.height(2.dp))

        SpeedTestChip(
            onSpeedTest = onSpeedTest
        )
    }
}

@Composable
private fun PowerIcon(
    color: Color,
    modifier: Modifier = Modifier,
    glow: Boolean = false,
) {
    Canvas(modifier = modifier) {
        val strokeW = 4.dp.toPx()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f - strokeW * 1.2f

        if (glow) {
            drawArc(
                color = color.copy(alpha = 0.18f),
                startAngle = -60f,
                sweepAngle = 300f,
                useCenter = false,
                style = Stroke(
                    width = strokeW * 3f,
                    cap = StrokeCap.Round
                ),
                topLeft = Offset(cx - r, cy - r),
                size = Size(r * 2f, r * 2f)
            )
        }

        drawArc(
            color = color,
            startAngle = -60f,
            sweepAngle = 300f,
            useCenter = false,
            style = Stroke(
                width = strokeW,
                cap = StrokeCap.Round
            ),
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2f, r * 2f)
        )

        val stemTop = cy - r - strokeW * 0.15f
        val stemBottom = cy - r * 0.02f

        drawLine(
            color = color,
            start = Offset(cx, stemBottom),
            end = Offset(cx, stemTop),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ConnectionDuration() {
    val startTime = CoreService.connectionStartTime

    if (startTime <= 0L) return

    var elapsed by remember { mutableStateOf(0L) }

    LaunchedEffect(startTime) {
        while (true) {
            elapsed = (System.currentTimeMillis() - startTime) / 1000
            delay(1000)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 9.dp)
    ) {
        Text(
            text = String.format(
                "%02d:%02d:%02d",
                elapsed / 3600,
                (elapsed % 3600) / 60,
                elapsed % 60
            ),
            color = C.TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(R.string.duration),
            color = C.TextDim,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

@Composable
private fun SpeedTestChip(
    onSpeedTest: ((Double?, String?) -> Unit) -> Unit,
) {
    var speedMbps by remember { mutableStateOf<Double?>(null) }
    var speedError by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }

    val chipModifier = Modifier
        .padding(top = 10.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(
            Brush.horizontalGradient(
                colors = listOf(
                    C.SurfaceVariant,
                    C.Surface
                )
            )
        )
        .border(
            width = 1.dp,
            color = C.Border,
            shape = RoundedCornerShape(10.dp)
        )

    fun runTest() {
        isTesting = true
        speedError = null

        onSpeedTest { mbps, err ->
            speedMbps = mbps
            speedError = err
            isTesting = false
        }
    }

    when {
        isTesting -> {
            Box(
                modifier = chipModifier.padding(
                    horizontal = 16.dp,
                    vertical = 9.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.testing),
                    color = C.PrimaryGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        speedMbps != null -> {
            Row(
                modifier = chipModifier
                    .clickable { runTest() }
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.speed_result_mbps,
                        speedMbps!!
                    ),
                    color = C.Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "\u21BB",
                    color = C.TextSecondary,
                    fontSize = 15.sp
                )
            }
        }

        speedError != null -> {
            Box(
                modifier = chipModifier
                    .clickable { runTest() }
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "${stringResource(R.string.speed_failed)} \u21BB",
                    color = C.Red,
                    fontSize = 12.sp
                )
            }
        }

        else -> {
            Box(
                modifier = chipModifier
                    .clickable { runTest() }
                    .padding(
                        horizontal = 15.dp,
                        vertical = 9.dp
                    )
            ) {
                Text(
                    text = "\u26A1 ${stringResource(R.string.speed_test)}",
                    color = C.Amber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}