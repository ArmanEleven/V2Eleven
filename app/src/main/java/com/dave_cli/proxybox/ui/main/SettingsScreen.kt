package ir.armaneleven.v2eleven.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ir.armaneleven.v2eleven.BuildConfig
import ir.armaneleven.v2eleven.R
import ir.armaneleven.v2eleven.core.GeoProfile
import ir.armaneleven.v2eleven.core.LocaleHelper
import ir.armaneleven.v2eleven.core.UpdateResult
import ir.armaneleven.v2eleven.ui.main.theme.C

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenSplitTunnel: () -> Unit = {},
    onLanguageChanged: () -> Unit = {},
) {
    val context = LocalContext.current

    val isUpdatingGeo by viewModel.isUpdatingGeo.collectAsState()
    val geoProgress by viewModel.geoProgress.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val currentGeoProfile by viewModel.geoProfile.collectAsState()
    val subUserAgent by viewModel.subUserAgent.collectAsState()

    var updateResult by remember {
        mutableStateOf<UpdateResult?>(null)
    }

    var showGeoProfileDialog by remember {
        mutableStateOf(false)
    }

    var showUaDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(C.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {

        // ─────────────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────────────

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 18.dp,
                    top = 10.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        C.Primary.copy(alpha = 0.10f)
                    )
                    .border(
                        1.dp,
                        C.Border,
                        CircleShape
                    )
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u2190",
                    color = C.TextPrimary,
                    fontSize = 23.sp
                )
            }

            Spacer(
                Modifier.width(13.dp)
            )

            Column {
                Text(
                    text = "SETTINGS",
                    color = C.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "V2 ELEVEN",
                    color = C.Primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        // ─────────────────────────────────────────────
        // BRAND CARD
        // ─────────────────────────────────────────────

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
                .clip(
                    RoundedCornerShape(18.dp)
                )
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF26090D),
                            Color(0xFF15090C),
                            Color(0xFF0E080A)
                        )
                    )
                )
                .border(
                    1.dp,
                    C.Border,
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {

            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        C.PrimaryGlow,
                                        C.PrimaryDark,
                                        Color(0xFF160609)
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                C.Primary.copy(alpha = 0.75f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "11",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(
                        Modifier.width(13.dp)
                    )

                    Column {
                        Text(
                            text = "V2 ELEVEN",
                            color = C.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Text(
                            text = "ARMAN PING",
                            color = C.PrimaryGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Spacer(
                    Modifier.height(12.dp)
                )

                Text(
                    text = "Private connection. Eleven style.",
                    color = C.TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    BrandBadge(
                        text = "XRAY",
                        color = C.Primary
                    )

                    BrandBadge(
                        text = "TUN",
                        color = C.Violet
                    )

                    BrandBadge(
                        text = "REALITY",
                        color = C.Green
                    )
                }
            }
        }

        // ─────────────────────────────────────────────
        // VPN
        // ─────────────────────────────────────────────

        GroupLabel(
            text = stringResource(R.string.group_vpn)
        )

        SettingsItem(
            icon = "\uD83D\uDD00",
            iconColor = C.Violet,
            title = stringResource(R.string.split_tunneling),
            subtitle = stringResource(R.string.split_tunneling_subtitle),
            onClick = onOpenSplitTunnel
        )

        SettingsItem(
            icon = "\uD83C\uDD94",
            iconColor = C.Blue,
            title = stringResource(R.string.sub_user_agent),
            subtitle = if (subUserAgent.isBlank()) {
                stringResource(R.string.sub_ua_default)
            } else {
                subUserAgent
            },
            onClick = {
                showUaDialog = true
            }
        )

        // ─────────────────────────────────────────────
        // LANGUAGE
        // ─────────────────────────────────────────────

        val currentLang =
            LocaleHelper.getSavedLanguage(context)

        val langLabel =
            LocaleHelper.getDisplayName(currentLang)

        SettingsItem(
            icon = "\uD83C\uDF10",
            iconColor = C.Amber,
            title = stringResource(R.string.language),
            subtitle = langLabel,
            onClick = {

                val next = when (currentLang) {
                    "" -> "en"
                    "en" -> "ru"
                    else -> ""
                }

                LocaleHelper.saveLanguage(
                    context,
                    next
                )

                onLanguageChanged()
            }
        )

        // ─────────────────────────────────────────────
        // UPDATES
        // ─────────────────────────────────────────────

        GroupLabel(
            text = stringResource(R.string.group_updates)
        )

        SettingsItem(
            icon = "\u2193",
            iconColor = C.Pink,
            title = stringResource(R.string.update_app),
            subtitle = if (isCheckingUpdate) {
                stringResource(R.string.update_app_checking)
            } else {
                stringResource(
                    R.string.update_app_subtitle,
                    BuildConfig.VERSION_NAME
                )
            },
            onClick = {

                viewModel.checkForUpdate { result ->

                    if (
                        result.hasUpdate &&
                        result.downloadUrl != null
                    ) {
                        updateResult = result

                    } else if (result.hasUpdate) {

                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.update_found_no_apk,
                                result.latestVersion
                            ),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.on_latest_version,
                                result.latestVersion
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        SettingsItem(
            icon = "\uD83D\uDDFA",
            iconColor = C.Violet,
            title = stringResource(R.string.geo_profile),
            subtitle = geoProfileDisplayName(
                currentGeoProfile
            ),
            onClick = {
                showGeoProfileDialog = true
            }
        )

        SettingsItem(
            icon = "\uD83C\uDF0D",
            iconColor = C.Blue,
            title = stringResource(R.string.update_geo),
            subtitle = if (
                isUpdatingGeo &&
                geoProgress.isNotEmpty()
            ) {
                geoProgress
            } else {
                stringResource(
                    R.string.update_geo_subtitle
                )
            },
            onClick = {

                viewModel.updateGeoFiles { result ->

                    Toast.makeText(
                        context,
                        result,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        // ─────────────────────────────────────────────
        // ABOUT
        // ─────────────────────────────────────────────

        GroupLabel(
            text = stringResource(R.string.group_about)
        )

        SettingsItem(
            icon = "\u2666",
            iconColor = C.Primary,
            title = "V2 ELEVEN",
            subtitle = "ARMAN PING  •  Version ${BuildConfig.VERSION_NAME}",
            showArrow = false
        )

        SettingsItem(
            icon = "\uD83D\uDCAC",
            iconColor = C.Blue,
            title = "Telegram",
            subtitle = "@PingArmanBot",
            subtitleColor = C.Primary,
            onClick = {
                try {

                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://t.me/PingArmanBot"
                            )
                        )
                    )

                } catch (_: Exception) {

                    Toast.makeText(
                        context,
                        "@PingArmanBot",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        SettingsItem(
            icon = "\u2B21",
            iconColor = C.TextPrimary,
            title = "V2ElevenProxyBox",
            subtitle = "GitHub • V2 ELEVEN",
            subtitleColor = C.Primary,
            onClick = {

                try {

                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://github.com/atroxxx89-beep/V2ElevenProxyBox"
                            )
                        )
                    )

                } catch (_: Exception) {

                    Toast.makeText(
                        context,
                        "github.com/atroxxx89-beep/V2ElevenProxyBox",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        SettingsItem(
            icon = "\u26A1",
            iconColor = C.Green,
            title = stringResource(R.string.xray_core),
            subtitle = stringResource(R.string.xray_subtitle),
            showArrow = false
        )

        SettingsItem(
            icon = "\uD83D\uDCDC",
            iconColor = C.Amber,
            title = stringResource(R.string.license),
            subtitle = stringResource(R.string.license_value),
            showArrow = false
        )

        Spacer(
            Modifier.height(18.dp)
        )

        Text(
            text = "ELEVEN PRO  •  ARMAN PING",
            color = C.TextDim,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    // ─────────────────────────────────────────────
    // GEO PROFILE DIALOG
    // ─────────────────────────────────────────────

    if (showGeoProfileDialog) {

        GeoProfileDialog(
            currentProfile = currentGeoProfile,

            onSelect = { profile ->

                viewModel.setGeoProfile(
                    profile
                )

                showGeoProfileDialog = false

                Toast.makeText(
                    context,
                    context.getString(
                        R.string.geo_profile_switched,
                        geoProfileDisplayName(profile)
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            },

            onDismiss = {
                showGeoProfileDialog = false
            }
        )
    }

    // ─────────────────────────────────────────────
    // USER AGENT DIALOG
    // ─────────────────────────────────────────────

    if (showUaDialog) {

        UserAgentDialog(
            currentUa = subUserAgent,

            onSelect = { ua ->

                viewModel.setSubUserAgent(
                    ua
                )

                showUaDialog = false
            },

            onDismiss = {
                showUaDialog = false
            }
        )
    }

    // ─────────────────────────────────────────────
    // UPDATE DIALOG
    // ─────────────────────────────────────────────

    updateResult?.let { result ->

        AlertDialog(
            onDismissRequest = {
                updateResult = null
            },

            title = {
                Text(
                    stringResource(
                        R.string.update_available
                    ),
                    color = C.TextPrimary
                )
            },

            text = {

                Column {

                    Text(
                        stringResource(
                            R.string.new_version,
                            result.latestVersion
                        ),
                        color = C.TextPrimary,
                        fontSize = 14.sp
                    )

                    if (!result.releaseNotes.isNullOrBlank()) {

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(
                            result.releaseNotes,
                            color = C.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.downloadAndInstallUpdate(
                            context,
                            result.downloadUrl!!,
                            result.latestVersion
                        )

                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.downloading_update
                            ),
                            Toast.LENGTH_SHORT
                        ).show()

                        updateResult = null
                    }
                ) {
                    Text(
                        stringResource(
                            R.string.download_install
                        ),
                        color = C.Primary
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        updateResult = null
                    }
                ) {
                    Text(
                        stringResource(R.string.later),
                        color = C.TextSecondary
                    )
                }
            },

            containerColor = C.SurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────
// BRAND BADGE
// ─────────────────────────────────────────────

@Composable
private fun BrandBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(
                color.copy(alpha = 0.10f)
            )
            .border(
                1.dp,
                color.copy(alpha = 0.22f),
                RoundedCornerShape(6.dp)
            )
            .padding(
                horizontal = 7.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

// ─────────────────────────────────────────────
// GROUP LABEL
// ─────────────────────────────────────────────

@Composable
private fun GroupLabel(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = 17.dp,
                bottom = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(5.dp)
                .background(
                    C.Primary,
                    CircleShape
                )
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Text(
            text = text.uppercase(),
            color = C.PrimaryGlow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

// ─────────────────────────────────────────────
// SETTINGS ITEM
// ─────────────────────────────────────────────

@Composable
private fun SettingsItem(
    icon: String,
    iconColor: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color = C.TextDim,
    showArrow: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(
                C.Surface.copy(alpha = 0.78f)
            )
            .border(
                1.dp,
                C.Border.copy(alpha = 0.80f),
                RoundedCornerShape(14.dp)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = 13.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(39.dp)
                .clip(
                    RoundedCornerShape(11.dp)
                )
                .background(
                    iconColor.copy(alpha = 0.09f)
                )
                .border(
                    1.dp,
                    iconColor.copy(alpha = 0.16f),
                    RoundedCornerShape(11.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = icon,
                fontSize = 17.sp,
                color = iconColor
            )
        }

        Spacer(
            Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = C.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }

        if (showArrow && onClick != null) {

            Text(
                text = "\u203A",
                color = C.TextDim,
                fontSize = 22.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
// GEO PROFILE
// ─────────────────────────────────────────────

private fun geoProfileDisplayName(
    profile: GeoProfile
): String =
    when (profile.id) {
        "loyalsoldier" -> "Loyalsoldier"
        "v2fly" -> "v2fly upstream"
        "runetfreedom" -> "runetfreedom (RU-focused)"
        else -> profile.id
    }

@Composable
private fun GeoProfileDialog(
    currentProfile: GeoProfile,
    onSelect: (GeoProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                stringResource(
                    R.string.geo_profile
                ),
                color = C.TextPrimary
            )
        },

        text = {

            Column(
                Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {

                Text(
                    stringResource(
                        R.string.geo_profile_hint
                    ),
                    color = C.TextDim,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )

                GeoProfile.ALL.forEach { profile ->

                    val isActive =
                        profile.id == currentProfile.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .background(
                                if (isActive) {
                                    C.Primary.copy(
                                        alpha = 0.15f
                                    )
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {
                                onSelect(profile)
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                geoProfileDisplayName(
                                    profile
                                ),
                                color = if (isActive) {
                                    C.Primary
                                } else {
                                    C.TextPrimary
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isActive) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                }
                            )

                            Text(
                                geoProfileDescription(
                                    profile
                                ),
                                color = C.TextDim,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(
                                    top = 2.dp
                                )
                            )
                        }

                        if (isActive) {

                            Text(
                                "\u2713",
                                color = C.Primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    stringResource(R.string.close),
                    color = C.TextSecondary
                )
            }
        },

        containerColor = C.SurfaceVariant
    )
}

@Composable
private fun geoProfileDescription(
    profile: GeoProfile
): String =
    when (profile.id) {
        "loyalsoldier" ->
            stringResource(
                R.string.geo_desc_loyalsoldier
            )

        "v2fly" ->
            stringResource(
                R.string.geo_desc_v2fly
            )

        "runetfreedom" ->
            stringResource(
                R.string.geo_desc_runetfreedom
            )

        else -> ""
    }

// ─────────────────────────────────────────────
// USER AGENT
// ─────────────────────────────────────────────

private val UA_PRESETS =
    listOf(
        "" to "Default (none)",
        "Happ/3.20.4/Android" to "Happ Android",
        "v2rayNG/1.8.29" to "v2rayNG",
        "ClashForAndroid/2.5.12" to "Clash for Android"
    )

@Composable
private fun UserAgentDialog(
    currentUa: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var customUa by remember {
        mutableStateOf(currentUa)
    }

    var isCustom by remember {
        mutableStateOf(
            UA_PRESETS.none {
                it.first == currentUa
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                stringResource(
                    R.string.sub_user_agent
                ),
                color = C.TextPrimary
            )
        },

        text = {

            Column {

                Text(
                    stringResource(
                        R.string.sub_ua_hint
                    ),
                    color = C.TextDim,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )

                UA_PRESETS.forEach { (ua, label) ->

                    val isActive =
                        !isCustom && ua == currentUa

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                if (isActive) {
                                    C.Primary.copy(
                                        alpha = 0.15f
                                    )
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {

                                isCustom = false
                                onSelect(ua)
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                label,
                                color = if (isActive) {
                                    C.Primary
                                } else {
                                    C.TextPrimary
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isActive) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                }
                            )

                            if (ua.isNotBlank()) {

                                Text(
                                    ua,
                                    color = C.TextDim,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isActive) {

                            Text(
                                "\u2713",
                                color = C.Primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(
                    stringResource(
                        R.string.sub_ua_custom
                    ),
                    color = C.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        bottom = 4.dp
                    )
                )

                OutlinedTextField(
                    value = customUa,
                    onValueChange = {
                        customUa = it
                        isCustom = true
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            "MyApp/1.0",
                            color = C.TextDim,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = C.TextPrimary,
                        unfocusedTextColor = C.TextPrimary,
                        focusedBorderColor = C.Primary,
                        unfocusedBorderColor =
                            C.TextDim.copy(
                                alpha = 0.3f
                            ),
                        cursorColor = C.Primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            if (isCustom) {

                TextButton(
                    onClick = {
                        onSelect(
                            customUa.trim()
                        )
                    }
                ) {
                    Text(
                        stringResource(
                            R.string.save
                        ),
                        color = C.Primary
                    )
                }
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    stringResource(
                        R.string.cancel
                    ),
                    color = C.TextSecondary
                )
            }
        },

        containerColor = C.SurfaceVariant
    )
}