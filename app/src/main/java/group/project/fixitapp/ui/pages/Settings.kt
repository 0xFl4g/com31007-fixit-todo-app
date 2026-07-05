package group.project.fixitapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary),
        title = { Text("Settings", color = colorScheme.onPrimary) },
        navigationIcon = {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings Logo",
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .padding(start = MediumPadding)
                    .size(40.dp)
            )
        }
    )
}

@Composable
fun SettingsContent(viewModel: SettingsViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showWarning by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = MediumPadding, end = MediumPadding, top = MediumPadding)
    ) {
        Row(modifier = Modifier.padding(MediumPadding)) {
            Text(text = "Notifications", fontSize = 20.sp)
        }
        HorizontalDivider(color = Color.Black)
        Spacer(modifier = Modifier.height(SmallPadding))

        // Notification for due reminder tasks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notification for due reminder tasks",
                fontSize = 15.sp
            )
            Switch(
                checked = viewModel.notifyDueReminderTasks,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        viewModel.updateNotifyDueReminderTasks(isChecked)
                    }
                }
            )
        }

        // Notification for geo-located tasks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notification for geo-located tasks",
                fontSize = 15.sp
            )
            Switch(
                checked = viewModel.notifyGeoLocatedTasks,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        viewModel.updateNotifyGeoLocatedTasks(isChecked)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(400.dp))

        // Reset Data
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showWarning = true }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondary, contentColor = colorScheme.onSecondary
                )
            ) {
                Text(text = "Reset Data")
            }
        }

        if (showWarning) {
            AlertDialog(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SmallPadding),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Are you sure you want to delete all lists and tasks saved? \n\nThis process is non-reversible")
                    }
                },
                onDismissRequest = { showWarning = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SmallPadding),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showWarning = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.tertiary,
                                contentColor = colorScheme.onError
                            )
                        ) {
                            Text(text = "Cancel")
                        }
                        Button(
                            onClick = {
                                viewModel.deleteEverything(); showWarning = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.error,
                                contentColor = colorScheme.onError
                            )
                        ) {
                            Text(text = "Reset All Data")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { SettingsTopBar() }
    ) { _ ->
        SettingsContent(viewModel<SettingsViewModel>())
    }
}


