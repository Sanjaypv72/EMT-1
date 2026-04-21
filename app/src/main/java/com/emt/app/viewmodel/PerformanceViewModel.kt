package com.emt.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emt.app.model.Performance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PerformanceViewModel : ViewModel() {

    private val db  = FirebaseFirestore.getInstance()
    private val col = db.collection("performance")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _performanceList = MutableStateFlow<List<Performance>>(emptyList())
    val performanceList: StateFlow<List<Performance>> = _performanceList

    init {
        col.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            _performanceList.value = snapshot.documents.mapNotNull {
                it.toObject(Performance::class.java)?.copy(id = it.id)
            }
        }
    }

    // ✅ Matches exactly what PerformanceEvalScreen calls
    fun submitReview(
        performance: Performance,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = col.add(performance).await()
                col.document(doc.id).update("id", doc.id).await()
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to save review")
            }
        }
    }

    fun fetchReviewsForEmployee(employeeId: String) {
        col.whereEqualTo("employeeId", employeeId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                _performanceList.value = snapshot.documents.mapNotNull {
                    it.toObject(Performance::class.java)?.copy(id = it.id)
                }
            }
    }
}