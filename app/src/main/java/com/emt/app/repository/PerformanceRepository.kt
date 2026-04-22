package com.emt.app.repository

import com.emt.app.model.Performance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PerformanceRepository {
    private val col = FirebaseFirestore.getInstance().collection("performance")

    fun getAllPerformance(): Flow<List<Performance>> = callbackFlow {
        val l = col.addSnapshotListener { snap, _ ->
            trySend(snap?.documents?.mapNotNull {
                it.toObject(Performance::class.java)?.copy(id = it.id)
            } ?: emptyList())
        }
        awaitClose { l.remove() }
    }

    fun getPerformanceByEmployee(employeeId: String): Flow<List<Performance>> = callbackFlow {
        val l = col.whereEqualTo("employeeId", employeeId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.documents?.mapNotNull {
                    it.toObject(Performance::class.java)?.copy(id = it.id)
                } ?: emptyList())
            }
        awaitClose { l.remove() }
    }

    suspend fun savePerformance(p: Performance): Result<String> = try {
        val doc = col.add(p).await()
        col.document(doc.id).update("id", doc.id).await()
        Result.success(doc.id)
    } catch (e: Exception) { Result.failure(e) }
}