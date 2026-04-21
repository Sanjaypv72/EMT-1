package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Attendance
import com.emt.app.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class OpState {
    object Idle    : OpState()
    object Loading : OpState()
    object Success : OpState()
    data class Error(val message: String) : OpState()
}

class AttendanceViewModel : ViewModel() {

    private val repository = AttendanceRepository()

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList

    private val _opState = MutableStateFlow<OpState>(OpState.Idle)
    val opState: StateFlow<OpState> = _opState

    private val _selectedDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    val selectedDate: StateFlow<String> = _selectedDate

    init {
        loadForDate(_selectedDate.value)
    }

    // ✅ FIXED: calls getAttendanceByDate (matches repository exactly)
    fun loadForDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            repository.getAttendanceByDate(date)   // ← correct name!
                .collect { list -> _attendanceList.value = list }
        }
    }

    fun setDate(date: String) {
        loadForDate(date)
    }

    fun markAttendance(
        employeeId: String,
        employeeName: String,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val attendance = Attendance(
                employeeId   = employeeId,
                employeeName = employeeName,
                date         = _selectedDate.value,
                status       = status,
                notes        = notes,
                checkInTime  = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            )
            val result = repository.markAttendance(attendance)
            _opState.value = if (result.isSuccess) OpState.Success
            else OpState.Error(result.exceptionOrNull()?.message ?: "Failed")
        }
    }

    fun deleteAttendance(id: String) {
        viewModelScope.launch {
            repository.deleteAttendance(id)
        }
    }

    fun resetOpState() {
        _opState.value = OpState.Idle
    }
}