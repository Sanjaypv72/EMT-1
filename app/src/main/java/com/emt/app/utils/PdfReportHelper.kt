package com.emt.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.emt.app.model.Attendance
import com.emt.app.model.Employee
import com.emt.app.model.Performance
import com.emt.app.model.Task
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportHelper {

    fun generateReport(
        context: Context,
        employees: List<Employee>,
        tasks: List<Task>,
        performance: List<Performance>,
        attendance: List<Attendance>
    ): File {
        val pdfDoc = PdfDocument()
        val pageWidth  = 595   // A4 width  in points
        val pageHeight = 842   // A4 height in points
        var pageNum    = 1

        // ── helpers ─────────────────────────────────────────────────────────
        fun newPage(): Pair<PdfDocument.Page, Canvas> {
            val info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
            val page = pdfDoc.startPage(info)
            return page to page.canvas
        }

        fun Paint.bold(size: Float = 14f) = apply {
            typeface  = Typeface.DEFAULT_BOLD; textSize = size; color = Color.BLACK
        }
        fun Paint.normal(size: Float = 11f) = apply {
            typeface  = Typeface.DEFAULT; textSize = size; color = Color.DKGRAY
        }
        fun Paint.header() = apply {
            typeface  = Typeface.DEFAULT_BOLD; textSize = 22f; color = Color.parseColor("#00796B")
        }

        val paint  = Paint()
        val margin = 40f
        val lineH  = 18f

        // ── Page 1: Summary ────────────────────────────────────────────────
        var (page, canvas) = newPage()

        // Title banner
        paint.color = Color.parseColor("#00796B")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 70f, paint)
        paint.color = Color.WHITE
        paint.typeface = Typeface.DEFAULT_BOLD; paint.textSize = 26f
        canvas.drawText("EMT – Employee Management Report", margin, 46f, paint)

        paint.normal(10f); paint.color = Color.WHITE
        canvas.drawText(
            "Generated: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())}",
            margin, 62f, paint
        )

        var y = 100f
        paint.header(); canvas.drawText("Summary", margin, y, paint); y += 30f

        fun summaryRow(label: String, value: String) {
            paint.bold(12f);   canvas.drawText(label, margin, y, paint)
            paint.normal(12f); canvas.drawText(value, 220f, y, paint)
            y += lineH * 1.4f
        }
        summaryRow("Total Employees",   "${employees.size}")
        summaryRow("Active Employees",  "${employees.count { it.isActive }}")
        summaryRow("Total Tasks",        "${tasks.size}")
        summaryRow("Completed Tasks",    "${tasks.count { it.status == "Completed" || it.status == "Reviewed" }}")
        summaryRow("Pending Tasks",      "${tasks.count { it.status == "Pending" }}")
        summaryRow("Performance Records","${performance.size}")
        summaryRow("Attendance Records", "${attendance.size}")

        if (performance.isNotEmpty()) {
            val avg = performance.map { it.overallRating.toDouble() }.average()
            summaryRow("Avg Overall Rating", String.format("%.2f / 5.0", avg))
        }

        pdfDoc.finishPage(page)

        // ── Page 2: Employees ──────────────────────────────────────────────
        val ep = newPage(); page = ep.first; canvas = ep.second; y = 50f

        paint.bold(18f); paint.color = Color.parseColor("#00796B")
        canvas.drawText("Employees", margin, y, paint); y += 30f

        // table header
        paint.color = Color.parseColor("#004D40"); paint.style = Paint.Style.FILL
        canvas.drawRect(margin, y, pageWidth - margin, y + 20f, paint)
        paint.color = Color.WHITE; paint.textSize = 10f; paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Name", margin + 4, y + 14f, paint)
        canvas.drawText("Role", 200f, y + 14f, paint)
        canvas.drawText("Dept", 310f, y + 14f, paint)
        canvas.drawText("Email", 400f, y + 14f, paint)
        y += 22f

        employees.forEachIndexed { i, emp ->
            if (y > pageHeight - 60) {
                pdfDoc.finishPage(page)
                val np = newPage(); page = np.first; canvas = np.second; y = 50f
            }
            paint.color = if (i % 2 == 0) Color.parseColor("#F1F8F7") else Color.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin, y - 12f, pageWidth - margin, y + 6f, paint)
            paint.color = Color.DKGRAY; paint.textSize = 9f; paint.typeface = Typeface.DEFAULT
            canvas.drawText(emp.name.take(22),        margin + 4, y, paint)
            canvas.drawText(emp.role.take(16),        200f, y, paint)
            canvas.drawText(emp.department.take(14),  310f, y, paint)
            canvas.drawText(emp.email.take(22),       400f, y, paint)
            y += lineH
        }
        pdfDoc.finishPage(page)

        // ── Page 3: Performance ────────────────────────────────────────────
        val pp = newPage(); page = pp.first; canvas = pp.second; y = 50f

        paint.bold(18f); paint.color = Color.parseColor("#00796B")
        canvas.drawText("Performance Evaluations", margin, y, paint); y += 30f

        paint.color = Color.parseColor("#004D40"); paint.style = Paint.Style.FILL
        canvas.drawRect(margin, y, pageWidth - margin, y + 20f, paint)
        paint.color = Color.WHITE; paint.textSize = 10f; paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Employee",    margin + 4, y + 14f, paint)
        canvas.drawText("Month",       200f, y + 14f, paint)
        canvas.drawText("Quality",     280f, y + 14f, paint)
        canvas.drawText("Timeliness",  335f, y + 14f, paint)
        canvas.drawText("Attendance",  400f, y + 14f, paint)
        canvas.drawText("Overall",     465f, y + 14f, paint)
        y += 22f

        performance.forEachIndexed { i, perf ->
            if (y > pageHeight - 60) {
                pdfDoc.finishPage(page)
                val np = newPage(); page = np.first; canvas = np.second; y = 50f
            }
            paint.color = if (i % 2 == 0) Color.parseColor("#F1F8F7") else Color.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin, y - 12f, pageWidth - margin, y + 6f, paint)
            paint.color = Color.DKGRAY; paint.textSize = 9f; paint.typeface = Typeface.DEFAULT
            canvas.drawText(perf.employeeName.take(20),             margin + 4, y, paint)
            canvas.drawText(perf.month.take(12),                    200f, y, paint)
            canvas.drawText(String.format("%.1f", perf.qualityScore),    280f, y, paint)
            canvas.drawText(String.format("%.1f", perf.timelinessScore), 335f, y, paint)
            canvas.drawText(String.format("%.1f", perf.attendanceScore), 400f, y, paint)
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.color = when {
                perf.overallRating >= 4f -> Color.parseColor("#00796B")
                perf.overallRating >= 3f -> Color.parseColor("#F59E0B")
                else                     -> Color.parseColor("#F43F5E")
            }
            canvas.drawText(String.format("%.1f", perf.overallRating),  465f, y, paint)
            y += lineH
        }
        pdfDoc.finishPage(page)

        // ── Page 4: Tasks ─────────────────────────────────────────────────
        val tp = newPage(); page = tp.first; canvas = tp.second; y = 50f

        paint.bold(18f); paint.color = Color.parseColor("#00796B")
        canvas.drawText("Tasks", margin, y, paint); y += 30f

        paint.color = Color.parseColor("#004D40"); paint.style = Paint.Style.FILL
        canvas.drawRect(margin, y, pageWidth - margin, y + 20f, paint)
        paint.color = Color.WHITE; paint.textSize = 10f; paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Title",      margin + 4, y + 14f, paint)
        canvas.drawText("Assigned To",200f, y + 14f, paint)
        canvas.drawText("Priority",   360f, y + 14f, paint)
        canvas.drawText("Status",     440f, y + 14f, paint)
        canvas.drawText("Deadline",   515f, y + 14f, paint)
        y += 22f

        tasks.forEachIndexed { i, task ->
            if (y > pageHeight - 60) {
                pdfDoc.finishPage(page)
                val np = newPage(); page = np.first; canvas = np.second; y = 50f
            }
            paint.color = if (i % 2 == 0) Color.parseColor("#F1F8F7") else Color.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin, y - 12f, pageWidth - margin, y + 6f, paint)
            paint.color = Color.DKGRAY; paint.textSize = 9f; paint.typeface = Typeface.DEFAULT
            canvas.drawText(task.title.take(22),        margin + 4, y, paint)
            canvas.drawText(task.employeeName.take(20), 200f, y, paint)
            paint.color = when (task.priority) {
                "High"   -> Color.parseColor("#F43F5E")
                "Medium" -> Color.parseColor("#F59E0B")
                else     -> Color.parseColor("#10B981")
            }
            canvas.drawText(task.priority, 360f, y, paint)
            paint.color = when (task.status) {
                "Completed", "Reviewed" -> Color.parseColor("#10B981")
                "In Progress"           -> Color.parseColor("#F59E0B")
                else                    -> Color.GRAY
            }
            canvas.drawText(task.status.take(14), 440f, y, paint)
            paint.color = Color.DKGRAY
            canvas.drawText(task.deadline.take(12), 515f, y, paint)
            y += lineH
        }
        pdfDoc.finishPage(page)

        // ── Page 5: Attendance ────────────────────────────────────────────
        val ap = newPage(); page = ap.first; canvas = ap.second; y = 50f

        paint.bold(18f); paint.color = Color.parseColor("#00796B")
        canvas.drawText("Attendance Records", margin, y, paint); y += 30f

        paint.color = Color.parseColor("#004D40"); paint.style = Paint.Style.FILL
        canvas.drawRect(margin, y, pageWidth - margin, y + 20f, paint)
        paint.color = Color.WHITE; paint.textSize = 10f; paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Employee",  margin + 4, y + 14f, paint)
        canvas.drawText("Date",      200f, y + 14f, paint)
        canvas.drawText("Status",    300f, y + 14f, paint)
        canvas.drawText("Check In",  380f, y + 14f, paint)
        canvas.drawText("Check Out", 460f, y + 14f, paint)
        canvas.drawText("Hours",     540f, y + 14f, paint)
        y += 22f

        attendance.forEachIndexed { i, att ->
            if (y > pageHeight - 60) {
                pdfDoc.finishPage(page)
                val np = newPage(); page = np.first; canvas = np.second; y = 50f
            }
            paint.color = if (i % 2 == 0) Color.parseColor("#F1F8F7") else Color.WHITE
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin, y - 12f, pageWidth - margin, y + 6f, paint)
            paint.color = Color.DKGRAY; paint.textSize = 9f; paint.typeface = Typeface.DEFAULT
            canvas.drawText(att.employeeName.take(20), margin + 4, y, paint)
            canvas.drawText(att.date.take(12),         200f, y, paint)
            paint.color = when (att.status) {
                "Present" -> Color.parseColor("#00796B")
                "Late"    -> Color.parseColor("#F59E0B")
                "Absent"  -> Color.parseColor("#F43F5E")
                else      -> Color.GRAY
            }
            canvas.drawText(att.status, 300f, y, paint)
            paint.color = Color.DKGRAY
            canvas.drawText(att.checkInTime.take(8),  380f, y, paint)
            canvas.drawText(att.checkOutTime.take(8), 460f, y, paint)
            canvas.drawText(String.format("%.1f h", att.hoursWorked), 540f, y, paint)
            y += lineH
        }
        pdfDoc.finishPage(page)

        // ── Save ──────────────────────────────────────────────────────────
        val file = File(context.cacheDir, "EMT_Report.pdf")
        FileOutputStream(file).use { pdfDoc.writeTo(it) }
        pdfDoc.close()
        return file
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open PDF Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}