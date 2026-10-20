package com.v2ray.ang.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R

private val GlassBlack = Color(0xFF08090D)
private val GlassPanel = Color(0xD91B1A20)
private val ElevenRed = Color(0xFFFF667C)
private val GlassText = Color(0xFFF7F3F6)
private val GlassMuted = Color(0xFFAAA6B0)

/** A stable identity for a server row: move flag emoji into a circular medallion, not its title. */
internal data class ServerVisualIdentity(val displayName: String, val flagEmoji: String)

private val serverFlagPattern = Regex("(?:\\uD83C[\\uDDE6-\\uDDFF]){2}")

internal fun stripServerFlagEmojis(value: String): String =
    serverFlagPattern.replace(value, " ").replace(Regex("\\s+"), " ").trim()

internal fun serverVisualIdentity(rawName: String): ServerVisualIdentity {
    val explicitFlag = serverFlagPattern.find(rawName)?.value
    val lower = rawName.lowercase()
    val inferredFlag = when {
        "türkiye" in lower || "turkiye" in lower || "turkey" in lower || "ترکیه" in lower -> "🇹🇷"
        "germany" in lower || "deutschland" in lower || "آلمان" in lower -> "🇩🇪"
        "france" in lower || "فرانسه" in lower -> "🇫🇷"
        "singapore" in lower || "سنگاپور" in lower -> "🇸🇬"
        "finland" in lower || "فنلاند" in lower -> "🇫🇮"
        "united states" in lower || "usa" in lower || "america" in lower || "آمریکا" in lower -> "🇺🇸"
        "united kingdom" in lower || "britain" in lower || "uk " in "$lower " || "بریتانیا" in lower -> "🇬🇧"
        "netherlands" in lower || "هلند" in lower -> "🇳🇱"
        "romania" in lower || "رومانی" in lower -> "🇷🇴"
        "hungary" in lower || "مجارستان" in lower -> "🇭🇺"
        "poland" in lower || "لهستان" in lower -> "🇵🇱"
        "italy" in lower || "ایتالیا" in lower -> "🇮🇹"
        "japan" in lower || "ژاپن" in lower -> "🇯🇵"
        "canada" in lower || "کانادا" in lower -> "🇨🇦"
        "sweden" in lower || "سوئد" in lower -> "🇸🇪"
        else -> "◉"
    }
    val cleaned = stripServerFlagEmojis(rawName)
        .replace(Regex("^[\\s|·•:/_-]+|[\\s|·•:/_-]+$"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
    return ServerVisualIdentity(cleaned.ifBlank { "Server" }, explicitFlag ?: inferredFlag)
}

@Composable
fun V2ElevenTopBar(
    titleRes: Int,
    isLoading: Boolean,
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onMenuClick: () -> Unit,
    onAction: (MainAction) -> Unit,
    onMoreMenuAction: (MainMoreMenuAction) -> Unit,
) {
    var showImportMenu by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val edge = Color.White.copy(alpha = 0.12f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xF70A0A0E), Color(0xE9101015))))
            .statusBarsPadding()
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        GlassIconButton(
            iconRes = if (showSearch) R.drawable.ic_arrow_back_24dp else R.drawable.ic_menu_24dp,
            description = stringResource(if (showSearch) R.string.acc_back else R.string.acc_open_menu),
            onClick = { if (showSearch) onSearchClose() else onMenuClick() }
        )
        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(stringResource(R.string.menu_item_search), color = GlassMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GlassText,
                    unfocusedTextColor = GlassText,
                    focusedBorderColor = ElevenRed,
                    unfocusedBorderColor = edge,
                    cursorColor = ElevenRed,
                    focusedContainerColor = Color.White.copy(alpha = 0.04f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.035f)
                )
            )
        } else {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    "V2ELEVEN",
                    color = GlassText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.2.sp
                )
                Text(
                    stringResource(titleRes),
                    color = GlassMuted,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = ElevenRed,
                    strokeWidth = 2.dp
                )
            }
        }

        if (!showSearch) {
            GlassIconButton(
                iconRes = R.drawable.ic_search_24dp,
                description = stringResource(R.string.acc_search),
                onClick = { onSearchToggle(true) }
            )
            Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                GlassIconButton(
                    iconRes = R.drawable.ic_add_24dp,
                    description = stringResource(R.string.acc_add),
                    onClick = { showImportMenu = true },
                    accent = true
                )
                DropdownMenu(
                    expanded = showImportMenu,
                    onDismissRequest = { showImportMenu = false },
                    modifier = Modifier
                        .border(1.dp, edge, RoundedCornerShape(24.dp))
                        .width(258.dp),
                    containerColor = Color(0xF21A191F),
                    tonalElevation = 0.dp,
                    shadowElevation = 22.dp,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    ImportMenuContent { action ->
                        showImportMenu = false
                        onAction(action)
                    }
                }
            }
            Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                GlassIconButton(
                    iconRes = R.drawable.ic_more_vert_24dp,
                    description = stringResource(R.string.acc_more),
                    onClick = { showMenu = true }
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .border(1.dp, edge, RoundedCornerShape(24.dp))
                        .width(270.dp),
                    containerColor = Color(0xF21A191F),
                    tonalElevation = 0.dp,
                    shadowElevation = 22.dp,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    MoreMenuContent { action ->
                        showMenu = false
                        onMoreMenuAction(action)
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassIconButton(
    @DrawableRes iconRes: Int,
    description: String,
    onClick: () -> Unit,
    accent: Boolean = false,
) {
    Surface(
        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(17.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(17.dp),
        color = if (accent) Color(0xFF7C2032).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.055f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = if (accent) 0.19f else 0.105f)),
        tonalElevation = 0.dp,
        shadowElevation = 5.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = description,
                tint = if (accent) Color(0xFFFFB6C0) else GlassText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun V2ElevenFlagMedallion(
    flag: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.065f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
        tonalElevation = 0.dp,
        shadowElevation = 5.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.08f), Color.Transparent, Color(0xFF7D2535).copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(flag, fontSize = 23.sp, color = GlassText, maxLines = 1)
        }
    }
}

