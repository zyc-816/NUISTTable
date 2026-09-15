package com.example.nuisttable.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuisttable.R
import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.storage.getTimetableCache
import com.example.nuisttable.ui.theme.CourseCardColorPreset
import com.example.nuisttable.ui.theme.appliedDarkTheme
import com.example.nuisttable.ui.theme.courseCardColorOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val weekHeaders = listOf("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
private val timeHeaders = listOf(
    "",
    "第一大节\n08:00-09:40",
    "第二大节\n10:10-11:50",
    "第三大节\n13:45-15:25",
    "第四大节\n15:55-17:35",
    "第五大节\n18:45-20:25",
    "第六大节\n20:35-21:20"
)
private val headerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd")
private const val totalWeeks = 20

data class TimetableSlot(
    val weekday: Int,
    val period: Int,
    val address: String = ""
)
data class TimetableCell(
    val className: String = "",
    val teacherName: String = "",
    val weekDay: Int = 0,
    val time: Int = 0,
    val addr: String = "",
    val backgroundPreset: CourseCardColorPreset? = null
)

data class TimetableDetail(
    val cell: TimetableCell,
    val slot: TimetableSlot
)

private fun parseScheduleCells(
    scheduleList: List<CxxszhxqkbContent>?): List<TimetableCell> {
    val cellList = mutableListOf<TimetableCell>()
    scheduleList?.withIndex()?.forEach { (_index, data) ->
        val index = _index % 15
        val cell = TimetableCell(
            className = data.KCM,
            teacherName = data.SKJS,
            weekDay = data.SKXQ,
            time = data.JSJC.toInt() / 2,
            addr = data.JASMC,
            backgroundPreset = CourseCardColorPreset.entries[index]
        )
        cellList.add(cell)
    }
    return cellList
}

private fun weekdayHeaderText(
    startDateText: String?,
    weekIndex: Int,
    weekday: Int
): String {
    val weekdayText = weekHeaders.getOrElse(weekday) { "" }
    val startDate = runCatching { startDateText?.let(LocalDate::parse) }.getOrNull()
        ?: return weekdayText
    val currentDate = startDate.plusDays(((weekIndex - 1) * 7L) + (weekday - 1).toLong())
    return "$weekdayText\n${currentDate.format(headerDateFormatter)}"
}

private fun resolveEffectiveDate(startDateText: String?): LocalDate? {
    val startDate = runCatching { startDateText?.let(LocalDate::parse) }.getOrNull()
        ?: return null
    val today = LocalDate.now()
    val endDate = startDate.plusDays(totalWeeks * 7L - 1)
    return when {
        today.isBefore(startDate) -> startDate
        today.isAfter(endDate) -> endDate
        else -> today
    }
}

private fun resolveInitialWeek(startDateText: String?): Int {
    val effectiveDate = resolveEffectiveDate(startDateText) ?: return 1
    val startDate = LocalDate.parse(startDateText)
    val daysFromStart = ChronoUnit.DAYS.between(startDate, effectiveDate)
    return (daysFromStart / 7L).toInt() + 1
}

private fun resolveHighlightWeekday(startDateText: String?, weekIndex: Int): Int? {
    val effectiveDate = resolveEffectiveDate(startDateText) ?: return null
    val startDate = LocalDate.parse(startDateText)
    val daysFromStart = ChronoUnit.DAYS.between(startDate, effectiveDate)
    val currentWeek = (daysFromStart / 7L).toInt() + 1
    if (weekIndex != currentWeek) {
        return null
    }
    return (daysFromStart % 7L).toInt() + 1
}

@Composable
private fun TimetableDetailDialog(
    detail: TimetableDetail,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = detail.cell.className,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "教师",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = detail.cell.teacherName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "地点",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = detail.slot.address,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "关闭")
            }
        }
    )
}

