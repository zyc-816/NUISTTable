package com.example.nuisttable.web

import okhttp3.FormBody
import kotlin.collections.component1
import kotlin.collections.component2


data class Api(val name : String,val path : String, var params : Map<String, String> = emptyMap()) {
    companion object {
        const val BASE_URL = "https://jwxt.nuist.edu.cn"
    }

    val url = BASE_URL + path
    val formBody = buildForm()

    fun buildForm() : FormBody {
        val builder = FormBody.Builder()
        params.forEach { (key, value) ->
            builder.add(key, value)
        }
        return builder.build()
    }
}
