package com.emt.app.model

import androidx.annotation.Keep

@Keep
data class Employee(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val department: String = "",
    val joiningDate: String = "",
    val email: String = "",
    val contact: String = "",
    val profilePicUri: String = "",
    val isActive: Boolean = true
)
