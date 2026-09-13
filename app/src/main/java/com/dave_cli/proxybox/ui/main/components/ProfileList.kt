package com.dave_cli.proxybox.ui.main.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.dave_cli.proxybox.R
import com.dave_cli.proxybox.data.db.ProfileEntity
import com.dave_cli.proxybox.ui.main.theme.C

@Composable
fun ProfileList(
    profiles: List<ProfileEntity>,
    menuOpenId: String?,
    onSelect: (String) -> Unit,
    onMenuToggle: (String) -> Unit,
    onMenuDismiss: () -> Unit,
    onRename: (ProfileEntity) -> Unit,
    onDelete: (ProfileEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 14.dp,
            vertical = 6.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            profiles,
            key = { it.id }
        ) { profile ->

            ProfileCard(
                profile = profile,
                isMenuOpen = menuOpenId == profile.id,

                onSelect = {
                    onSelect(profile.id)
                },

                onMenuToggle = {
                    onMenuToggle(profile.id)
                },

                onMenuDismiss = onMenuDismiss,

                onRename = {
                    onRename(profile)
                },

                onCopyConfig = {
                    onMenuDismiss()

                    val text = profile.rawUri.ifBlank {
                        profile.configJson
                    }

                    val clipboard =
                        context.getSystemService(
                            Context.CLIPBOARD_SERVICE
                        ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText(
                            "config",
                            text
                        )
                    )

                    Toast.makeText(
                        context,
                        context.getString(R.string.config_copied),
                        Toast.LENGTH_SHORT
                    ).show()
                },

                onShare = {
                    onMenuDismiss()

                    val text = profile.rawUri.ifBlank {
                        profile.configJson
                    }

                    val intent = Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                text
                            )
                        },
                        context.getString(R.string.share_config)
                    )

                    context.startActivity(intent)
                },

                onDelete = {
                    onDelete(profile)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileCard(
    profile: ProfileEntity,
    isMenuOpen: Boolean,
    onSelect: () -> Unit,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onRename: () -> Unit,
    onCopyConfig: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    val selected = profile.isSelected

    val pingColor = when {
        profile.latencyMs <= 0 -> C.TextDim
        profile.latencyMs < 60 -> C.Green
        profile.latencyMs < 100 -> Color(0xFF86EFAC)
        profile.latencyMs < 150 -> C.Yellow
        else -> C.Red
    }

    val cardBackground = if (selected) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF26090D),
                Color(0xFF17090C),
                Color(0xFF10080A)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF160A0D),
                Color(0xFF10080A)
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (selected) {
                    drawRoundRect(
                        color = C.Primary.copy(alpha = 0.13f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            16.dp.toPx()
                        )
                    )
                }
            }
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) {
                    C.Primary.copy(alpha = 0.75f)
                } else {
                    C.Border
                },
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = onSelect,
                onLongClick = onMenuToggle
            )
            .padding(
                start = 14.dp,
                top = 13.dp,
                end = 8.dp,
                bottom = 13.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Connection indicator
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        C.Primary.copy(alpha = 0.16f)
                    } else {
                        C.SurfaceVariant
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (selected) {
                        C.Primary.copy(alpha = 0.55f)
                    } else {
                        C.Border
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(
                        if (selected) 10.dp else 8.dp
                    )
                    .background(
                        if (selected) C.Primary else C.TextDim,
                        CircleShape
                    )
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        // Server information
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = profile.name,
                color = C.TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Protocol badge
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(6.dp)
                        )
                        .background(
                            C.Primary.copy(alpha = 0.12f)
                        )
                        .padding(
                            horizontal = 7.dp,
                            vertical = 3.dp
                        )
                ) {
                    Text(
                        text = profile.protocol.uppercase(),
                        color = C.PrimaryGlow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                }

                if (profile.latencyMs > 0) {

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                pingColor,
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "${profile.latencyMs} ms",
                        color = pingColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Selected indicator
        if (selected) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        C.Green,
                        CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )
        }

        // Menu
        Box {

            Text(
                text = "\u22EE",
                color = C.TextDim,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable(
                        onClick = onMenuToggle
                    )
                    .padding(
                        horizontal = 7.dp,
                        vertical = 7.dp
                    )
            )

            DropdownMenu(
                expanded = isMenuOpen,
                onDismissRequest = onMenuDismiss
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "\u270F\uFE0F  ${
                                stringResource(
                                    R.string.rename
                                )
                            }",
                            color = C.TextPrimary
                        )
                    },
                    onClick = {
                        onMenuDismiss()
                        onRename()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "\uD83D\uDCCB  ${
                                stringResource(
                                    R.string.copy_config
                                )
                            }",
                            color = C.TextPrimary
                        )
                    },
                    onClick = onCopyConfig
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "\uD83D\uDCE4  ${
                                stringResource(
                                    R.string.share
                                )
                            }",
                            color = C.TextPrimary
                        )
                    },
                    onClick = onShare
                )

                Divider(
                    color = C.Border
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "\uD83D\uDDD1  ${
                                stringResource(
                                    R.string.delete
                                )
                            }",
                            color = C.Red
                        )
                    },
                    onClick = {
                        onMenuDismiss()
                        onDelete()
                    }
                )
            }
        }
    }
}