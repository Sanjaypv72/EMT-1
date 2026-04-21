package com.emt.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emt.app.model.Employee
import com.emt.app.ui.theme.screens.*

object Routes {
    const val SPLASH           = "splash"
    const val LOGIN            = "login"
    const val EMPLOYEE_LIST    = "employee_list"
    const val ADD_EMPLOYEE     = "add_employee"
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
}

@Composable
fun NavGraph(navController: NavHostController) {

    // ✅ Employee (Firebase model) — consistent with EmployeeListScreen + EmployeeDetailScreen
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

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
                onAddEmployee   = { navController.navigate(Routes.ADD_EMPLOYEE) },
                onEmployeeClick = { emp: Employee ->      // ✅ explicit type — no ambiguity
                    selectedEmployee = emp
                    navController.navigate(Routes.EMPLOYEE_DETAIL)
                }
            )
        }

        composable(Routes.ADD_EMPLOYEE) {
            AddEditEmployeeScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.EMPLOYEE_DETAIL) {
            selectedEmployee?.let { emp ->
                EmployeeDetailScreen(
                    employee    = emp,
                    onBack      = { navController.popBackStack() },
                    onEditClick = { navController.navigate(Routes.ADD_EMPLOYEE) }
                )
            }
        }

        composable(Routes.TASK_ASSIGNMENT) {
            TaskAssignmentScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PERFORMANCE_EVAL) {
            PerformanceEvalScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ANALYTICS) {
            AnalyticsDashboard(onSettingsClick = { navController.navigate(Routes.SETTINGS) })
        }

        composable(Routes.MY_TASKS) {
            MyTasksScreen(onSettingsClick = { navController.navigate(Routes.SETTINGS) })
        }

        composable(Routes.MY_PERFORMANCE) {
            MyPerformanceScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ATTENDANCE) {
            AttendanceScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                isAdmin               = true,
                onBack                = { navController.popBackStack() },
                onLogout              = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfileClick        = { navController.navigate(Routes.PROFILE_INFO) },
                onNotificationsClick  = { navController.navigate(Routes.NOTIFICATIONS) },
                onChangePasswordClick = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onAboutClick          = { navController.navigate(Routes.ABOUT_APP) }
            )
        }

        composable(Routes.PROFILE_INFO) {
            ProfileInfoScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT_APP) {
            AboutAppScreen(onBack = { navController.popBackStack() })
        }
    }
}
