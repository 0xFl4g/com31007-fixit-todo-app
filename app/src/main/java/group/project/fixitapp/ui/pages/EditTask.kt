package group.project.fixitapp.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.viewmodel.EditTaskViewModel
import java.time.LocalDateTime

@Composable
fun EditTaskContent(
    modifier: Modifier,
    editTaskViewModel: EditTaskViewModel,
    navController: NavController,
    taskId: Int
) {
    LaunchedEffect(taskId) {
        editTaskViewModel.loadTaskById(taskId)
    }
    val task by editTaskViewModel.task.collectAsState()
    val localTask = task

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Edit Task",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        val taskTitle = localTask?.let { taskTitleInput(it.title) }
        Spacer(modifier = Modifier.height(16.dp))

        val taskDueAt = if (localTask != null) taskDueDateSelection(localTask.dueAt) else null
        Spacer(modifier = Modifier.height(16.dp))

        val taskReminderAt = if (localTask != null) taskReminderDropdown(
            taskDueAt,
            localTask.reminderAt
        ) else null
        Spacer(modifier = Modifier.height(16.dp))

        val (taskLatitude, taskLongitude) = if (localTask != null) taskLocationSelection(
            localTask.latitude,
            localTask.longitude
        ) else Pair(null, null)
        Spacer(modifier = Modifier.height(20.dp))

        val taskPriority = localTask?.priority?.let { taskPrioritySetter(it) }
        Spacer(modifier = Modifier.height(16.dp))

        val lists by editTaskViewModel.lists.collectAsState()

        val taskSelectedListId =
            if (localTask != null) taskListDropdown(lists, localTask.listId) else null
        Spacer(modifier = Modifier.height(16.dp))

        val taskNote = if (localTask != null) taskNotes(localTask.note) else null
        Spacer(modifier = Modifier.height(16.dp))

        val taskImageBitmap = if (localTask != null) taskImagePicker(localTask.image?.let {
            editTaskViewModel.getBitmapFromFile(
                it
            )
        }) else null
        Spacer(modifier = Modifier.height(16.dp))

        if (taskTitle != null && taskPriority != null) {
            EditTaskButton(
                viewModel = editTaskViewModel,
                navController = navController,
                taskId = taskId,
                title = taskTitle,
                dueAt = taskDueAt,
                reminderAt = taskReminderAt,
                note = taskNote,
                priority = taskPriority,
                latitude = taskLatitude,
                longitude = taskLongitude,
                image = taskImageBitmap,
                selectedListId = taskSelectedListId
            )
        }
    }
}

@Composable
fun EditTaskButton(
    viewModel: EditTaskViewModel,
    navController: NavController,
    taskId: Int,
    title: String,
    dueAt: LocalDateTime?,
    reminderAt: LocalDateTime?,
    note: String?,
    priority: Int,
    latitude: Double?,
    longitude: Double?,
    image: ImageBitmap?,
    selectedListId: Int?
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (title.isBlank()) {
                // Show dialog if title is empty
                showDialog = true
            } else {
                viewModel.editTask(
                    navController,
                    taskId,
                    title,
                    note,
                    priority,
                    reminderAt,
                    dueAt,
                    latitude,
                    longitude,
                    image,
                    selectedListId
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Text(text = "Update Task")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Error") },
            text = { Text("Please fill in the title") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun EditTaskScreen(
    taskId: Int,
    navController: NavController,
    viewModel: EditTaskViewModel
) {
    Scaffold(
        topBar = { HomeTopBar() } // Assuming an existing Composable for the top bar
    ) { innerPadding ->
        EditTaskContent(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = MediumPadding, end = MediumPadding),
            editTaskViewModel = viewModel,
            navController = navController,
            taskId = taskId
        )
    }
}
