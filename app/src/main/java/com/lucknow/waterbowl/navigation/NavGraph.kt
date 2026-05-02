package com.lucknow.waterbowl.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lucknow.waterbowl.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    data object Drives : Screen("drives", "Drives", Icons.Outlined.DirectionsCar)
    data object Record : Screen("record", "Record", Icons.Filled.CameraAlt)
    data object RoutePlanner : Screen("route_planner", "Route", Icons.Filled.Map)
    data object Gallery : Screen("gallery", "Gallery", Icons.Filled.PhotoLibrary)
    data object DriveDetail : Screen("drive_detail/{driveId}", "Drive Detail", Icons.Outlined.DirectionsCar) {
        fun createRoute(driveId: Int) = "drive_detail/$driveId"
    }
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Drives,
    Screen.Record,
    Screen.RoutePlanner,
    Screen.Gallery,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterBowlNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController)
            }
            composable(Screen.Drives.route) {
                DrivesScreen(navController = navController)
            }
            composable(
                route = Screen.DriveDetail.route,
                arguments = listOf(navArgument("driveId") { type = NavType.IntType })
            ) { backStackEntry ->
                val driveId = backStackEntry.arguments?.getInt("driveId") ?: 0
                DriveDetailScreen(driveId = driveId, navController = navController)
            }
            composable(Screen.Record.route) {
                RecordDistributionScreen()
            }
            composable(Screen.RoutePlanner.route) {
                RoutePlannerScreen()
            }
            composable(Screen.Gallery.route) {
                GalleryScreen()
            }
        }
    }
}
