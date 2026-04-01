package group.project.fixitapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.R
import group.project.fixitapp.ui.theme.LargePadding
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.HomeViewModel

@Composable
fun AppBottomNavigation(navController: NavController, viewModel: HomeViewModel) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showListDialog by rememberSaveable { mutableStateOf(false) }
    var newListName by rememberSaveable { mutableStateOf("") }

    BottomNavigation {
        BottomNavigationItem(
            modifier = Modifier.background(color = colorScheme.primary),
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home Icon") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate(Destinations.HOME_ROUTE) }
        )
        BottomNavigationItem(
            modifier = Modifier.background(color = colorScheme.primary),
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Icon") },
            label = { Text("Add New") },
            selected = true,
            onClick = { showDialog = true }
        )
        BottomNavigationItem(
            modifier = Modifier.background(color = colorScheme.primary),
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Icon"
                )
            },
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate(Destinations.SETTINGS_ROUTE) }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            text = {
                Column {

                    // New Task Button
                    Row(
                        modifier = Modifier
                            .background(
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(SmallPadding)
                            )
                            .padding(SmallPadding)
                            .clickable {
                                showDialog = false
                                navController.navigate(Destinations.TASK_NEW_ROUTE)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create a New Task Icon",
                            modifier = Modifier.size(LargePadding),
                            tint = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(SmallPadding)) // Add space between the icon and text
                        Text("Create a New Task", color = colorScheme.onPrimary)
                    }

                    Spacer(modifier = Modifier.height(MediumPadding))

                    Row(
                        modifier = Modifier
                            .background(
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(SmallPadding)
                            )
                            .padding(SmallPadding)
                            .clickable {
                                showDialog = false
                                newListName = "" // Reset the list name
                                showListDialog = true // Show the New List dialog
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.create_new_list),
                            contentDescription = "Create a New List Icon Icon",
                            modifier = Modifier.size(LargePadding),
                            tint = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(SmallPadding)) // Add space between the icon and text
                        Text("Create a New List", color = colorScheme.onPrimary)
                    }

                    Spacer(modifier = Modifier.height(MediumPadding))

                    Row(
                        modifier = Modifier
                            .background(
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(SmallPadding)
                            )
                            .padding(SmallPadding)
                            .clickable {
                                showDialog = false
                                navController.navigate(Destinations.TEMPLATE_TASK_LIST_ROUTE)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.create_task_from_template),
                            contentDescription = "Use Template to Create Task Icon",
                            modifier = Modifier.size(LargePadding),
                            tint = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(SmallPadding)) // Add space between the icon and text
                        Text("Use Template to Create Task", color = colorScheme.onPrimary)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorScheme.tertiary,
                        contentColor = colorScheme.onError
                    )
                ) {
                    Text(text = "Cancel")
                }

            }
        )
    }

    // New List Dialog
    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            title = { Text("New List") },
            text = {
                Column {
                    OutlinedTextField(
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorScheme.primary,
                            focusedLabelColor = colorScheme.primary
                        ),
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text("List Name") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addNewList(newListName)
                        newListName = ""
                        showListDialog = false
                        if (navController.currentDestination?.route == Destinations.HOME_ROUTE) {
                            navController.navigate(Destinations.HOME_ROUTE)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                )
                {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showListDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorScheme.tertiary,
                        contentColor = colorScheme.onError
                    )
                ) {
                    Text(text = "Cancel")
                }


            }
        )
    }
}
