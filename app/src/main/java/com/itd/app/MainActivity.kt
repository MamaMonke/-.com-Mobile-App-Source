package com.itd.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itd.app.ui.navigation.ITDBottomBar
import com.itd.app.ui.navigation.ITDNavHost
import com.itd.app.ui.navigation.Screen
import com.itd.app.ui.navigation.bottomNavItems
import com.itd.app.ui.theme.ITDBackground
import com.itd.app.ui.theme.ITDTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ITDTheme {
                ITDApp()
            }
        }
    }
}

@Composable
fun ITDApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val notificationCount by mainViewModel.notificationCount.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route } ||
            currentRoute == Screen.MyProfile.route

    val startDestination = if (isLoggedIn == true) Screen.Feed.route else Screen.Login.route

    // Wait until we know the login state
    if (isLoggedIn == null) return

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ITDBackground,
        bottomBar = {
            if (showBottomBar) {
                ITDBottomBar(
                    navController = navController,
                    notificationCount = notificationCount
                )
            }
        }
    ) { innerPadding ->
        ITDNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
