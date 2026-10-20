package com.v2ray.ang.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.wrapContentSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.dto.LocateTarget
import com.v2ray.ang.dto.entities.ProfileItem
import com.v2ray.ang.ui.compose.ItemDivider
import com.v2ray.ang.ui.compose.ReorderableGridItem
import com.v2ray.ang.ui.compose.ReorderableListItem
import com.v2ray.ang.ui.compose.colorConfigType
import com.v2ray.ang.ui.compose.colorPing
import com.v2ray.ang.ui.compose.colorPingRed
import com.v2ray.ang.ui.compose.verticalScrollbar
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.abs

@Composable
fun GroupPagerPage(
    groupId: String,
    mainViewModel: MainViewModel,
    selectedGuid: String?,
    locateTarget: LocateTarget?,
    doubleColumnDisplay: Boolean,
    searchQuery: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onShareServer: (String, ProfileItem) -> Unit,
    onTestServerPing: (String) -> Unit,
    onRemoveServer: (String, String) -> Unit,
    serverCategory: V2ElevenServerCategory = V2ElevenServerCategory.ALL,
    contentPadding: PaddingValues
) {
    val groupStateFlow = remember(groupId) {
        mainViewModel.serverGroupState(groupId)
    }
    val groupState by groupStateFlow.collectAsStateWithLifecycle()
    val visibleRows = remember(groupState.rows, serverCategory) {
        groupState.rows.filter { serverCategory.matches(it.profile) }
    }
    val canReorder = groupId.isNotEmpty() && searchQuery.isEmpty() &&
            serverCategory == V2ElevenServerCategory.ALL
    val actions = remember(
        onSelectServer,
        onEditServer,
        onShareServer,
        onTestServerPing,
        onRemoveServer,
    ) {
        ServerRowActions(
            select = onSelectServer,
            edit = onEditServer,
            share = onShareServer,
            testPing = onTestServerPing,
            remove = onRemoveServer,
        )
    }
    ServerListPage(
        rows = visibleRows,
        selectedGuid = selectedGuid,
        locateTarget = locateTarget?.takeIf { it.groupId == groupId },
        canReorder = canReorder,
        doubleColumnDisplay = doubleColumnDisplay,
        groupId = groupId,
        lazyListStates = lazyListStates,
        lazyGridStates = lazyGridStates,
        actions = actions,
        onLocateHandled = { mainViewModel.onAction(MainAction.LocateHandled) },
        onMoveServer = { fromIndex, toIndex ->
            mainViewModel.moveServer(groupId, fromIndex, toIndex)
        },
        contentPadding = contentPadding
    )
}

private class ServerRowActions(
    val select: (String) -> Unit,
    val edit: (String, ProfileItem) -> Unit,
    val share: (String, ProfileItem) -> Unit,
    val testPing: (String) -> Unit,
    val remove: (String, String) -> Unit,
)

@Composable
private fun ServerListPage(
    rows: List<ServerRowUiModel>,
    selectedGuid: String?,
    locateTarget: LocateTarget?,
    canReorder: Boolean,
    doubleColumnDisplay: Boolean,
    groupId: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    actions: ServerRowActions,
    onLocateHandled: () -> Unit,
    onMoveServer: (Int, Int) -> Unit,
    contentPadding: PaddingValues
) {
    if (doubleColumnDisplay) {
        val gridState = remember(groupId) {
            lazyGridStates.getOrPut(groupId) { LazyGridState() }
        }
        val reorderableGridState = if (canReorder) {
            rememberReorderableLazyGridState(gridState) { from, to ->
                onMoveServer(from.index, to.index)
            }
        } else null

        LocateTargetEffect(locateTarget, rows, gridState, onLocateHandled)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollbar(gridState),
            contentPadding = contentPadding
        ) {
            itemsIndexed(items = rows, key = { _, item -> item.guid }) { _, row ->
                val content: @Composable () -> Unit = {
                    ServerItemColumn(
                        row = row,
                        isSelected = row.guid == selectedGuid,
                        doubleColumnDisplay = true,
                        actions = actions
                    )
                }
                if (canReorder && reorderableGridState != null) {
                    ReorderableItem(
                        reorderableGridState,
                        key = row.guid
                    ) { isDragging ->
                        ReorderableGridItem(
                            scope = this,
                            isDragging = isDragging
                        ) { content() }
                    }
                } else {
                    content()
                }
            }
        }
    } else {
        val listState = remember(groupId) {
            lazyListStates.getOrPut(groupId) { LazyListState() }
        }
        val reorderableState = if (canReorder) {
            rememberReorderableLazyListState(listState) { from, to ->
                onMoveServer(from.index, to.index)
            }
        } else null

        LocateTargetEffect(locateTarget, rows, listState, onLocateHandled)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollbar(listState),
            contentPadding = contentPadding
        ) {
            itemsIndexed(items = rows, key = { _, item -> item.guid }) { _, row ->
                if (canReorder && reorderableState != null) {
                    ReorderableItem(
                        reorderableState,
                        key = row.guid
                    ) { isDragging ->
                        ReorderableListItem(
                            scope = this,
                            isDragging = isDragging
                        ) {
                            ServerItemRow(
                                row = row,
                                isSelected = row.guid == selectedGuid,
                                actions = actions
                            )
                        }
                    }
                } else {
                    ServerItemRow(
                        row = row,
                        isSelected = row.guid == selectedGuid,
                        actions = actions
                    )
                }
            }
        }
    }
}

