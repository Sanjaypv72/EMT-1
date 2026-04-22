package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Performance
import com.emt.app.repository.PerformanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PerformanceViewModel : ViewModel() {
    private val repo = PerformanceRepository()

    private val _allPerformance = MutableStateFlow<List<Performance>>(emptyList())
    val allPerformance: StateFlow<List<Performance>> = _allPerformance

    private val _empPerformance = MutableStateFlow<List<Performance>>(emptyList())
    val empPerformance: StateFlow<List<Performance>> = _empPerformance

    private val _opState = MutableStateFlow<OpState>(OpState.Idle)
    val opState: StateFlow<OpState> = _opState

    init {
        viewModelScope.launch {
            repo.getAllPerformance().collectLatest { _allPerformance.value = it }
        }
    }

    fun loadForEmployee(employeeId: String) {
        viewModelScope.launch {
            repo.getPerformanceByEmployee(employeeId).collectLatest { _empPerformance.value = it }
        }
    }

    fun savePerformance(p: Performance, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val r = repo.savePerformance(p)
            if (r.isSuccess) { _opState.value = OpState.Success; onSuccess() }
            else { _opState.value = OpState.Error(r.exceptionOrNull()?.message ?: "Failed"); onError(r.exceptionOrNull()?.message ?: "Failed") }
        }
    }

    fun resetOpState() { _opState.value = OpState.Idle }
}