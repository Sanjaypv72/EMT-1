package com.emt.app.model

import androidx.annotation.Keep

@Keep
data class Attendance(
    val id           : String = "",
    val employeeId   : String = "",
    val employeeName : String = "",
    val date         : String = "",
    val checkInTime  : String = "",
    val checkOutTime : String = "",
    val status       : String = "Present",
    val hoursWorked  : Double = 0.0,
    val notes        : String = "",
    val markedBy     : String = ""
)