@Composable
fun V2ElevenHomePage(
    isRunning: Boolean,
    statusText: String,
    selectedServerName: String,
    selectedServerFlag: String,
    selectedServerPingMillis: Long,
    onToggleConnection: () -> Unit,
    onTestPing: () -> Unit,
    onOpenGaming: () -> Unit,
    onOpenSubscription: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "eleven-orbit")
    val pulse by transition.animateFloat(
        initialValue = 0.965f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eleven-pulse"
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF07080B), Color(0xFF101015), Color(0xFF170C12), Color(0xFF08090D))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 21.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.v2eleven_home_eyebrow).uppercase(),
                    color = ElevenRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.4.sp
                )
                Text(
                    stringResource(R.string.v2eleven_home_title),
                    color = GlassText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (isRunning) Color(0xFF5D1C2B).copy(alpha = 0.64f) else Color.White.copy(alpha = 0.055f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.11f))
            ) {
                Text(
                    statusText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isRunning) Color(0xFFFFAAB5) else GlassMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            stringResource(R.string.v2eleven_home_subtitle),
            modifier = Modifier.fillMaxWidth(),
            color = GlassMuted,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(248.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(218.dp)
                    .graphicsLayer { scaleX = pulse; scaleY = pulse }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF8F263D).copy(alpha = 0.24f), Color(0xFF36131E).copy(alpha = 0.1f), Color.Transparent)
                        )
                    )
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f),), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(182.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.055f), Color(0xFF1D1219).copy(alpha = 0.35f))))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.13f)), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(142.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF672034), Color(0xFF24151C), Color(0xFF100F14))
                        )
                    )
                    .border(BorderStroke(1.dp, Color(0xFFFFA7B2).copy(alpha = 0.25f)), CircleShape)
                    .clickable(onClick = onToggleConnection),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        painter = painterResource(if (isRunning) R.drawable.ic_stop_24dp else R.drawable.ic_play_24dp),
                        contentDescription = null,
                        tint = Color(0xFFFF9EAC),
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        stringResource(if (isRunning) R.string.v2eleven_disconnect else R.string.v2eleven_connect),
                        color = GlassText,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        Button(
            onClick = onToggleConnection,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(23.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color(0xFF381923) else Color(0xFF9A263E),
                contentColor = GlassText
            ),
            border = BorderStroke(1.dp, Color(0xFFFF9AA8).copy(alpha = 0.26f))
        ) {
            Text(
                stringResource(if (isRunning) R.string.v2eleven_disconnect else R.string.v2eleven_connect),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenGaming),
            shape = RoundedCornerShape(28.dp),
            color = GlassPanel,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            tonalElevation = 0.dp,
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                V2ElevenFlagMedallion(flag = selectedServerFlag, modifier = Modifier.size(52.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(R.string.v2eleven_home_select_server),
                        color = GlassMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        selectedServerName.ifBlank { stringResource(R.string.v2eleven_home_no_server) },
                        color = GlassText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        if (selectedServerPingMillis > 0L) "$selectedServerPingMillis ms" else "— ms",
                        color = if (selectedServerPingMillis > 0L) ElevenRed else GlassMuted,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.v2eleven_home_latency),
                        color = GlassMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassActionTile(
                text = stringResource(R.string.v2eleven_nav_gaming),
                iconRes = R.drawable.ic_play_24dp,
                modifier = Modifier.weight(1f),
                onClick = onOpenGaming
            )
            GlassActionTile(
                text = stringResource(R.string.v2eleven_nav_subscription),
                iconRes = R.drawable.ic_subscriptions_24dp,
                modifier = Modifier.weight(1f),
                onClick = onOpenSubscription
            )
            GlassActionTile(
                text = stringResource(R.string.v2eleven_menu_test_ping),
                iconRes = R.drawable.ic_search_24dp,
                modifier = Modifier.weight(1f),
                onClick = onTestPing
            )
        }
    }
}

@Composable
private fun GlassActionTile(
    text: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.height(68.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.09f)),
        tonalElevation = 0.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = ElevenRed, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(4.dp))
            Text(text, color = GlassMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
