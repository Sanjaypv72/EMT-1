package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Employee
import com.emt.app.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {

    private val repository = EmployeeRepository()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            repository.getEmployees()
                .catch { e -> }
                .collect { list -> _employees.value = list }
        }
    }

    fun addEmployee(employee: Employee, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addEmployee(employee)
            _isLoading.value = false
            if (result.isSuccess) onSuccess()
            else onError(result.exceptionOrNull()?.message ?: "Failed to save")
        }
    }

    fun updateEmployee(employee: Employee, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateEmployee(employee)
            _isLoading.value = false
            if (result.isSuccess) onSuccess()
            else onError(result.exceptionOrNull()?.message ?: "Failed to update")
        }
    }

    fun deleteEmployee(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteEmployee(id)
            if (result.isSuccess) onSuccess()
            else onError(result.exceptionOrNull()?.message ?: "Failed to delete")
        }
    }
}