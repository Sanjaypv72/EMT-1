package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {

    private val db  = FirebaseFirestore.getInstance()
    private val col = db.collection("tasks")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList

    init {
        col.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            _taskList.value = snapshot.documents.mapNotNull {
                it.toObject(Task::class.java)?.copy(id = it.id)
            }
        }
    }

    fun addTask(
        task: Task,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = col.add(task).await()
                col.document(doc.id).update("id", doc.id).await()
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to assign task")
            }
        }
    }

    fun fetchTasksForEmployee(employeeId: String) {
        col.whereEqualTo("employeeId", employeeId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                _taskList.value = snapshot.documents.mapNotNull {
                    it.toObject(Task::class.java)?.copy(id = it.id)
                }
            }
    }
}
