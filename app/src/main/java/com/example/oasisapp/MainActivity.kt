// MainActivity.kt
package com.example.oasisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oasisapp.navigation.Screen
import com.example.oasisapp.ui.screens.DevicesScreen
import com.example.oasisapp.ui.screens.HomeScreen
import com.example.oasisapp.ui.screens.LoginScreen
import com.example.oasisapp.ui.screens.RegistrationScreen
import com.example.oasisapp.ui.theme.OasisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OasisApp()
        }
    }
}

@Composable
fun OasisApp() {
    OasisTheme {
        val navController = rememberNavController()
        var isLoggedIn by rememberSaveable { mutableStateOf(false) }

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = {
                        isLoggedIn = true
                        navController.navigate(Screen.Main.route) {
                            popUpTo(0)
                        }
                    },
                    onShowRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegistrationScreen(
                    onRegister = {
                        isLoggedIn = true
                        navController.navigate(Screen.Main.route) {
                            popUpTo(0)
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Main.route) {
                MainScaffold()
            }
        }
    }
}

@Composable
fun MainScaffold() {
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = Home, 1 = Eszközök

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Főoldal") },
                    label = { Text("Főoldal") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Eszközök") },
                    label = { Text("Eszközök") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(innerPadding)
            1 -> DevicesScreen(innerPadding)
        }
    }
}
