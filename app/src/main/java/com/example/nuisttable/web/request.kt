package com.example.nuisttable.web

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


private val httpClient = OkHttpClient()

//获取学年学期接口
val termApi = Api(
    name = "获取学年学期",
    path = "/jwapp/sys/wdkb/modules/jshkcb/dqxnxq.do",
    params = emptyMap()
)

//获取学期开始日期接口
fun beginDateApi(XN: String, XQ: String) = Api(
    name = "获取学期开始日期",
    path = "/jwapp/sys/wdkb/modules/jshkcb/cxjcs.do",
    params = mapOf("XN" to XN, "XQ" to XQ)
)

//获取已排课全年课表接口
fun scheduleApi(term : String) = Api(
    name = "获取已排课课程",
    path = "/jwapp/sys/wdkb/modules/xskcb/cxxszhxqkb.do",
    params = mapOf("XNXQDM" to term) //待获取term
)

//获取未排课课程接口
fun unplacedApi(term : String, xh : String) = Api(
    name = "获取未排课课程",
    path = "/jwapp/sys/wdkb/modules/xskcb/xswpkc.do",
    params = mapOf("XH" to xh, "XNXQDM" to term) //待获取term,xh
)

//发起请求
suspend fun requestData(api: Api, cookie: String): String = withContext(Dispatchers.IO) {
    try {
        val request = Request.Builder()
            .url(api.url)
            .post(api.formBody)
            .addHeader("Cookie", cookie)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}")
            }
            response.body.string()
        }
    } catch (e: Exception) {
        throw IllegalStateException("请求\"${api.name}\"失败", e)
    }
}