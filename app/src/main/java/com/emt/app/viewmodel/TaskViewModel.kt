package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Task
import com.emt.app.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repo = TaskRepository()

    private val _tasks    = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _empTasks = MutableStateFlow<List<Task>>(emptyList())
    val empTasks: StateFlow<List<Task>> = _empTasks

    private val _opState  = MutableStateFlow<OpState>(OpState.Idle)
    val opState: StateFlow<OpState> = _opState

    init {
        viewModelScope.launch {
            repo.getAllTasks().collectLatest { _tasks.value = it }
        }
    }

    fun loadTasksForEmployee(employeeId: String) {
        viewModelScope.launch {
            repo.getTasksByEmployee(employeeId).collectLatest { _empTasks.value = it }
        }
    }

    fun addTask(task: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val r = repo.addTask(task)
            if (r.isSuccess) { _opState.value = OpState.Success; onSuccess() }
            else { _opState.value = OpState.Error(r.exceptionOrNull()?.message ?: "Failed"); onError(r.exceptionOrNull()?.message ?: "Failed") }
        }
    }

    fun updateTask(task: Task, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _opState.value = OpState.Loading
            val r = repo.updateTask(task)
            if (r.isSuccess) { _opState.value = OpState.Success; onSuccess() }
            else { _opState.value = OpState.Error(r.exceptionOrNull()?.message ?: "Failed"); onError(r.exceptionOrNull()?.message ?: "Failed") }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch { repo.deleteTask(id) }
    }

    fun resetOpState() { _opState.value = OpState.Idle }
}