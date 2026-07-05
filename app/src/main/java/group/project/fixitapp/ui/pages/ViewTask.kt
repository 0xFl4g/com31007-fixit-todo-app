package group.project.fixitapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.R
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.ViewTaskViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ViewTaskContent(
    modifier: Modifier,
    viewModel: ViewTaskViewModel,
    id: Int,
    navController: NavController
) {
    var showTemplateSavedDialog by rememberSaveable { mutableStateOf(false) }
    var deletionAlert by rememberSaveable { mutableStateOf(false) }

    // loads whole task, each part can be accessed through wholeTask.{fieldName}
    LaunchedEffect(id) {
        viewModel.loadTaskById(id)
    }
    val task by viewModel.task.collectAsState()
    val localTask = task

    val formatterDate = DateTimeFormatter.ofPattern("d/M/yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("h:mm a")

    val formattedCreatedAt = localTask?.createdAt?.let {
        it.format(formatterDate) + " at " + it.format(formatterTime)
    } ?: ""
    val formattedUpdated = localTask?.updatedAt?.let {
        it.format(formatterDate) + " at " + it.format(formatterTime)
    } ?: ""
    val formattedDue = localTask?.dueAt?.let {
        it.format(formatterDate) + " at " + it.format(formatterTime)
    } ?: "No Due Date Specified"
    val formattedReminder = localTask?.reminderAt?.let {
        it.format(formatterDate) + " at " + it.format(formatterTime)
    } ?: "No Reminder Date Specified"
    val formattedCompleted = localTask?.completedAt?.let {
        it.format(formatterDate) + " at " + it.format(formatterTime)
    } ?: "Not Completed"

    // Task title
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(MediumPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (localTask != null) {
                Text(
                    text = localTask.title,
                    style = MaterialTheme.typography.headlineMedium, // Adjust typography as per your theme
                    color = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Due Date
        Row(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Date Due: ")
                }
                append(formattedDue)
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        // Reminder DateTime
        Row(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Reminder at: ")
                }
                append(formattedReminder)
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Priority Level
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Priority Level: ")
                }
                if (localTask != null) {
                    append("${localTask.priority}")
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Notes
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Note: ")
                }
                if (localTask != null) {
                    if (localTask.note != null) {
                        append("${localTask.note}")
                    }
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        // Template Button
        Row(
            modifier = Modifier
                .clickable {
                    if (localTask != null) {
                        viewModel.saveAsTemplate(localTask)
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = {
                if (localTask != null) {
                    viewModel.saveAsTemplate(localTask)
                }
                showTemplateSavedDialog = true
            }) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.create_template_task),
                        contentDescription = "Create a New Template Task Icon",
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add space between the icon and text
                    Text("Save as a Template", color = colorScheme.secondary)
                }
            }
        }

        if (showTemplateSavedDialog) {
            AlertDialog(
                onDismissRequest = { showTemplateSavedDialog = false },
                title = { Text("Task Saved as Template") },
                text = { Text("The task has been saved as a template.") },
                confirmButton = {
                    Button(
                        onClick = { showTemplateSavedDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorScheme.onPrimary,
                            containerColor = colorScheme.primary
                        )
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MediumPadding, bottom = MediumPadding)
        )

        //Location
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleLarge, // Adjust typography as per your theme
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Longitude
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Longitude: ")
                }
                if (localTask != null) {
                    if (localTask.longitude != null) {
                        append(
                            String.format(
                                "%.6f°",
                                localTask.longitude
                            )
                        ) // Format the latitude to 5 decimal places
                    } else {
                        append(" Not Specified")
                    }
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding / 2))

        //Latitude
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Latitude: ")
                }
                if (localTask != null) {
                    if (localTask.latitude != null) {
                        append(
                            String.format(
                                "%.6f°",
                                localTask.latitude
                            )
                        ) // Format the latitude to 5 decimal places
                    } else {
                        append("Not Specified")
                    }
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding / 2))

        // Distance from where user is
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Distance from Location: ")
                }
                val distance = localTask?.latitude?.let { lat ->
                    localTask.longitude?.let { lon ->
                        viewModel.distanceFromLocation(lat, lon)
                    }
                }
                if (distance != null) {
                    append(String.format("%.1f metres", distance))
                } else {
                    append("Unknown")
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MediumPadding, bottom = MediumPadding)
        )

        //Task Dates
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Task Dates",
                style = MaterialTheme.typography.titleLarge, // Adjust typography as per your theme
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Date Created
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Date Created: ")
                }
                withStyle(style = SpanStyle(color = colorScheme.onTertiary)) {
                    append(formattedCreatedAt)
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Date Last Edited
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Last Edited: ")
                }
                withStyle(style = SpanStyle(color = colorScheme.onTertiary)) {
                    append(formattedUpdated)
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(SmallPadding))

        //Date Completed
        Row {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                    append("Date Completed: ")
                }
                withStyle(style = SpanStyle(color = colorScheme.onTertiary)) {
                    append(formattedCompleted)
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(modifier = Modifier.height(MediumPadding))

        //Attached Image
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Attached Image",
                style = MaterialTheme.typography.titleLarge, // Adjust typography as per your theme
                color = colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(SmallPadding))
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            val imgRef = localTask?.image
            val imageBitmap = if (!imgRef.isNullOrEmpty()) viewModel.getBitmapFromFile(imgRef) else null
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageBitmap.width.toFloat() / imageBitmap.height.toFloat())
                )
            } else {
                Text(
                    "No Image Attached to this task",
                    modifier = Modifier.padding(SmallPadding / 2)
                )
            }
        }

        Spacer(modifier = Modifier.height(MediumPadding))

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth(), // Fill the maximum width
            horizontalArrangement = Arrangement.Center // Center children horizontally
        )
        {
            // Complete / Reopen Button
            Column {
                // Button
                if (localTask != null) {
                    if (localTask.completedAt != null) {
                        Button(
                            onClick = { viewModel.reopenTask(id) },
                            modifier = Modifier
                                .padding(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "Reopen Task")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.completeTask(id) },
                            modifier = Modifier
                                .padding(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "Complete")
                        }
                    }
                }
            }

            // Edit button
            if (localTask != null) {
                if (localTask.completedAt == null) {
                    Column {
                        OutlinedButton(
                            onClick = {
                                navController.navigate(
                                    Destinations.TASK_EDIT_ROUTE.replace(
                                        "{taskId}",
                                        id.toString()
                                    )
                                )
                            },
                            modifier = Modifier.padding(5.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary)
                        ) {
                            Text(text = "Edit Task")
                        }
                    }
                }
            }

            Column {
                Button(
                    onClick = {
                        deletionAlert = true
                    },
                    modifier = Modifier
                        .padding(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError
                    )
                ) {
                    Text(text = "Delete")
                }
            }

            if (deletionAlert) {
                AlertDialog(
                    onDismissRequest = { deletionAlert = false },
                    title = {
                        Row(
                            modifier = Modifier
                                .padding(SmallPadding)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Text("Are you sure you want to delete this task?")
                        }

                    },

                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .padding(SmallPadding)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { deletionAlert = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.tertiary,
                                    contentColor = colorScheme.onError
                                )
                            ) {
                                Text(text = "Cancel")
                            }
                            Button(
                                onClick = {
                                    localTask?.id?.let {
                                        viewModel.deleteTask(
                                            it,
                                            navController
                                        )
                                    }
                                    deletionAlert = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.error,
                                    contentColor = colorScheme.onError
                                )
                            ) {
                                Text(text = "Confirm Deletion")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ViewTaskScreen(id: Int, navController: NavController) {
    Scaffold(
        topBar = { HomeTopBar() }
    ) { innerPadding ->
        ViewTaskContent(
            Modifier
                .padding(innerPadding)
                .padding(start = MediumPadding, end = MediumPadding),
            viewModel<ViewTaskViewModel>(), id, navController
        )
    }
}
