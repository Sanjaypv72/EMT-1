package com.emt.app.model

import androidx.annotation.Keep

@Keep
data class Task(
    val id          : String = "",
    val title       : String = "",
    val description : String = "",
    val employeeId  : String = "",
    val employeeName: String = "",
    val deadline    : String = "",
    val priority    : String = "Medium",
    val status      : String = "Pending",
    val assignedDate: String = ""
)