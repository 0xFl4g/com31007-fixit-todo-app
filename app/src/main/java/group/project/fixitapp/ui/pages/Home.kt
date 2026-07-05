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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import group.project.fixitapp.utils.ListSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        //title
        title = { Text("FixIt", color = colorScheme.onPrimary) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary),
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
            style = MaterialTheme.typography.titleLarge,
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
        HorizontalDivider()
        Spacer(modifier = Modifier.height(SmallPadding))

        UserListsSection(userLists, navController)
    }
}

@Composable
fun UserListsSection(userLists: List<ListEntity>, navController: NavController) {

    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showSort by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var currentListId by rememberSaveable { mutableIntStateOf(0) }

    val homeViewModel = viewModel<HomeViewModel>()

    Column {
        // Subtitle for the User Lists
        Text(
            text = "Your Lists",
            style = MaterialTheme.typography.titleLarge,
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
                    containerColor = colorScheme.primary,
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
                style = MaterialTheme.typography.titleSmall,
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
                                            containerColor = Color.Transparent,
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
                                            containerColor = Color.Transparent,
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
                }
            }
        }
    }

    if (showEdit) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            text = {
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
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
                        homeViewModel.editListName(currentListId, newName)
                        showEdit = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
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
                        containerColor = colorScheme.tertiary,
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
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
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
                            homeViewModel.deleteListMoveUnlisted(currentListId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.secondary,
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
                        onClick = { showDelete = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.tertiary,
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
                    ListSortOrder.entries.forEach { order ->
                        Text(
                            text = order.name,
                            modifier = Modifier
                                .padding(top = SmallPadding, bottom = SmallPadding)
                                .clickable {
                                    homeViewModel.changeSortOrder(order)
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
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Text("Close")
                }
            }
        )
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