@Composable
private fun LocateTargetEffect(
    target: LocateTarget?,
    rows: List<ServerRowUiModel>,
    state: LazyListState,
    onHandled: () -> Unit,
) {
    if (target == null) return
    LaunchedEffect(target, rows) {
        val index = rows.indexOfFirst { it.guid == target.serverGuid }
        if (index < 0) return@LaunchedEffect
        state.scrollToItem(index, -state.layoutInfo.viewportSize.height / 3)
        onHandled()
    }
}

@Composable
private fun LocateTargetEffect(
    target: LocateTarget?,
    rows: List<ServerRowUiModel>,
    state: LazyGridState,
    onHandled: () -> Unit,
) {
    if (target == null) return
    LaunchedEffect(target, rows) {
        val index = rows.indexOfFirst { it.guid == target.serverGuid }
        if (index < 0) return@LaunchedEffect
        state.scrollToItem(index, -state.layoutInfo.viewportSize.height / 3)
        onHandled()
    }
}

@Composable
private fun ServerItemRow(
    row: ServerRowUiModel,
    isSelected: Boolean,
    actions: ServerRowActions
) {
    ServerListItem(
        row = row,
        isSelected = isSelected,
        doubleColumnDisplay = false,
        actions = actions,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

@Composable
private fun ServerItemColumn(
    row: ServerRowUiModel,
    isSelected: Boolean,
    doubleColumnDisplay: Boolean,
    actions: ServerRowActions
) {
    ServerListItem(
        row = row,
        isSelected = isSelected,
        doubleColumnDisplay = doubleColumnDisplay,
        actions = actions,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp)
    )
}

@Composable
private fun ServerListItem(
    row: ServerRowUiModel,
    isSelected: Boolean,
    doubleColumnDisplay: Boolean,
    actions: ServerRowActions,
    modifier: Modifier = Modifier
) {
    val identity = remember(row.guid, row.remarks) { serverVisualIdentity(row.remarks) }
    val statistics = remember(row.guid, row.statistics) { stripServerFlagEmojis(row.statistics) }
    val pingText = when {
        row.testDelayMillis < 0L -> "TIMEOUT"
        row.testDelayMillis == 0L -> "— ms"
        else -> "${row.testDelayMillis} ms"
    }
    val pingColor = when {
        row.testDelayMillis < 0L -> Color(0xFFFF5964)
        row.testDelayMillis > 0L -> Color(0xFF55D887)
        else -> Color(0xFFAAA6B0)
    }
    val selectedDescription = if (isSelected) stringResource(R.string.acc_selected_server) else null
    var menuExpanded by remember(row.guid) { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(27.dp),
        color = if (isSelected) Color(0xD72B1820) else Color(0xC91A191F),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFFF8295).copy(alpha = 0.42f)
            else Color.White.copy(alpha = 0.105f)
        ),
        tonalElevation = 0.dp,
        shadowElevation = if (isSelected) 10.dp else 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    if (selectedDescription != null) stateDescription = selectedDescription
                }
                .clickable { actions.select(row.guid) }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            V2ElevenFlagMedallion(flag = identity.flagEmoji, modifier = Modifier.size(49.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = identity.displayName,
                        modifier = Modifier.weight(1f, fill = false),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF8F3F6),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF7C2438).copy(alpha = 0.72f),
                            border = BorderStroke(1.dp, Color(0xFFFFAAB5).copy(alpha = 0.18f))
                        ) {
                            Text(
                                stringResource(R.string.acc_selected_server),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                color = Color(0xFFFFB5BE),
                                maxLines = 1
                            )
                        }
                    }
                }
                Text(
                    text = statistics.ifBlank { row.typeDescription },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAAA6B0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.045f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.055f))
                ) {
                    Text(
                        text = row.typeDescription,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFD2BAC2),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = pingText,
                    color = pingColor,
                    fontSize = if (doubleColumnDisplay) 15.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = stringResource(R.string.v2eleven_home_latency),
                    color = Color(0xFF928D98),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert_24dp),
                        contentDescription = stringResource(R.string.acc_more),
                        tint = Color(0xFFDCD4DB),
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(24.dp))
                        .width(232.dp),
                    containerColor = Color(0xF21A191F),
                    tonalElevation = 0.dp,
                    shadowElevation = 24.dp,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.v2eleven_menu_test_ping), color = Color(0xFFF8F3F6)) },
                        onClick = { menuExpanded = false; actions.testPing(row.guid) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.title_configuration_share), color = Color(0xFFF8F3F6)) },
                        onClick = { menuExpanded = false; actions.share(row.guid, row.profile) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_edit), color = Color(0xFFF8F3F6)) },
                        onClick = { menuExpanded = false; actions.edit(row.guid, row.profile) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_delete), color = Color(0xFFFF8798)) },
                        onClick = { menuExpanded = false; actions.remove(row.guid, row.remarks) }
                    )
                }
            }
        }
    }
}

internal suspend fun PagerState.navigateToPageOptimized(
    targetPage: Int,
    animateAdjacentPage: Boolean = true
) {
    if (pageCount <= 0) return
    val target = targetPage.coerceIn(0, pageCount - 1)
    val current = settledPage.coerceIn(0, pageCount - 1)
    if (target == current) return

    if (abs(target - current) == 1 && animateAdjacentPage) {
        animateScrollToPage(target)
    } else {
        scrollToPage(target)
    }
}
