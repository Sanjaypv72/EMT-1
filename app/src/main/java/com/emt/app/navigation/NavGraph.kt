package com.emt.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emt.app.ui.theme.screens.*
import com.emt.app.viewmodel.*

object Routes {
    const val SPLASH           = "splash"
    const val LOGIN            = "login"
    const val EMPLOYEE_LIST    = "employee_list"
    const val ADD_EMPLOYEE     = "add_employee"
    const val EDIT_EMPLOYEE    = "edit_employee"
    const val EMPLOYEE_DETAIL  = "employee_detail"
    const val TASK_ASSIGNMENT  = "task_assignment"
    const val PERFORMANCE_EVAL = "performance_eval"
    const val MY_TASKS         = "my_tasks"
    const val MY_PERFORMANCE   = "my_performance"
    const val ANALYTICS        = "analytics"
    const val SETTINGS         = "settings"
    const val PROFILE_INFO     = "profile_info"
    const val NOTIFICATIONS    = "notifications"
    const val CHANGE_PASSWORD  = "change_password"
    const val ABOUT_APP        = "about_app"
    const val ATTENDANCE       = "attendance"
    const val REPORTS          = "reports"
}

@Composable
fun NavGraph(navController: NavHostController) {

    val employeeVM   : EmployeeViewModel    = viewModel()
    val taskVM       : TaskViewModel        = viewModel()
    val performanceVM: PerformanceViewModel = viewModel()
    val attendanceVM : AttendanceViewModel  = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginAsAdmin = {
                    navController.navigate(Routes.EMPLOYEE_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginAsEmployee = {
                    navController.navigate(Routes.MY_TASKS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMPLOYEE_LIST) {
            EmployeeListScreen(
                navController   = navController,
                employeeVM      = employeeVM,
                onAddEmployee   = { navController.navigate(Routes.ADD_EMPLOYEE) },
                onEmployeeClick = { emp ->
                    employeeVM.selectEmployee(emp)
                    navController.navigate(Routes.EMPLOYEE_DETAIL)
                }
            )
        }

        composable(Routes.ADD_EMPLOYEE) {
            AddEditEmployeeScreen(
                existingEmployee = null,
                employeeVM       = employeeVM,
                onBack           = { navController.popBackStack() }
            )
        }

        composable(Routes.EDIT_EMPLOYEE) {
            val selectedEmployee by employeeVM.selectedEmployee.collectAsState()
            AddEditEmployeeScreen(
                existingEmployee = selectedEmployee,
                employeeVM       = employeeVM,
                onBack           = { navController.popBackStack() }
            )
        }

        composable(Routes.EMPLOYEE_DETAIL) {
            val emp by employeeVM.selectedEmployee.collectAsState()
            if (emp != null) {
                EmployeeDetailScreen(
                    employee      = emp!!,
                    taskVM        = taskVM,
                    performanceVM = performanceVM,
                    onBack        = { navController.popBackStack() },
                    onEditClick   = { navController.navigate(Routes.EDIT_EMPLOYEE) }
                )
            }
        }

        composable(Routes.TASK_ASSIGNMENT) {
            TaskAssignmentScreen(
                employeeVM = employeeVM,
                taskVM     = taskVM,
                onBack     = { navController.popBackStack() }
            )
        }

        composable(Routes.PERFORMANCE_EVAL) {
            PerformanceEvalScreen(
                employeeVM    = employeeVM,
                performanceVM = performanceVM,
                onBack        = { navController.popBackStack() }
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsDashboard(
                employeeVM        = employeeVM,
                taskVM            = taskVM,
                performanceVM     = performanceVM,
                attendanceVM      = attendanceVM,
                onSettingsClick   = { navController.navigate(Routes.SETTINGS) },
                onAttendanceClick = { navController.navigate(Routes.ATTENDANCE) },
                onReportsClick    = { navController.navigate(Routes.REPORTS) }
            )
        }

        // Staff panel: My Tasks - has back button to go to login/logout
        composable(Routes.MY_TASKS) {
            MyTasksScreen(
                taskVM          = taskVM,
                employeeVM      = employeeVM,
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.MY_PERFORMANCE) {
            MyPerformanceScreen(
                performanceVM = performanceVM,
                employeeVM    = employeeVM,
                onBack        = { navController.popBackStack() }
            )
        }

        composable(Routes.ATTENDANCE) {
            AttendanceScreen(
                attendanceVM = attendanceVM,
                employeeVM   = employeeVM,
                onBack       = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORTS) {
            ReportsScreen(
                employeeVM    = employeeVM,
                taskVM        = taskVM,
                performanceVM = performanceVM,
                attendanceVM  = attendanceVM,
                onBack        = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                employeeVM           = employeeVM,
                isAdmin              = true,
                onBack               = { navController.popBackStack() },
                onLogout             = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfileClick       = { navController.navigate(Routes.PROFILE_INFO) },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onChangePasswordClick= { navController.navigate(Routes.CHANGE_PASSWORD) },
                onAboutClick         = { navController.navigate(Routes.ABOUT_APP) }
            )
        }

        composable(Routes.PROFILE_INFO) {
            ProfileInfoScreen(
                employeeVM = employeeVM,
                onBack     = { navController.popBackStack() }
            )
        }

        composable(Routes.NOTIFICATIONS)   { NotificationsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CHANGE_PASSWORD) { ChangePasswordScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.ABOUT_APP)       { AboutAppScreen(onBack = { navController.popBackStack() }) }
    }
}