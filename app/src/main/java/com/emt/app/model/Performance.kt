package com.emt.app.model

import androidx.annotation.Keep

@Keep
data class Performance(
    val id                : String = "",
    val employeeId        : String = "",
    val employeeName      : String = "",
    val month             : String = "",
    val qualityScore      : Float  = 0f,
    val timelinessScore   : Float  = 0f,
    val attendanceScore   : Float  = 0f,
    val communicationScore: Float  = 0f,
    val innovationScore   : Float  = 0f,
    val overallRating     : Float  = 0f,
    val remarks           : String = ""
)