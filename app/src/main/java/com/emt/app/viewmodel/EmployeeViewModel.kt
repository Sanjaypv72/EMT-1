package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Employee
import com.emt.app.repository.EmployeeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {
    private val repo = EmployeeRepository()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee: StateFlow<Employee?> = _selectedEmployee.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            repo.getEmployees()
                .catch { /* Handle potential DB errors here */ }
                .collect { list -> _employees.value = list }
        }
    }

    fun selectEmployee(employee: Employee) {
        _selectedEmployee.value = employee
    }

    // --- ADDED THIS FUNCTION TO FIX THE ERRORS ---
    fun addEmployee(
        employee: Employee,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.addEmployee(employee)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Failed to add employee")
            }
        }
    }

    // Clean operation state handling
    fun updateEmployee(employee: Employee, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.updateEmployee(employee)
            if (result.isSuccess) {
                _selectedEmployee.value = employee
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
}