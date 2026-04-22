package com.emt.app.repository

import com.emt.app.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val col = FirebaseFirestore.getInstance().collection("tasks")

    fun getAllTasks(): Flow<List<Task>> = callbackFlow {
        val l = col.addSnapshotListener { snap, _ ->
            trySend(snap?.documents?.mapNotNull {
                it.toObject(Task::class.java)?.copy(id = it.id)
            } ?: emptyList())
        }
        awaitClose { l.remove() }
    }

    fun getTasksByEmployee(employeeId: String): Flow<List<Task>> = callbackFlow {
        val l = col.whereEqualTo("employeeId", employeeId)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.documents?.mapNotNull {
                    it.toObject(Task::class.java)?.copy(id = it.id)
                } ?: emptyList())
            }
        awaitClose { l.remove() }
    }

    suspend fun addTask(task: Task): Result<String> = try {
        val doc = col.add(task).await()
        col.document(doc.id).update("id", doc.id).await()
        Result.success(doc.id)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateTask(task: Task): Result<Boolean> = try {
        col.document(task.id).set(task).await()
        Result.success(true)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteTask(id: String): Result<Boolean> = try {
        col.document(id).delete().await()
        Result.success(true)
    } catch (e: Exception) { Result.failure(e) }
}