@Composable
private fun TimetableCard(
    cell: TimetableCell,
    slot: TimetableSlot,
    modifier: Modifier = Modifier,
    textFontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit = {}
) {
    val compactLineHeight = (textFontSize.value * 1.05f).coerceAtLeast(7.5f).sp
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = cell.backgroundPreset?.let { courseCardColorOf(it) }
                ?: MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cell.className,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = textFontSize,
                    lineHeight = compactLineHeight
                )
            )
            Text(
                text = slot.address,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = textFontSize,
                    lineHeight = compactLineHeight
                )
            )
        }
    }
}
@Composable
fun Timetable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val timetableCache by getTimetableCache(context).collectAsState(initial = null)
    val XNXQDM: String? = timetableCache?.XNXQDM
    val XQKSRQ: String? = timetableCache?.XQKSRQ
    val scheduleList = timetableCache?.scheduleList
    var selectedDetail by rememberSaveable { mutableStateOf<TimetableDetail?>(null) }
    val initialWeek = resolveInitialWeek(XQKSRQ)
    var weekIndex by rememberSaveable { mutableIntStateOf(initialWeek) }
    val timetableIdentity = listOf(XNXQDM, XQKSRQ, scheduleList?.size).joinToString("|")

    LaunchedEffect(timetableIdentity) {
        if (XQKSRQ != null) {
            weekIndex = resolveInitialWeek(XQKSRQ)
        }
    }

    val highlightWeekday = resolveHighlightWeekday(XQKSRQ, weekIndex)
    if (timetableCache == null) {
        Text(
            modifier = modifier.padding(16.dp),
            text = "暂未获取课表信息"
        )
        return
    }

    selectedDetail?.let { detail ->
        TimetableDetailDialog(
            detail = detail,
            onDismiss = { selectedDetail = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "学期：",
                        modifier = Modifier.wrapContentHeight(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = XNXQDM ?: "--",
                        modifier = Modifier.wrapContentHeight(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { weekIndex = when(weekIndex) {
                            1 -> 20
                            else -> weekIndex - 1
                        } }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "上一周"
                            )
                        }
                        Column(
                            modifier = Modifier.wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {

                            Text(
                                text = "第${weekIndex}周",
                                modifier = Modifier.wrapContentHeight(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { weekIndex = weekIndex%20 + 1 }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = "下一周"
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            tonalElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                val cellSpacing = 4.dp
                val totalHorizontalSpacing = cellSpacing * 7
                val totalVerticalSpacing = cellSpacing * 6
                val cellWidth = (maxWidth - totalHorizontalSpacing) / 8
                val gridHeight = maxWidth / 8 * 7
                val cellHeight = ((gridHeight - totalVerticalSpacing) / 7).coerceIn(64.dp, 96.dp)
                val headerFontSize = (cellWidth.value * 0.22f).coerceIn(10f, 14f).sp
                val timeFontSize = (cellWidth.value * 0.15f).coerceIn(7f, 10f).sp
                val cardTextFontSize = (cellHeight.value * 0.12f).coerceIn(6.5f, 10f).sp

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val highlightHeaderColor = MaterialTheme.colorScheme.secondaryContainer
                    val highlightCellColor = if (appliedDarkTheme) {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                    val headerCellColor = MaterialTheme.colorScheme.surfaceContainerLow
                    val timeColumnColor = MaterialTheme.colorScheme.surfaceContainerLow
                    val defaultCellColor = MaterialTheme.colorScheme.surface
                    val cellShape = RoundedCornerShape(8.dp)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(cellSpacing)
                    ) {
                        repeat(7) { rowIndex ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(cellSpacing)
                            ) {
                                repeat(8) { columnIndex ->
                                    val cellText = when {
                                        rowIndex == 0 && columnIndex != 0 -> weekdayHeaderText(
                                            startDateText = XQKSRQ,
                                            weekIndex = weekIndex,
                                            weekday = columnIndex
                                        )
                                        columnIndex == 0 -> timeHeaders[rowIndex]
                                        else -> ""
                                    }
                                    val fontSize = when {
                                        rowIndex == 0 && columnIndex != 0 -> headerFontSize
                                        columnIndex == 0 -> timeFontSize
                                        else -> headerFontSize
                                    }
                                    val cellColor = when {
                                        rowIndex == 0 && columnIndex == 0 -> timeColumnColor
                                        rowIndex == 0 && columnIndex == highlightWeekday -> highlightHeaderColor
                                        rowIndex == 0 -> headerCellColor
                                        columnIndex == 0 -> timeColumnColor
                                        columnIndex == highlightWeekday -> highlightCellColor
                                        else -> defaultCellColor
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .width(cellWidth)
                                            .height(cellHeight),
                                        shape = cellShape,
                                        color = cellColor,
                                        tonalElevation = if (rowIndex == 0 || columnIndex == 0) 1.dp else 0.dp
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cellText,
                                                textAlign = TextAlign.Center,
                                                color = if (rowIndex == 0 || columnIndex == 0) {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                },
                                                style = if (rowIndex == 0 && columnIndex != 0) {
                                                    MaterialTheme.typography.labelMedium.copy(fontSize = fontSize)
                                                } else {
                                                    MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val timetableCells = parseScheduleCells(scheduleList?.get(weekIndex-1))
                    timetableCells.forEach { cell ->
                        val slot = TimetableSlot(cell.weekDay, cell.time, cell.addr)
                        if (slot.weekday in 1..7 && slot.period in 1..6) {
                            TimetableCard(
                                cell = cell,
                                slot = slot,
                                modifier = Modifier
                                    .offset(
                                        x = (cellWidth + cellSpacing) * slot.weekday,
                                        y = (cellHeight + cellSpacing) * slot.period
                                    )
                                    .width(cellWidth)
                                    .height(cellHeight),
                                textFontSize = cardTextFontSize,
                                onClick = {
                                    selectedDetail = TimetableDetail(cell = cell, slot = slot)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
