package com.example.nuisttable.json

import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.data.CxxszhxqkbData
import com.example.nuisttable.json.data.Respose
import kotlinx.serialization.json.Json
import org.json.JSONObject

fun getXNXQDM(json : String): String {
    val obj = JSONObject(json)
    return obj
        .getJSONObject("datas")
        .getJSONObject("dqxnxq")
        .getJSONArray("rows")
        .getJSONObject(0)
        .optString("DM")
}

fun getXQKSRQ(json: String): String {
    val obj = JSONObject(json)
    return obj
        .getJSONObject("datas")
        .getJSONObject("cxjcs")
        .getJSONArray("rows")
        .getJSONObject(0)
        .optString("XQKSRQ")
        .split(" ")[0]
}

fun getSchedule(json: String): List<CxxszhxqkbContent> {
    val jsonObj  = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val result = jsonObj.decodeFromString<Respose<CxxszhxqkbData>>(json)
    return result.datas.cxxszhxqkb.rows
}

