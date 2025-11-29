package com.plcoding.echojournal.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plcoding.echojournal.echos.presentation.echos.EchosRoot
import com.plcoding.echojournal.echos.presentation.util.toCreateEchoRoute

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Echos
    ) {
        composable<NavigationRoute.Echos> {
            EchosRoot(
                onNavigateToCreateEcho = { details ->
                    navController.navigate(details.toCreateEchoRoute())
                }
            )
        }
        composable<NavigationRoute.CreateEcho> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Create Echo Screen")
            }
        }
    }
}