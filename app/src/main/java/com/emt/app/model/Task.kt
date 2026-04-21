package com.emt.app.model

data class Task(
    val id: String = "",
    val employeeId: String = "",
    val description: String = "",
    val status: String = "Pending", // Pending, In Progress, Completed, Reviewed
    val deadline: String = "",
    val priority: String = "Medium", // Low, Medium, High
    val assignedDate: String = ""
)