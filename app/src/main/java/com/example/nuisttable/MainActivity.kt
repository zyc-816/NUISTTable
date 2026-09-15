package com.example.nuisttable

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.data.XswpkcContent
import com.example.nuisttable.json.getSchedule
import com.example.nuisttable.json.getUnplaced
import com.example.nuisttable.json.getXNXQDM
import com.example.nuisttable.json.getXQKSRQ
import com.example.nuisttable.storage.data.TimetableCache
import com.example.nuisttable.storage.getIsDarkMode
import com.example.nuisttable.storage.saveIsDarkMode
import com.example.nuisttable.storage.saveTimetableCache
import com.example.nuisttable.ui.asset.CreateWebView
import com.example.nuisttable.ui.asset.FetchingDialog
import com.example.nuisttable.ui.asset.Timetable
import com.example.nuisttable.ui.theme.NUISTTableTheme
import com.example.nuisttable.web.beginDateApi
import com.example.nuisttable.web.requestData
import com.example.nuisttable.web.termApi
import com.example.nuisttable.web.scheduleApi
import com.example.nuisttable.web.unplacedApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NUISTTableApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreenSizes
@Composable
fun NUISTTableApp() {
    val systemColorMode = isSystemInDarkTheme()
    var showWebPage by rememberSaveable { mutableStateOf(false) }
    var cookieStr by rememberSaveable { mutableStateOf("") }
    var showFetchingDialog by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val applicationContext = LocalContext.current.applicationContext
    val isDarkMode by getIsDarkMode(applicationContext, systemColorMode)
        .collectAsState(initial = systemColorMode)
    var termJson: String
    var XQKSRQJson: String
    var scheduleJson: String
//    var unplacedJson: String
//    var XH: String
    var XNXQDM: String
    var XQKSRQ: String
    var scheduleList: List<List<CxxszhxqkbContent>>?

    NUISTTableTheme(
        darkTheme = isDarkMode
    ) {
        if (showWebPage) {
            CreateWebView(
                url = "https://jwxt.nuist.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh#/xskcb",
                onClose = { showWebPage = false },
                onLoginSuccess = { cookie,_XH ->
                    cookieStr = cookie
//                    XH = _XH
                    showFetchingDialog = true
                    coroutineScope.launch {
                        try {
                            //获取学年学期代码
                            termJson = requestData(termApi, cookie)
                            XNXQDM = getXNXQDM(termJson)
                            //获取学期开始日期
                            XQKSRQJson = requestData(beginDateApi(XNXQDM.dropLast(2), XNXQDM.takeLast(1)), cookie)
                            XQKSRQ = getXQKSRQ(XQKSRQJson)
                            //已排课课程信息（遍历1-20周，按周合并）
                            val scheduleList = mutableListOf<List<CxxszhxqkbContent>>()
                            for (week in 1..20) {
                                val weekJson = requestData(scheduleApi(XNXQDM, week.toString()), cookie)
//                                Log.d("Data", "week $week: $weekJson")
                                val weekData = getSchedule(weekJson)
                                scheduleList.add(weekData)
                            }
                            //未排课课程信息
//                            unplacedJson = requestData(unplacedApi(XNXQDM, XH), cookie)
//                            unplacedList = getUnplaced(unplacedJson)
                            //课表数据
                            val timetableCache = TimetableCache(XNXQDM, XQKSRQ, scheduleList)
                            saveTimetableCache(applicationContext, timetableCache)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.d("Data", "get data failed", e)
                        } finally {
                            showFetchingDialog = false
                            showWebPage = false
                        }
                    }
                }
            )
            if (showFetchingDialog) {
                FetchingDialog()
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier.wrapContentHeight(),
                        title = {
                            Text(text = "课表")
                        },
                        windowInsets = WindowInsets.systemBars,
                        navigationIcon = {
                            Icon(
                                modifier = Modifier.padding(10.dp),
                                painter = painterResource(R.drawable.settings_icon),
                                contentDescription = "设置"
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    saveIsDarkMode(applicationContext, !isDarkMode)
                                }
                            }) {
                                Icon(
                                    modifier = Modifier.padding(10.dp),
                                    painter = if (isDarkMode) painterResource(R.drawable.dark_mode_icon)
                                    else painterResource(R.drawable.light_mode_icon),
                                    contentDescription = if (isDarkMode) "浅色模式" else "暗色模式"
                                )
                            }

                            IconButton(onClick = { showWebPage = true }) {
                                Icon(
                                    modifier = Modifier.padding(10.dp),
                                    painter = painterResource(R.drawable.get_table_icon),
                                    contentDescription = "获取课表"
                                )
                            }
                        }
                    )
                },
                bottomBar = {

                }
            ) { innerPadding ->
                Timetable(modifier = Modifier.padding(innerPadding))
            }

        }
    }
}