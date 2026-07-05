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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import group.project.fixitapp.utils.TaskSortOrder

// Define an enum or sealed class for the criteria
sealed class TaskLoadCriteria {
    object MyDay : TaskLoadCriteria()
    object Unlisted : TaskLoadCriteria()
    data class ListId(val id: Int) : TaskLoadCriteria()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    criteria: TaskLoadCriteria,
    navController: NavController
) {
    val listNameState by viewModel.listName.collectAsState()
    val listName = when (criteria) {
        is TaskLoadCriteria.MyDay -> "My Day"
        is TaskLoadCriteria.Unlisted -> "Unlisted"
        is TaskLoadCriteria.ListId -> listNameState
    }

    LaunchedEffect(criteria) {
        viewModel.setCriteria(criteria)
    }

    val tasks by viewModel.tasks.collectAsState()
    // Sort tasks based on completion status
    val sortedIfCompletedTasks = tasks.sortedBy { it.completedAt != null }

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary),
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
                style = MaterialTheme.typography.titleLarge,
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
                        viewModel = viewModel
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
                        TaskSortOrder.entries.forEach { order ->
                            Text(
                                text = order.name,
                                modifier = Modifier
                                    .padding(top = SmallPadding, bottom = SmallPadding)
                                    .clickable {
                                        viewModel.changeSortOrder(order)
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
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onTaskClick: () -> Unit,
    viewModel: TaskListViewModel
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
                    // Handle task completion; the observed Flow refreshes the list
                    if (isChecked) {
                        viewModel.completeTask(task.id!!)
                    } else {
                        viewModel.reopenTask(task.id!!)
                    }
                }
            )

            Spacer(modifier = Modifier.width(SmallPadding))

            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                task.note?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                // Add more task details like due date, priority, etc.
            }
        }
    }
}

