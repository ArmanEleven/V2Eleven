package com.v2ray.ang.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ProfileItem

enum class V2ElevenSection(val titleRes: Int, @DrawableRes val iconRes: Int) {
    SUBSCRIPTION(R.string.v2eleven_nav_subscription, R.drawable.ic_subscriptions_24dp),
    GAMING(R.string.v2eleven_nav_gaming, R.drawable.ic_play_24dp),
    BROWSING(R.string.v2eleven_nav_browsing, R.drawable.ic_source_code_24dp)
}

enum class V2ElevenServerCategory {
    ALL,
    TURKEY_GAMING,
    WEB_BROWSING;

    fun matches(profile: ProfileItem): Boolean {
        val turkey = isTurkeyServer(profile)
        return when (this) {
            ALL -> true
            TURKEY_GAMING -> turkey
            WEB_BROWSING -> !turkey
        }
    }
}

internal fun isTurkeyServer(profile: ProfileItem): Boolean {
    val searchableName = listOf(
        profile.remarks,
        profile.description.orEmpty(),
        profile.server.orEmpty(),
        profile.host.orEmpty()
    ).joinToString(" ")
    return Regex(
        "(?iu)(?:🇹🇷|(?<![\\p{L}\\p{N}])(?:turkey|türkiye|turkiye|ترکیه|tr)(?![\\p{L}\\p{N}]))"
    ).containsMatchIn(searchableName)
}

@Composable
fun V2ElevenNavigationBar(
    selected: V2ElevenSection,
    onSelect: (V2ElevenSection) -> Unit
) {
    Surface(
        color = Color(0xF20A0B0E),
        contentColor = Color(0xFFF2F2F4),
        tonalElevation = 0.dp,
        shadowElevation = 14.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 9.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            V2ElevenSection.entries.forEach { item ->
                val isSelected = item == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (isSelected) Color(0xFF7F1D2D).copy(alpha = 0.58f)
                            else Color.Transparent
                        )
                        .clickable { onSelect(item) }
                        .padding(horizontal = 5.dp, vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFFFF7D88) else Color(0xFF989BA3),
                        modifier = Modifier.size(21.dp)
                    )
                    Text(
                        text = stringResource(item.titleRes),
                        color = if (isSelected) Color.White else Color(0xFFA7AAB2),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF6478))
                        )
                    } else {
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun V2ElevenSubscriptionPage(
    onAddSubscription: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    var url by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF08090C), Color(0xFF101116), Color(0xFF170D12))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = stringResource(R.string.v2eleven_subscription_eyebrow),
                color = Color(0xFFFF7A88),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 2.sp
            )
            Text(
                text = stringResource(R.string.v2eleven_subscription_title),
                color = Color(0xFFF5F3F4),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        V2ElevenGlassCard {
            Text(
                text = stringResource(R.string.v2eleven_link_title),
                color = Color(0xFFF7F3F5),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.v2eleven_link_description),
                color = Color(0xFFB5B0B6),
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    localError = false
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.v2eleven_subscription_url)) },
                placeholder = { Text(stringResource(R.string.v2eleven_subscription_placeholder)) },
                isError = localError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFF9F5F7),
                    unfocusedTextColor = Color(0xFFF1EDF1),
                    focusedBorderColor = Color(0xFFFF697D),
                    unfocusedBorderColor = Color(0xFF55515A),
                    focusedLabelColor = Color(0xFFFF8995),
                    unfocusedLabelColor = Color(0xFFAAA4AD),
                    cursorColor = Color(0xFFFF697D),
                    focusedContainerColor = Color(0xFF08090C).copy(alpha = 0.8f),
                    unfocusedContainerColor = Color(0xFF08090C).copy(alpha = 0.65f)
                )
            )
            if (localError) {
                Text(
                    text = stringResource(R.string.v2eleven_enter_subscription_url),
                    color = Color(0xFFFF929D),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val copied = clipboard.getText()?.text.orEmpty()
                        if (copied.isBlank()) {
                            localError = true
                        } else {
                            url = copied.trim()
                            localError = false
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = null,
                        tint = Color(0xFFFF8995),
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.v2eleven_paste_link),
                        color = Color(0xFFFFA0A8)
                    )
                }
                Button(
                    onClick = {
                        val link = url.trim()
                        if (link.isBlank()) {
                            localError = true
                        } else {
                            onAddSubscription(link)
                            localError = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9D263C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Text(
                        text = stringResource(R.string.v2eleven_add_subscription),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun V2ElevenEmptyServersPage(
    section: V2ElevenSection,
    onGoToSubscription: () -> Unit,
    onRenewalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF08090C), Color(0xFF151015)))
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.055f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.11f)),
            modifier = Modifier.size(74.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(section.iconRes),
                    contentDescription = null,
                    tint = Color(0xFFFF8995),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            stringResource(
                if (section == V2ElevenSection.GAMING) R.string.v2eleven_no_gaming
                else R.string.v2eleven_no_browsing
            ),
            color = Color(0xFFF7F3F5),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.v2eleven_no_servers_hint),
            color = Color(0xFFB5B0B6),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onGoToSubscription,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9D263C)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.v2eleven_go_subscription))
        }
        TextButton(onClick = onRenewalClick) {
            Text(stringResource(R.string.v2eleven_get_link), color = Color(0xFFFF8995))
        }
    }
}

@Composable
private fun V2ElevenGlassCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.045f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.095f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            content = { content() }
        )
    }
}

@Composable
private fun V2ElevenInfoTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        color = Color(0xFF08090C).copy(alpha = 0.72f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(title, color = Color(0xFFA6A1A8), style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                color = Color(0xFFE5DFE4),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
