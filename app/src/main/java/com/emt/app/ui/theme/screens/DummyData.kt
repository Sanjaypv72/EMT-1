package com.emt.app.ui.theme.screens

// Dummy Employee Data Class (no Firebase)
data class EmployeeUI(
    val id: String,
    val name: String,
    val role: String,
    val department: String,
    val joiningDate: String,
    val email: String,
    val contact: String,
    val avatarColor: Long = 0xFF00796B
)

// Dummy Employees List
val dummyEmployees = listOf(
    EmployeeUI("1", "Rahul Verma",    "Android Developer", "Engineering", "01/03/2023", "rahul@emt.com",  "9876543210"),
    EmployeeUI("2", "Priya Sharma",   "UI/UX Designer",    "Design",      "15/06/2022", "priya@emt.com",  "9876543211", 0xFF43A047),
    EmployeeUI("3", "Amit Patel",     "HR Manager",        "HR",          "10/01/2021", "amit@emt.com",   "9876543212", 0xFF7B1FA2),
    EmployeeUI("4", "Sneha Joshi",    "Marketing Lead",    "Marketing",   "20/09/2023", "sneha@emt.com",  "9876543213", 0xFFE65100),
    EmployeeUI("5", "Karan Mehta",    "Backend Developer", "Engineering", "05/11/2022", "karan@emt.com",  "9876543214"),
    EmployeeUI("6", "Neha Singh",     "Sales Executive",   "Sales",       "12/04/2023", "neha@emt.com",   "9876543215", 0xFF00838F),
    EmployeeUI("7", "Vikas Gupta",    "Finance Analyst",   "Finance",     "08/07/2021", "vikas@emt.com",  "9876543216", 0xFFC62828),
    EmployeeUI("8", "Riya Kapoor",    "Operations Head",   "Operations",  "25/02/2022", "riya@emt.com",   "9876543217", 0xFF43A047),
)

val departments = listOf("All", "Engineering", "Design", "HR", "Marketing", "Sales", "Finance", "Operations")

// ── Task Data ───────────────────────────────────────
data class TaskUI(
    val id: String,
    val title: String,
    val description: String,
    val assignedTo: String,
    val deadline: String,
    val priority: String,   // High / Medium / Low
    val status: String      // Pending / In Progress / Completed / Reviewed
)

val dummyTasks = listOf(
    TaskUI("t1", "Fix Login Bug",       "Resolve auth crash on Samsung devices",  "Rahul Verma",   "05/04/2026", "High",   "In Progress"),
    TaskUI("t2", "Design Onboarding",   "Create 3 onboarding screens in Figma",   "Priya Sharma",  "07/04/2026", "Medium", "Pending"),
    TaskUI("t3", "HR Policy Update",    "Update leave policy document",           "Amit Patel",    "04/04/2026", "Low",    "Completed"),
    TaskUI("t4", "Q1 Campaign",         "Prepare Q1 social media calendar",       "Sneha Joshi",   "10/04/2026", "Medium", "Pending"),
    TaskUI("t5", "API Integration",     "Integrate payment gateway APIs",         "Karan Mehta",   "06/04/2026", "High",   "In Progress"),
    TaskUI("t6", "Sales Report",        "Compile March monthly sales report",     "Neha Singh",    "03/04/2026", "High",   "Completed"),
    TaskUI("t7", "Budget Analysis",     "Q1 budget vs actual comparison",         "Vikas Gupta",   "08/04/2026", "Medium", "Reviewed"),
    TaskUI("t8", "Ops Streamlining",    "Document new warehouse process flow",    "Riya Kapoor",   "09/04/2026", "Low",    "Pending"),
)

// ── Performance Data ────────────────────────────────
data class PerformanceUI(
    val employeeId: String,
    val month: String,
    val quality: Int,
    val timeliness: Int,
    val attendance: Int,
    val communication: Int,
    val innovation: Int
) {
    val overall: Float get() =
        (quality + timeliness + attendance + communication + innovation) / 5f
}

val dummyPerformance = listOf(
    PerformanceUI("1", "March 2026", 4, 5, 5, 4, 3),
    PerformanceUI("1", "February 2026", 3, 4, 5, 3, 4),
    PerformanceUI("2", "March 2026", 5, 4, 5, 5, 4),
    PerformanceUI("3", "March 2026", 4, 3, 4, 5, 3),
    PerformanceUI("4", "March 2026", 3, 4, 4, 4, 5),
    PerformanceUI("5", "March 2026", 5, 5, 5, 4, 5),
    PerformanceUI("6", "March 2026", 4, 4, 3, 4, 3),
    PerformanceUI("7", "March 2026", 3, 5, 5, 3, 4),
)
