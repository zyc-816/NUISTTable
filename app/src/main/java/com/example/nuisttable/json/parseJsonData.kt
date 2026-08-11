package com.example.nuisttable.json

import com.example.nuisttable.ui.asset.TimetableSlot

fun getEvenSlot(data: String): List<TimetableSlot> {
    val strList: List<String> = data.split(",")
    val res: MutableList<TimetableSlot> = mutableListOf()
    strList.forEach { str ->
        val (week, weekDay, period, addr) = str.split(" ")
        if(week.contains("单")) {}
        else {
            val slot = TimetableSlot(
                weekday = when (weekDay) {
                    "星期一" -> 1
                    "星期二" -> 2
                    "星期三" -> 3
                    "星期四" -> 4
                    "星期五" -> 5
                    "星期六" -> 6
                    "星期日" -> 7
                    else -> 0
                },
                period = when(period) {
                    "第1节-第2节" -> 1
                    "第3节-第4节" -> 2
                    "第5节-第6节" -> 3
                    "第7节-第8节" -> 4
                    "第9节-第10节" -> 5
                    "第11节-第12节" -> 6
                    else -> 0
                },
                address = addr
            )
            res.add(slot)
        }
    }
    return res
}

fun getOddSlot(data: String): List<TimetableSlot> {
    val strList: List<String> = data.split(",")
    val res: MutableList<TimetableSlot> = mutableListOf()
    strList.forEach { str ->
        val (week, weekDay, period, addr) = str.split(" ")
        if(week.contains("双")) {}
        else {
            val slot = TimetableSlot(
                weekday = when (weekDay) {
                    "星期一" -> 1
                    "星期二" -> 2
                    "星期三" -> 3
                    "星期四" -> 4
                    "星期五" -> 5
                    "星期六" -> 6
                    "星期日" -> 7
                    else -> 0
                },
                period = when(period) {
                    "第1节-第2节" -> 1
                    "第3节-第4节" -> 2
                    "第5节-第6节" -> 3
                    "第7节-第8节" -> 4
                    "第9节-第10节" -> 5
                    "第11节-第12节" -> 6
                    else -> 0
                },
                address = addr
            )
            res.add(slot)
        }
    }
    return res
}