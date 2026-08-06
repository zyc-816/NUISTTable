package com.example.nuisttable.json

import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.data.CxxszhxqkbData
import com.example.nuisttable.json.data.Respose
import com.example.nuisttable.json.data.XswpkcContent
import com.example.nuisttable.json.data.XswpkcData
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
fun getSchedule(json: String): List<CxxszhxqkbContent> {
    val jsonObj  = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val result = jsonObj.decodeFromString<Respose<CxxszhxqkbData>>(json)
    return result.datas.cxxszhxqkb.rows
}

fun getUnplaced(json : String): List<XswpkcContent> {
    val jsonObj  = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val result = jsonObj.decodeFromString<Respose<XswpkcData>>(json)
    return result.datas.xswpkc.rows
}