package com.example.nuisttable.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuisttable.R
import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.getEvenSlot
import com.example.nuisttable.json.getOddSlot
import com.example.nuisttable.storage.getTimetableCache
import com.example.nuisttable.ui.theme.CourseCardColorPreset
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
    val weeks: String = "",
    val evenWeek: List<TimetableSlot> = emptyList(),
    val oddWeek: List<TimetableSlot> = emptyList(),
    val backgroundPreset: CourseCardColorPreset? = null
)

data class TimetableDetail(
    val cell: TimetableCell,
    val slot: TimetableSlot
)

private fun parseScheduleCells(
    scheduleList: List<CxxszhxqkbContent>?): List<TimetableCell> {
    var cellList: MutableList<TimetableCell> = mutableListOf<TimetableCell>()
    scheduleList?.withIndex()?.forEach { (index, data) ->
        var cell: TimetableCell = TimetableCell(
            className = data.KCM,
            teacherName = data.SKJS,
            weeks = data.SKZC,
            evenWeek = getEvenSlot(data.YPSJDD),
            oddWeek = getOddSlot(data.YPSJDD),
            backgroundPreset = CourseCardColorPreset.entries[index]
        )
        cellList.add(cell)
    }
    return cellList
}

private fun TimetableCell.currentWeekSlots(weekIndex: Int): List<TimetableSlot> {
    return if (weekIndex % 2 == 0) evenWeek else oddWeek
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

private fun Modifier.timetableCellBorder(
    rowIndex: Int,
    columnIndex: Int,
    borderColor: Color
): Modifier = drawBehind {
    val innerStroke = 2.dp.toPx()
    val outerStroke = 2.dp.toPx()
    val widthPx = size.width
    val heightPx = size.height

    drawLine(
        color = borderColor,
        start = Offset(0f, 0f),
        end = Offset(widthPx, 0f),
        strokeWidth = if (rowIndex == 0) outerStroke else innerStroke
    )
    drawLine(
        color = borderColor,
        start = Offset(0f, 0f),
        end = Offset(0f, heightPx),
        strokeWidth = if (columnIndex == 0) outerStroke else innerStroke
    )
    drawLine(
        color = borderColor,
        start = Offset(widthPx, 0f),
        end = Offset(widthPx, heightPx),
        strokeWidth = if (columnIndex == 7) outerStroke else innerStroke
    )
    drawLine(
        color = borderColor,
        start = Offset(0f, heightPx),
        end = Offset(widthPx, heightPx),
        strokeWidth = if (rowIndex == 6) outerStroke else innerStroke
    )
}

private fun weekdayDisplayText(weekday: Int): String {
    return weekHeaders.getOrElse(weekday) { "" }
}

private fun periodDisplayText(period: Int): String {
    return timeHeaders.getOrElse(period) { "" }
}

@Composable
private fun TimetableDetailDialog(
    detail: TimetableDetail,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = detail.cell.className)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "教师：${detail.cell.teacherName}")
                Text(text = "地点：${detail.slot.address}")
            }
        },
        confirmButton = {}
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
    Box(
        modifier = modifier
            .padding(2.dp)
            .background(
                cell.backgroundPreset?.let { courseCardColorOf(it) }
                    ?: MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cell.className,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = textFontSize)
            )
            Text(
                text = slot.address,
                textAlign = TextAlign.Center,
                maxLines = 3,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = textFontSize)
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
    val unplacedList = timetableCache?.unplacedList
    val timetableCells = parseScheduleCells(scheduleList)
    var selectedDetail by rememberSaveable { mutableStateOf<TimetableDetail?>(null) }
    val initialWeek = resolveInitialWeek(XQKSRQ)
    var weekIndex by rememberSaveable { mutableStateOf(initialWeek) }
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .heightIn(0.dp, 20.dp),
            text = "学期：${XNXQDM}",
            style = MaterialTheme.typography.titleSmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                IconButton(onClick = { weekIndex = when(weekIndex) {
                    1 -> 20
                    else -> weekIndex - 1
                } }) {
                    Icon(
                        modifier = Modifier.padding(10.dp),
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "上一周"
                    )
                }
                Box(
                    modifier = Modifier.height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "第${weekIndex}周"
                    )
                }
                IconButton(onClick = { weekIndex = weekIndex%20 + 1 }) {
                    Icon(
                        modifier = Modifier.padding(10.dp),
                        painter = painterResource(R.drawable.arrow_forward),
                        contentDescription = "下一周"
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val cellWidth = maxWidth / 8
            val cellHeight = (cellWidth * 1.1f).coerceIn(64.dp, 96.dp)
            val headerFontSize = (cellWidth.value * 0.22f).coerceIn(10f, 14f).sp
            val timeFontSize = (cellWidth.value * 0.15f).coerceIn(7f, 10f).sp
            val cardTextFontSize = (cellHeight.value * 0.12f).coerceIn(6.5f, 10f).sp

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                val highlightCellColor = MaterialTheme.colorScheme.primaryContainer
                val defaultCellColor = MaterialTheme.colorScheme.surface
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(7) { rowIndex ->
                        Row(modifier = Modifier.fillMaxWidth()) {
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

                                Box(
                                    modifier = Modifier
                                        .width(cellWidth)
                                        .height(cellHeight)
                                        .background(
                                            if (columnIndex != 0 && columnIndex == highlightWeekday) {
                                                highlightCellColor
                                            } else {
                                                defaultCellColor
                                            }
                                        )
                                        .timetableCellBorder(
                                            rowIndex = rowIndex,
                                            columnIndex = columnIndex,
                                            borderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cellText,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize)
                                    )
                                }
                            }
                        }
                    }
                }

                timetableCells.forEach { cell ->
                    if (cell.weeks.getOrNull(weekIndex - 1) == '1') {
                        cell.currentWeekSlots(weekIndex).forEach { slot ->
                            if (slot.weekday in 1..7 && slot.period in 1..6) {
                                TimetableCard(
                                    cell = cell,
                                    slot = slot,
                                    modifier = Modifier
                                        .offset(
                                            x = cellWidth * slot.weekday,
                                            y = cellHeight * slot.period
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
}
