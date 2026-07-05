package group.project.fixitapp.ui.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.ui.theme.LargePadding
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.Orange
import group.project.fixitapp.ui.theme.OrangeDark
import group.project.fixitapp.ui.theme.OrangeLight
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.LocationViewModel
import group.project.fixitapp.ui.viewmodel.NewTaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun NewTaskContent(
    newTaskViewModel: NewTaskViewModel,
    navController: NavController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(MediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Create Task",
            style = MaterialTheme.typography.headlineMedium, // Adjust typography as per your theme
            color = colorScheme.primary // Adjust color as per your theme
        )

        Spacer(modifier = Modifier.height(MediumPadding))

        val taskTitle = taskTitleInput("")
        Spacer(modifier = Modifier.height(MediumPadding))

        val taskDueAt = taskDueDateSelection(null)
        Spacer(modifier = Modifier.height(MediumPadding))

        val taskReminderAt = taskReminderDropdown(taskDueAt, null)
        Spacer(modifier = Modifier.height(MediumPadding))

        val (taskLatitude, taskLongitude) = taskLocationSelection(null, null)
        Spacer(modifier = Modifier.height(MediumPadding))

        val taskPriority = taskPrioritySetter(0)
        Spacer(modifier = Modifier.height(MediumPadding))

        val lists by newTaskViewModel.lists.collectAsState()
        val taskSelectedListId = taskListDropdown(lists, -1)
        Spacer(modifier = Modifier.height(MediumPadding))

        val taskNote = taskNotes("")
        Spacer(modifier = Modifier.height(MediumPadding))

        val taskImageBitmap = taskImagePicker(null)
        Spacer(modifier = Modifier.height(MediumPadding))

        AddTaskButton(
            viewModel = newTaskViewModel,
            navController = navController,
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

@Composable
fun taskTitleInput(initialTitle: String): String {
    var taskTitle by rememberSaveable { mutableStateOf(initialTitle) }

    Text(text = "Title for Task", style = MaterialTheme.typography.titleMedium)

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = taskTitle,
            onValueChange = { taskTitle = it },
            label = { Text("New Task") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                focusedLabelColor = colorScheme.primary
            )
        )
    }

    return taskTitle
}

@Composable
fun taskDueDateSelection(
    initialDueDate: LocalDateTime?
): LocalDateTime? {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm", Locale.ENGLISH)
    val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

    val context = LocalContext.current

    // Calendar instance to get the current date and time
    val calendar = Calendar.getInstance()
    val currentYear = calendar[Calendar.YEAR]
    val currentMonth = calendar[Calendar.MONTH]
    val currentDay = calendar[Calendar.DAY_OF_MONTH]
    val currentHour = calendar[Calendar.HOUR_OF_DAY]
    val currentMinute = calendar[Calendar.MINUTE]

    // Mutable states to hold the selected date and time
    var dueDate by rememberSaveable {
        mutableStateOf(
            initialDueDate?.format(dateFormatter) ?: ""
        )
    }
    var dueTime by rememberSaveable {
        mutableStateOf(
            initialDueDate?.format(timeFormatter) ?: ""
        )
    }

    // Creating the TimePickerDialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            dueTime = String.format("%02d:%02d", hourOfDay, minute)
        }, currentHour, currentMinute, true // Use 'true' for 24-hour format
    )

    // Creating the DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dueDate = "$dayOfMonth/${month + 1}/$year"
            // Show TimePickerDialog after a date is picked
            timePickerDialog.show()
        }, currentYear, currentMonth, currentDay
    )

    Text(text = "Set Due Date and Time", style = MaterialTheme.typography.titleMedium)

    Button(
        onClick = { datePickerDialog.show() },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        if (dueDate.isEmpty() && dueTime.isEmpty()) {
            Text("None")
        } else
            Text("$dueDate $dueTime")
    }

    return if (dueDate.isNotEmpty() && dueTime.isNotEmpty()) LocalDateTime.parse(
        "$dueDate $dueTime",
        dateTimeFormatter
    ) else initialDueDate
}

