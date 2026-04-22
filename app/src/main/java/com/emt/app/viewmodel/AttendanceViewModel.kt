package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Attendance
import com.emt.app.repository.AttendanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel : ViewModel() {

    private val repo     = AttendanceRepository()
    private val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _selectedDate   = MutableStateFlow(todayStr)
    val selectedDate: StateFlow<String> = _selectedDate

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList

    private val _allAttendance  = MutableStateFlow<List<Attendance>>(emptyList())
    val allAttendance: StateFlow<List<Attendance>> = _allAttendance

    private val _opState        = MutableStateFlow<OpState>(OpState.Idle)
    val opState: StateFlow<OpState> = _opState

    init {
        loadByDate(todayStr)
        loadAll()
    }

    private fun loadByDate(date: String) {
        viewModelScope.launch {
            repo.getAttendanceByDate(date).collectLatest { _attendanceList.value = it }
        }
    }

    private fun loadAll() {
        viewModelScope.launch {
            repo.getAllAttendance().collectLatest { _allAttendance.value = it }
        }
    }

    fun setDate(date: String) {
        _selectedDate.value = date
        loadByDate(date)
    }

    fun markAttendance(employeeId: String, employeeName: String, status: String, notes: String = "") {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val att = Attendance(
                employeeId = employeeId, employeeName = employeeName,
                date = _selectedDate.value, status = status, notes = notes
            )
            val r = repo.markAttendance(att)
            _opState.value = if (r.isSuccess) OpState.Success
            else OpState.Error(r.exceptionOrNull()?.message ?: "Failed")
        }
    }

    fun checkIn(
        employeeId: String, employeeName: String, time: String, notes: String = "",
        onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val today    = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val status   = if (time >= "09:30") "Late" else "Present"
            val existing = repo.getRecordForDate(employeeId, today)

            val result = if (existing != null) {
                repo.updateAttendance(existing.copy(checkInTime = time, status = status, notes = notes))
            } else {
                repo.markAttendance(
                    Attendance(employeeId = employeeId, employeeName = employeeName,
                        date = today, checkInTime = time, status = status, notes = notes)
                ).map { true }
            }

            if (result.isSuccess) { _opState.value = OpState.Success; onSuccess() }
            else { val msg = result.exceptionOrNull()?.message ?: "Check-in failed"
                _opState.value = OpState.Error(msg); onError(msg) }
        }
    }

    fun checkOut(
        employeeId: String, time: String,
        onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val today    = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val existing = repo.getRecordForDate(employeeId, today)

            if (existing == null) {
                onError("No check-in found for today. Please check-in first.")
                return@launch
            }

            _opState.value = OpState.Loading
            val hours  = calcHours(existing.checkInTime, time)
            val status = when {
                hours < 4.0              -> "Half Day"
                existing.status == "Late" -> "Late"
                else                     -> "Present"
            }

            val r = repo.updateAttendance(existing.copy(checkOutTime = time, hoursWorked = hours, status = status))
            if (r.isSuccess) { _opState.value = OpState.Success; onSuccess() }
            else { val msg = r.exceptionOrNull()?.message ?: "Check-out failed"
                _opState.value = OpState.Error(msg); onError(msg) }
        }
    }

    fun deleteAttendance(id: String) {
        viewModelScope.launch { repo.deleteAttendance(id) }
    }

    fun resetOpState() { _opState.value = OpState.Idle }

    private fun calcHours(checkIn: String, checkOut: String): Double {
        return try {
            val fmt  = SimpleDateFormat("HH:mm", Locale.getDefault())
            val inT  = fmt.parse(checkIn) ?: return 0.0
            val outT = fmt.parse(checkOut) ?: return 0.0
            val diff = (outT.time - inT.time) / (1000.0 * 60 * 60)
            if (diff < 0) 0.0 else diff
        } catch (_: Exception) { 0.0 }
    }
}