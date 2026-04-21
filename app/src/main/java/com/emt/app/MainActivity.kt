package com.emt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.emt.app.navigation.NavGraph
import com.emt.app.ui.theme.EMTTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            EMTTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}