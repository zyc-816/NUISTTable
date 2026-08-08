package com.example.nuisttable.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuisttable.R
import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.data.XswpkcContent
import com.example.nuisttable.storage.getTimetableCache
import com.example.nuisttable.ui.theme.CourseCardColorPreset
import com.example.nuisttable.ui.theme.courseCardColorOf

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

data class TimetableCell(
    val className: String = "",
    val teacherName: String = "",
    val address: String = "",
    val evenWeek: Map<Int, Int> = emptyMap(),
    val oddWeek: Map<Int, Int> = emptyMap(),
    val backgroundPreset: CourseCardColorPreset? = null
)

private fun parseTimetableCells(
    scheduleList: List<CxxszhxqkbContent>?,
    unplacedList: List<XswpkcContent>?): List<TimetableCell> {

    return emptyList()
}

private fun TimetableCell.currentWeekSlots(weekIndex: Int): Map<Int, Int> {
    return if (weekIndex % 2 == 0) evenWeek else oddWeek
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

@Composable
private fun TimetableCard(
    cell: TimetableCell,
    modifier: Modifier = Modifier,
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
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cell.className,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = cell.teacherName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = cell.address,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TableTitle(XNXQDM: String?, _weekIndex: Int) {
    var weekIndex by rememberSaveable { mutableStateOf(_weekIndex) }
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
            IconButton(onClick = {}) {
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
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.padding(10.dp),
                    painter = painterResource(R.drawable.arrow_forward),
                    contentDescription = "下一周"
                )
            }
        }
    }
}

@Composable
fun Timetable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val timetableCache by getTimetableCache(context).collectAsState(initial = null)
    val XNXQDM: String? = timetableCache?.XNXQDM
    val scheduleList = timetableCache?.scheduleList
    val unplacedList = timetableCache?.unplacedList
    val timetableCells = parseTimetableCells(scheduleList, unplacedList)
    var weekIndex = 1
    if (timetableCache == null) {
        Text(
            modifier = modifier.padding(16.dp),
            text = "暂未获取课表信息"
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TableTitle(XNXQDM, weekIndex)

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val cellWidth = maxWidth / 8
            val cellHeight = (cellWidth * 0.9f).coerceIn(56.dp, 72.dp)
            val headerFontSize = (cellWidth.value * 0.22f).coerceIn(10f, 14f).sp
            val timeFontSize = (cellWidth.value * 0.15f).coerceIn(7f, 10f).sp

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(7) { rowIndex ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            repeat(8) { columnIndex ->
                                val cellText = when {
                                    rowIndex == 0 && columnIndex != 0 -> weekHeaders[columnIndex]
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
                                        .background(MaterialTheme.colorScheme.surface)
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
                    cell.currentWeekSlots(weekIndex).forEach { (weekday, period) ->
                        if (weekday in 1..7 && period in 1..6) {
                            TimetableCard(
                                cell = cell,
                                modifier = Modifier
                                    .offset(
                                        x = cellWidth * weekday,
                                        y = cellHeight * period
                                    )
                                    .width(cellWidth)
                                    .height(cellHeight),
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
