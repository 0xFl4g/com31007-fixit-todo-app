package group.project.fixitapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import group.project.fixitapp.services.GeoLocationService
import group.project.fixitapp.ui.pages.AppBottomNavigation
import group.project.fixitapp.ui.pages.EditTaskScreen
import group.project.fixitapp.ui.pages.HomeScreen
import group.project.fixitapp.ui.pages.NewTaskScreen
import group.project.fixitapp.ui.pages.SettingsScreen
import group.project.fixitapp.ui.pages.TaskListScreen
import group.project.fixitapp.ui.pages.TaskLoadCriteria
import group.project.fixitapp.ui.pages.TemplateTaskListScreen
import group.project.fixitapp.ui.pages.ViewTaskScreen
import group.project.fixitapp.ui.pages.ViewTemplateTaskScreen
import group.project.fixitapp.ui.theme.FixItAppTheme
import group.project.fixitapp.ui.viewmodel.EditTaskViewModel
import group.project.fixitapp.ui.viewmodel.LocationViewModel
import group.project.fixitapp.ui.viewmodel.TaskListViewModel
import group.project.fixitapp.ui.viewmodel.TemplateTaskListViewModel
import group.project.fixitapp.utils.createNotificationChannel

class MainActivity : ComponentActivity() {
    private val gpsLocationPermissionRequest = 1

    @Composable
    fun MainApp() {
        val navController = rememberNavController()
        Scaffold(bottomBar = { AppBottomNavigation(navController, viewModel()) }) { innerPadding ->
            AppNavHost(navController, Modifier.padding(innerPadding))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val locationViewModel = viewModel<LocationViewModel>()
            GeoLocationService.locationViewModel = locationViewModel
            if (!hasPermission()) {
                requestFineLocationPermission()
            }
            createNotificationChannel(
                applicationContext,
                "FixItApp",
                "FixItApp Notification Channel"
            )
            FixItAppTheme {
                MainApp()
            }

        }
    }

    override fun onPause() {
        super.onPause()
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(GeoLocationService)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (hasPermission()) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                GeoLocationService.updateLatestLocation(location)
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0.0f,
                GeoLocationService
            )
        }
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            gpsLocationPermissionRequest
        )
    }

    private fun hasPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
                )
    }
}

object Destinations {
    const val HOME_ROUTE = "home"
    const val SETTINGS_ROUTE = "settings"

    const val TASK_LIST_MY_DAY_ROUTE = "taskList/view/myDay"
    const val TASK_LIST_UNLISTED_ROUTE = "taskList/view/unlisted"
    const val TASK_LIST_SPECIFIC_ID_ROUTE = "taskList/view/{listId}"

    const val TASK_NEW_ROUTE = "task/new"
    const val TASK_DETAIL_ROUTE = "task/view/{taskId}"
    const val TASK_EDIT_ROUTE = "task/edit/{taskId}"

    const val TEMPLATE_TASK_LIST_ROUTE = "templateTask/view"
    const val TEMPLATE_TASK_DETAIL_ROUTE = "templateTask/view/{templateTaskId}"
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(Destinations.HOME_ROUTE) {
            HomeScreen(navController)
        }
        composable(Destinations.SETTINGS_ROUTE) {
            SettingsScreen()
        }
        composable(Destinations.TASK_LIST_MY_DAY_ROUTE) {
            TaskListScreen(viewModel<TaskListViewModel>(), TaskLoadCriteria.MyDay, navController)
        }
        composable(Destinations.TASK_LIST_UNLISTED_ROUTE) {
            TaskListScreen(viewModel<TaskListViewModel>(), TaskLoadCriteria.Unlisted, navController)
        }
        composable(Destinations.TASK_LIST_SPECIFIC_ID_ROUTE) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")?.toInt() ?: -1
            TaskListScreen(
                viewModel<TaskListViewModel>(),
                TaskLoadCriteria.ListId(listId),
                navController
            )
        }
        composable(Destinations.TASK_NEW_ROUTE) {
            NewTaskScreen(navController)
        }
        composable(Destinations.TASK_DETAIL_ROUTE) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: -1
            ViewTaskScreen(taskId, navController)
        }
        composable(Destinations.TASK_EDIT_ROUTE) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: -1
            EditTaskScreen(taskId, navController, viewModel<EditTaskViewModel>())
        }
        composable(Destinations.TEMPLATE_TASK_LIST_ROUTE) {
            TemplateTaskListScreen(viewModel<TemplateTaskListViewModel>(), navController)
        }
        composable(Destinations.TEMPLATE_TASK_DETAIL_ROUTE) { backStackEntry ->
            val templateTaskId =
                backStackEntry.arguments?.getString("templateTaskId")?.toInt() ?: -1
            ViewTemplateTaskScreen(templateTaskId, navController)
        }
    }
}