@Composable
fun taskImagePicker(initialPickedImageBitmap: ImageBitmap?): ImageBitmap? {
    var pickedImageBitmap by remember { mutableStateOf(initialPickedImageBitmap) }

    val context = LocalContext.current
    val imageFromGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val imageBitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, uri)
                ).asImageBitmap()
                pickedImageBitmap = imageBitmap
            } catch (_: Exception) {
                pickedImageBitmap = null
            }
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "Add Icon",
                Modifier.padding(end = 4.dp)
            )
            TextButton(
                onClick = {
                    imageFromGalleryLauncher.launch(
                        PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(
                    "Add Image",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.primary
                )
            }
        }

        // Image display row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pickedImageBitmap?.let { imageBitmap ->
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageBitmap.width.toFloat() / imageBitmap.height.toFloat())
                        .padding(SmallPadding)
                )
            }
        }
    }

    return pickedImageBitmap
}

@Composable
fun taskReminderDropdown(
    parsedDueDate: LocalDateTime?,
    initialReminderAt: LocalDateTime?
): LocalDateTime? {
    var expanded by remember { mutableStateOf(false) }

    val reminderOptions = listOf("None", "30 minutes before", "1 hour before", "1 day before")
    var selectedReminderOption by rememberSaveable { mutableStateOf(reminderOptions[0]) }
    var reminderTime by rememberSaveable {
        mutableStateOf(
            initialReminderAt?.format(
                DateTimeFormatter.ofPattern("d/M/yyyy HH:mm", Locale.ENGLISH)
            )
        )
    }

    Column {
        Text(text = "Set Reminder", style = MaterialTheme.typography.titleMedium)

        TextButton(
            onClick = {
                if (parsedDueDate != null) {
                    expanded = true
                }
            },
            colors = ButtonDefaults.textButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {
            // Display "None" if reminderTime is null or empty, otherwise display the selected reminder time
            Text(reminderTime?.takeIf { it.isNotEmpty() } ?: "None")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            reminderOptions.forEach { reminderOption ->
                DropdownMenuItem(
                    text = { Text(reminderOption) },
                    onClick = {
                        selectedReminderOption = reminderOption
                        // Calculate reminder time based on the selected option
                        reminderTime = when (reminderOption) {
                            "None" -> "" // Set reminderTime to empty when "None" is selected
                            "30 minutes before" -> parsedDueDate?.minusMinutes(30)?.format(
                                DateTimeFormatter.ofPattern(
                                    "d/M/yyyy HH:mm",
                                    Locale.ENGLISH
                                )
                            )
                                .toString()

                            "1 hour before" -> parsedDueDate?.minusHours(1)?.format(
                                DateTimeFormatter.ofPattern(
                                    "d/M/yyyy HH:mm",
                                    Locale.ENGLISH
                                )
                            )
                                .toString()

                            "1 day before" -> parsedDueDate?.minusDays(1)?.format(
                                DateTimeFormatter.ofPattern(
                                    "d/M/yyyy HH:mm",
                                    Locale.ENGLISH
                                )
                            )
                                .toString()

                            else -> ""
                        }
                        expanded = false
                    }
                )
            }
        }
    }

    return if (reminderTime?.isNotEmpty() == true) LocalDateTime.parse(
        reminderTime,
        DateTimeFormatter.ofPattern("d/M/yyyy HH:mm", Locale.ENGLISH)
    ) else null
}

@Composable
fun taskLocationSelection(
    initialLatitude: Double?,
    initialLongitude: Double?
): Pair<Double?, Double?> {
    val locationViewModel = viewModel<LocationViewModel>()
    var showLocationDialog by rememberSaveable { mutableStateOf(false) }
    var latitude by rememberSaveable { mutableStateOf(initialLatitude) }
    var longitude by rememberSaveable { mutableStateOf(initialLongitude) }

    Text(text = "Set Location", style = MaterialTheme.typography.titleMedium)

    Button(
        onClick = { showLocationDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Icon(
            Icons.Outlined.Place,
            contentDescription = "Location Icon"
        ) // Add this line to add an icon
        Text("Location")
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Set Location") },
            text = {
                Column {
                    OutlinedTextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            focusedLabelColor = colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = latitude?.toString() ?: "",
                        onValueChange = { newValue ->
                            latitude = if (newValue.isEmpty()) null else newValue.toDoubleOrNull()
                        },
                        label = { Text("Latitude") },
                        placeholder = { if (latitude == null) Text("Latitude") }
                    )
                    OutlinedTextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            focusedLabelColor = colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = longitude?.toString() ?: "",
                        onValueChange = { newValue ->
                            longitude = if (newValue.isEmpty()) null else newValue.toDoubleOrNull()
                        },
                        label = { Text("Longitude") },
                        placeholder = { if (longitude == null) Text("Longitude") }
                    )
                    Button(
                        onClick = {
                            // Fetching logic using group.project.fixitapp.ui.viewmodel.LocationViewModel
                            locationViewModel.fetchLocation()
                            latitude = locationViewModel.latitude
                            longitude = locationViewModel.longitude
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("Fetch Current Coordinates")
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .padding(all = SmallPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showLocationDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.tertiary,
                            contentColor = colorScheme.onError
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(SmallPadding))
                    Button(
                        onClick = {
                            showLocationDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        )
    }

    return Pair(latitude, longitude)
}

@Composable
fun taskPrioritySetter(initialPriority: Int): Int {
    var taskPriority by rememberSaveable { mutableIntStateOf(initialPriority) }

    Column {
        Spacer(modifier = Modifier.height(SmallPadding))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = "Set Priority Icon",
                modifier = Modifier.size(LargePadding)
            )
            Spacer(modifier = Modifier.width(SmallPadding)) // Adds spacing between icon and text
            Text(
                "Set Priority",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(SmallPadding))

        // Priority Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Low", fontSize = 12.sp)
            Text("High", fontSize = 12.sp)
        }

        // Slider
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = taskPriority.toFloat(),
            onValueChange = { taskPriority = it.toInt() },
            valueRange = 0f .. 5f,
            steps = 4,
            colors = SliderDefaults.colors(
                thumbColor = Orange,
                activeTrackColor = OrangeLight,
                inactiveTrackColor = OrangeDark
            )
        )
    }

    return taskPriority
}

@Composable
fun taskListDropdown(
    taskLists: List<ListEntity>,
    initialSelectedListId: Int?
): Int? {
    var expanded by remember { mutableStateOf(false) }

    val idToNameMap = taskLists.associate { it.id to it.name }.toMutableMap()
    idToNameMap[null] = "None" // Add "None" option with ID null

    var selectedListId by rememberSaveable { mutableStateOf(initialSelectedListId) }
    var selectedListName by rememberSaveable {
        mutableStateOf(
            idToNameMap[selectedListId] ?: "None"
        )
    }

    Column {
        Text(text = "Select List", style = MaterialTheme.typography.titleMedium)

        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {
            Text(selectedListName)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            idToNameMap.forEach { (listId, listName) ->
                DropdownMenuItem(
                    text = { Text(listName) },
                    onClick = {
                        selectedListName = listName
                        selectedListId = listId
                        expanded = false
                    }
                )
            }
        }
    }

    return selectedListId
}

@Composable
fun taskNotes(initialNote: String?): String? {
    var taskNote by rememberSaveable { mutableStateOf(initialNote ?: "") }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = taskNote,
        onValueChange = { taskNote = it },
        label = { Text("Add Notes") },
        textStyle = TextStyle(fontSize = 16.sp),
        minLines = 3,
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            focusedLabelColor = colorScheme.primary
        )
    )

    return taskNote.ifBlank { null }
}

@Composable
fun AddTaskButton(
    viewModel: NewTaskViewModel,
    navController: NavController,
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
                val selectedListIdValue = if (selectedListId == -1) null else selectedListId

                viewModel.addNewTask(
                    navController,
                    title,
                    note,
                    priority,
                    reminderAt,
                    dueAt,
                    latitude,
                    longitude,
                    image,
                    selectedListIdValue
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(SmallPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )
    ) {
        Text(text = "Add Task")
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
fun NewTaskScreen(navController: NavController, viewModel: NewTaskViewModel = viewModel()) {
    Scaffold(
        topBar = {
            HomeTopBar()
        },
        content = { paddingValues ->
            NewTaskContent(
                newTaskViewModel = viewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    )
}