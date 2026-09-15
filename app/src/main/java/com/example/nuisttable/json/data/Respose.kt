package com.example.nuisttable.json.data


import kotlinx.serialization.Serializable


@Serializable
data class Respose<T> (
    val datas: T
)

@Serializable
data class CxxszhxqkbData(
    val cxxszhxqkb: Cxxszhxqkb
)

@Serializable
data class XswpkcData(
    val xswpkc: Xswpkc
)

@Serializable
data class Cxxszhxqkb(
    val rows: List<CxxszhxqkbContent> = emptyList()
)

@Serializable
data class Xswpkc(
    val rows: List<XswpkcContent> = emptyList()
)

@Serializable
data class CxxszhxqkbContent(
    val KCM: String = "",    //课程名
    val SKJS: String = "",   //上课教师
    val SKXQ: Int = 0,         //星期几
    val KSJC: String = "",     // 开始节次
    val JSJC: String = "",     // 结束节次
    val JASMC: String = "",    // 教室名称
)

@Serializable
data class XswpkcContent(
    val KCM: String = "",   //课程名
    val SKJS: String = "",  //上课教师
    val KXH: String = ""    //教学班序号
)