package group.project.fixitapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.R
import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.HomeViewModel

@Composable
fun HomeTopBar() {
    TopAppBar(
        //title
        title = { Text("FixIt", color = colorScheme.onPrimary) },
        backgroundColor = colorScheme.primary,
        navigationIcon = {
            // Adding padding inside the icon while maintaining its size
            Box(modifier = Modifier.padding(start = MediumPadding)) {
                Icon(
                    painter = painterResource(id = R.drawable.fixit_icon),
                    contentDescription = "App Logo",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    )
}

@Composable
fun ListItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(MediumPadding)
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(MediumPadding))
        Text(text)
    }
}

@Composable
fun HomeContent(modifier: Modifier, viewModel: HomeViewModel, navController: NavController) {
    val userLists by viewModel.userLists.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Task Lists",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = MediumPadding, top = MediumPadding)
        )

        ListItem(
            Icons.Default.Home,
            "My Day"
        ) { navController.navigate(Destinations.TASK_LIST_MY_DAY_ROUTE) }
        ListItem(
            Icons.AutoMirrored.Filled.List,
            "Unlisted"
        ) { navController.navigate(Destinations.TASK_LIST_UNLISTED_ROUTE) }

        Spacer(modifier = Modifier.height(SmallPadding))
        Divider()
        Spacer(modifier = Modifier.height(SmallPadding))

        UserListsSection(userLists, navController)
    }
}

@Composable
fun UserListsSection(userLists: List<ListEntity>, navController: NavController) {

    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showSort by rememberSaveable { mutableStateOf(false) }
    var default by rememberSaveable { mutableStateOf(true) }
    var newName by rememberSaveable { mutableStateOf("") }
    var currentListId by rememberSaveable { mutableIntStateOf(0) }

    val homeViewModel = viewModel<HomeViewModel>()

    Column {
        // Subtitle for the User Lists
        Text(
            text = "Your Lists",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = MediumPadding)
        )
    }


    Column(modifier = Modifier, verticalArrangement = Arrangement.Center) {
        Row(modifier = Modifier, horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { showSort = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SmallPadding),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("Sort Lists")
            }
        }

        if (userLists.isEmpty()) {
            // Message when there are no lists
            Text(
                text = "You have no lists",
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(MediumPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                items(userLists) { list ->
                    Row {
                        Column {
                            ListItem(Icons.AutoMirrored.Filled.List, list.name) {
                                navController.navigate(
                                    Destinations.TASK_LIST_SPECIFIC_ID_ROUTE.replace(
                                        "{listId}",
                                        list.id.toString()
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Column {
                                    TextButton(
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = colorScheme.secondary
                                        ),
                                        onClick = { showEdit = true; currentListId = list.id!! },
                                        modifier = Modifier,
                                    ) {
                                        Text("Edit")
                                    }
                                }

                                Column {
                                    TextButton(
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Transparent,
                                            contentColor = colorScheme.secondary
                                        ),
                                        onClick = { showDelete = true; currentListId = list.id!! },
                                        modifier = Modifier,
                                    ) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }

                    }

                    if (showEdit) {
                        AlertDialog(
                            onDismissRequest = { showEdit = false },
                            text = {
                                OutlinedTextField(
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = colorScheme.primary,
                                        focusedLabelColor = colorScheme.primary
                                    ),
                                    value = newName,
                                    onValueChange = { newName = it },
                                    label = { Text("New List Name") }
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        currentListId.let {
                                            homeViewModel.editListName(
                                                it,
                                                newName
                                            )
                                        }
                                        showEdit = false
                                        navController.navigate(Destinations.HOME_ROUTE)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary
                                    )
                                ) {
                                    Text("Rename List")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showEdit = false },
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

                    if (showDelete) {
                        AlertDialog(
                            onDismissRequest = { showDelete = false },
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SmallPadding),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "Are you sure you want to delete list?")
                                }
                            },
                            confirmButton = {
                                Row {
                                    TextButton(
                                        onClick = {
                                            showDelete = false
                                            homeViewModel.deleteListWithoutUnlisted(currentListId)
                                            navController.navigate(Destinations.HOME_ROUTE)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = colorScheme.error,
                                            contentColor = colorScheme.onError
                                        )
                                    ) {
                                        Text("Delete List and Tasks")
                                    }
                                }
                                Row {
                                    TextButton(
                                        onClick = {
                                            showDelete = false
                                            homeViewModel.deleteListMoveUnlisted(
                                                currentListId,
                                                navController
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = colorScheme.secondary,
                                            contentColor = colorScheme.onSecondary
                                        )
                                    ) {
                                        Text("Delete List and move tasks to Unlisted")
                                    }
                                }
                            },
                            dismissButton = {
                                Row {
                                    Button(
                                        onClick = {
                                            showDelete = false
                                            navController.navigate(Destinations.HOME_ROUTE)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = colorScheme.tertiary,
                                            contentColor = colorScheme.onError
                                        )
                                    ) {
                                        Text(text = "Cancel")
                                    }
                                }
                            }
                        )
                    }

                    if (showSort) {
                        AlertDialog(
                            onDismissRequest = { showSort = false },
                            title = { Text("Sort lists by") },
                            text = {
                                Column {
                                    HomeViewModel.ListSortOrder.values().forEach { order ->
                                        Text(
                                            text = order.name,
                                            modifier = Modifier
                                                .padding(top = SmallPadding, bottom = SmallPadding)
                                                .clickable {

                                                    homeViewModel.changeSortOrder(order)
                                                    default = false
                                                    showSort = false
                                                }
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showSort = false },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary
                                    )
                                ) {
                                    Text("Close")
                                }
                            }
                        )
                    }

                    if (default) {
                        homeViewModel.changeSortOrder(HomeViewModel.ListSortOrder.Name)
                    }


                }
            }
        }

    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    Scaffold(
        topBar = { HomeTopBar() }
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier
                .padding(innerPadding)
                .padding(
                    start = MediumPadding,
                    end = MediumPadding
                ), // Add consistent start padding
            viewModel = viewModel,
            navController = navController
        )
    }
}
