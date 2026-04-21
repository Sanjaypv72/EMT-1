package com.emt.app.repository

import com.emt.app.model.Employee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EmployeeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("employees")

    fun getEmployees(): Flow<List<Employee>> = callbackFlow {
        val listener = col.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.documents?.mapNotNull {
                it.toObject(Employee::class.java)?.copy(id = it.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addEmployee(employee: Employee): Result<String> {
        return try {
            val doc = col.add(employee).await()
            Result.success(doc.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateEmployee(employee: Employee): Result<Boolean> {
        return try {
            col.document(employee.id).set(employee).await()
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteEmployee(id: String): Result<Boolean> {
        return try {
            col.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }
}