package group.project.fixitapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.TaskListViewModel

// Define an enum or sealed class for the criteria
sealed class TaskLoadCriteria {
    object MyDay : TaskLoadCriteria()
    object Unlisted : TaskLoadCriteria()
    data class ListId(val id: Int) : TaskLoadCriteria()
}

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    criteria: TaskLoadCriteria,
    navController: NavController
) {
    val listNameState by viewModel.listName.collectAsState()
    var listName = "Default"
    when (criteria) {
        is TaskLoadCriteria.MyDay -> {
            listName = "My Day"
            viewModel.loadMyDayTasks()
        }

        is TaskLoadCriteria.Unlisted -> {
            listName = "Unlisted"
            viewModel.loadUnlistedTasks()
        }

        is TaskLoadCriteria.ListId -> {
            viewModel.loadListName(criteria.id)
            listName = listNameState
            viewModel.loadTasksByListId(criteria.id)
        }
    }

    val tasks by viewModel.tasks.collectAsState()
    // Sort tasks based on completion status
    val sortedIfCompletedTasks = tasks.sortedBy { it.completedAt != null }

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorScheme.primary,
                title = { Text("Tasks", color = colorScheme.onPrimary) },
                actions = {
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Sort",
                            tint = colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column {
            Text(
                text = "List: $listName",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(MediumPadding)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(sortedIfCompletedTasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = {
                            navController.navigate(
                                Destinations.TASK_DETAIL_ROUTE.replace(
                                    "{taskId}", task.id.toString()
                                )
                            )
                        },
                        viewModel = viewModel,
                        navController = navController,
                        criteria = criteria
                    )
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Sort tasks by") },
                text = {
                    Column {
                        TaskListViewModel.TaskSortOrder.values().forEach { order ->
                            Text(
                                text = order.name,
                                modifier = Modifier
                                    .padding(top = SmallPadding, bottom = SmallPadding)
                                    .clickable {
                                        when (criteria) {
                                            is TaskLoadCriteria.ListId -> {
                                                viewModel.changeSortOrder(order, criteria.id)
                                            }

                                            is TaskLoadCriteria.MyDay -> {
                                                viewModel.changeSortOrderForMyDay(order)
                                            }

                                            is TaskLoadCriteria.Unlisted -> {
                                                viewModel.changeSortOrderForUnlisted(order)
                                            }
                                        }
                                        showDialog.value = false
                                    }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog.value = false },
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
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onTaskClick: () -> Unit,
    viewModel: TaskListViewModel,
    navController: NavController,
    criteria: TaskLoadCriteria
) {
    Card( // Wrap the Row in a Card
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick)
            .padding(MediumPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(SmallPadding)
        ) {
            Checkbox(
                checked = (task.completedAt != null),
                onCheckedChange = { isChecked ->
                    // Handle task completion
                    if (isChecked) {
                        // If the checkbox is checked, set the completedAt property to the current time
                        viewModel.completeTask(task.id!!)
                    } else {
                        // If the checkbox is unchecked, set the completedAt property to null
                        viewModel.reopenTask(task.id!!)
                    }

                    if (criteria is TaskLoadCriteria.MyDay) {
                        navController.navigate(
                            Destinations.TASK_LIST_MY_DAY_ROUTE
                        )
                    } else if (task.listId != null) {
                        navController.navigate(
                            Destinations.TASK_LIST_SPECIFIC_ID_ROUTE.replace(
                                "{listId}",
                                task.listId.toString()
                            )
                        )
                    } else {
                        navController.navigate(
                            Destinations.TASK_LIST_UNLISTED_ROUTE
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(SmallPadding))

            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.subtitle1)
                task.note?.let {
                    Text(it, style = MaterialTheme.typography.body2)
                }
                // Add more task details like due date, priority, etc.
            }
        }
    }
}

