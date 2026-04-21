package com.emt.app.model

data class Performance(
    val id: String = "",
    val employeeId: String = "",
    val date: String = "",
    val qualityScore: Float = 0f,
    val timelinessScore: Float = 0f,
    val attendanceScore: Float = 0f,
    val communicationScore: Float = 0f,
    val innovationScore: Float = 0f,
    val overallRating: Float = 0f,
    val remarks: String = ""
)