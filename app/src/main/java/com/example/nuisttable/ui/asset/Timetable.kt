package com.example.nuisttable.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nuisttable.R
import com.example.nuisttable.storage.getTimetableCache

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
    val KCM: String = "",
    val SKJS: String = "",
    val YPSJDD: String = "",
    val SKZC: String = "",
    val backgroundColor: Color = Color.Transparent
)

@Composable
fun Timetable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val timetableCache by getTimetableCache(context).collectAsState(initial = null)
    val XNXQDM = timetableCache?.XNXQDM
    val scheduleList = timetableCache?.scheduleList
    val unplacedList = timetableCache?.unplacedList
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

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .border(1.dp, Color.Gray)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cellText,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
