package com.example.nuisttable.storage.data

import com.example.nuisttable.json.data.CxxszhxqkbContent
import com.example.nuisttable.json.data.XswpkcContent
import kotlinx.serialization.Serializable

@Serializable
data class TimetableCache(
    val XNXQDM: String,
    val XQKSRQ: String,
    val scheduleList: List<CxxszhxqkbContent>?,
    val unplacedList: List<XswpkcContent>?
)