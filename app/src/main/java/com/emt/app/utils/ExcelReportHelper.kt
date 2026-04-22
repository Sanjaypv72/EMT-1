package com.emt.app.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.emt.app.model.Attendance
import com.emt.app.model.Employee
import com.emt.app.model.Performance
import com.emt.app.model.Task
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelReportHelper {

    fun generateReport(
        context: Context,
        employees: List<Employee>,
        tasks: List<Task>,
        performance: List<Performance>,
        attendance: List<Attendance>
    ): File {
        val wb = XSSFWorkbook()

        fun headerStyle(): CellStyle = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.TEAL.index
            fillPattern         = FillPatternType.SOLID_FOREGROUND
            val font = wb.createFont().also {
                it.bold  = true
                it.color = IndexedColors.WHITE.index
                it.fontHeightInPoints = 11
            }
            setFont(font)
            alignment    = HorizontalAlignment.CENTER
            borderBottom = BorderStyle.THIN
        }

        fun altStyle(even: Boolean): CellStyle = wb.createCellStyle().apply {
            fillForegroundColor = if (even) IndexedColors.LIGHT_TURQUOISE.index
            else      IndexedColors.WHITE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }

        fun Row.str(col: Int, v: String,  s: CellStyle? = null) =
            createCell(col).also { it.setCellValue(v); if (s != null) it.cellStyle = s }
        fun Row.num(col: Int, v: Double, s: CellStyle? = null) =
            createCell(col).also { it.setCellValue(v); if (s != null) it.cellStyle = s }

        // Sheet 1: Summary
        wb.createSheet("Summary").apply {
            createRow(0).str(0, "EMT - Employee Management Report")
            createRow(1).str(0, "Generated: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
            createRow(3).apply { str(0, "Metric", headerStyle()); str(1, "Value", headerStyle()) }
            listOf(
                "Total Employees"     to "${employees.size}",
                "Active Employees"    to "${employees.count { it.isActive }}",
                "Total Tasks"         to "${tasks.size}",
                "Completed Tasks"     to "${tasks.count { it.status == "Completed" || it.status == "Reviewed" }}",
                "Pending Tasks"       to "${tasks.count { it.status == "Pending" }}",
                "Performance Records" to "${performance.size}",
                "Attendance Records"  to "${attendance.size}",
                "Avg Overall Rating"  to if (performance.isEmpty()) "N/A"
                else String.format(Locale.getDefault(), "%.2f / 5.0", performance.map { it.overallRating.toDouble() }.average())
            ).forEachIndexed { i, (k, v) ->
                val s = altStyle(i % 2 == 0)
                createRow(4 + i).apply { str(0, k, s); str(1, v, s) }
            }
            setColumnWidth(0, 30 * 256); setColumnWidth(1, 20 * 256)
        }

        // Sheet 2: Employees
        wb.createSheet("Employees").apply {
            val hs = headerStyle()
            createRow(0).apply {
                str(0,"ID",hs); str(1,"Name",hs); str(2,"Role",hs)
                str(3,"Department",hs); str(4,"Email",hs)
                str(5,"Contact",hs); str(6,"Joining Date",hs); str(7,"Active",hs)
            }
            employees.forEachIndexed { i, e ->
                val s = altStyle(i % 2 == 0)
                createRow(i + 1).apply {
                    str(0,e.id,s); str(1,e.name,s); str(2,e.role,s)
                    str(3,e.department,s); str(4,e.email,s)
                    str(5,e.contact,s); str(6,e.joiningDate,s)
                    str(7, if (e.isActive) "Yes" else "No", s)
                }
            }
            (0..7).forEach { autoSizeColumn(it) }
        }

        // Sheet 3: Tasks
        wb.createSheet("Tasks").apply {
            val hs = headerStyle()
            createRow(0).apply {
                str(0,"Title",hs); str(1,"Description",hs); str(2,"Employee",hs)
                str(3,"Priority",hs); str(4,"Status",hs)
                str(5,"Deadline",hs); str(6,"Assigned Date",hs)
            }
            tasks.forEachIndexed { i, t ->
                val s = altStyle(i % 2 == 0)
                createRow(i + 1).apply {
                    str(0,t.title,s); str(1,t.description,s); str(2,t.employeeName,s)
                    str(3,t.priority,s); str(4,t.status,s)
                    str(5,t.deadline,s); str(6,t.assignedDate,s)
                }
            }
            (0..6).forEach { autoSizeColumn(it) }
        }

        // Sheet 4: Performance
        wb.createSheet("Performance").apply {
            val hs = headerStyle()
            createRow(0).apply {
                str(0,"Employee",hs); str(1,"Month",hs)
                str(2,"Quality",hs); str(3,"Timeliness",hs)
                str(4,"Attendance",hs); str(5,"Communication",hs)
                str(6,"Innovation",hs); str(7,"Overall Rating",hs); str(8,"Remarks",hs)
            }
            performance.forEachIndexed { i, p ->
                val s = altStyle(i % 2 == 0)
                createRow(i + 1).apply {
                    str(0,p.employeeName,s); str(1,p.month,s)
                    num(2,p.qualityScore.toDouble(),s)
                    num(3,p.timelinessScore.toDouble(),s)
                    num(4,p.attendanceScore.toDouble(),s)
                    num(5,p.communicationScore.toDouble(),s)
                    num(6,p.innovationScore.toDouble(),s)
                    num(7,p.overallRating.toDouble(),s)
                    str(8,p.remarks,s)
                }
            }
            (0..8).forEach { autoSizeColumn(it) }
        }

        // Sheet 5: Attendance
        wb.createSheet("Attendance").apply {
            val hs = headerStyle()
            createRow(0).apply {
                str(0,"Employee",hs); str(1,"Date",hs); str(2,"Status",hs)
                str(3,"Check In",hs); str(4,"Check Out",hs)
                str(5,"Hours Worked",hs); str(6,"Notes",hs); str(7,"Marked By",hs)
            }
            attendance.forEachIndexed { i, a ->
                val s = altStyle(i % 2 == 0)
                createRow(i + 1).apply {
                    str(0,a.employeeName,s); str(1,a.date,s); str(2,a.status,s)
                    str(3,a.checkInTime,s); str(4,a.checkOutTime,s)
                    num(5,a.hoursWorked,s); str(6,a.notes,s); str(7,a.markedBy,s)
                }
            }
            (0..7).forEach { autoSizeColumn(it) }
        }

        val file = File(context.cacheDir, "EMT_Report.xlsx")
        FileOutputStream(file).use { wb.write(it) }
        wb.close()
        return file
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(
            Intent.createChooser(intent, "Open Excel Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}