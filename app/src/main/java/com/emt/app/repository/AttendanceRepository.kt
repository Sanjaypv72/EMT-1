package com.emt.app.repository

import com.emt.app.model.Attendance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("attendance")

    fun getAttendanceByDate(date: String): Flow<List<Attendance>> = callbackFlow {
        val listener = col.whereEqualTo("date", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Attendance::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getAttendanceByEmployee(employeeId: String): Flow<List<Attendance>> = callbackFlow {
        val listener = col.whereEqualTo("employeeId", employeeId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Attendance::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markAttendance(attendance: Attendance): Result<String> {
        return try {
            val doc = col.add(attendance).await()
            col.document(doc.id).update("id", doc.id).await()
            Result.success(doc.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateAttendance(attendance: Attendance): Result<Boolean> {
        return try {
            col.document(attendance.id).set(attendance).await()
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteAttendance(id: String): Result<Boolean> {
        return try {
            col.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAttendanceByMonth(employeeId: String, month: String): List<Attendance> {
        return try {
            val snapshot = col.whereEqualTo("employeeId", employeeId)
                .whereGreaterThanOrEqualTo("date", "$month-01")
                .whereLessThanOrEqualTo("date", "$month-31")
                .get().await()
            snapshot.documents.mapNotNull {
                it.toObject(Attendance::class.java)?.copy(id = it.id)
            }
        } catch (e: Exception) { emptyList() }
    }
}
