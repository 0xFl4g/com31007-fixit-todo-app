package group.project.fixitapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import group.project.fixitapp.ui.theme.LargePadding
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.ViewTemplateTaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ViewTemplateTaskContent(
    modifier: Modifier,
    viewModel: ViewTemplateTaskViewModel,
    id: Int,
    navController: NavController
) {
    viewModel.loadTemplateTask(id)
    val templateTask by viewModel.templateTask.collectAsState()
    val formatterDate = DateTimeFormatter.ofPattern("d/M/yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("h:mm a")
    val formattedCreatedAt =
        formatDateTime(templateTask?.createdAt, formatterDate, formatterTime) ?: "Not available"
    val formattedUpdated =
        formatDateTime(templateTask?.updatedAt, formatterDate, formatterTime) ?: "Not available"
    var deletionAlert by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${templateTask?.title ?: "No title"}",
            style = MaterialTheme.typography.h4, // Adjust typography as per your theme
            color = colorScheme.primary // Adjust color as per your theme
        )

        Spacer(modifier = Modifier.height(MediumPadding))

        /* Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Title: ")
                }
                append(" ${templateTask?.title ?: "No title"}")
            },
            modifier = Modifier.padding(SmallPadding)
        )
        */

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Priority: ")
                }
                append("${templateTask?.priority ?: "No priority Set"}")
            },
            modifier = Modifier.padding(SmallPadding)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Note: ")
                }
                append(templateTask?.note ?: "No note")
            },
            modifier = Modifier.padding(SmallPadding)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Created At: ")
                }
                append(formattedCreatedAt)
            }, modifier = Modifier.padding(SmallPadding)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Updated At: ")
                }
                append(formattedUpdated)
            }, modifier = Modifier.padding(SmallPadding)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = LargePadding * 3),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                modifier = Modifier
                    .background(color = colorScheme.primary, shape = RoundedCornerShape(8.dp))
                    .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp),
                onClick = {
                    templateTask?.let {
                        viewModel.createTaskFromTemplate(
                            it,
                            navController
                        )
                    }
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add task from template",
                        tint = colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Add a spacer for some space between icon and text
                    Text("Create Task from Template", color = colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { deletionAlert = true },
                colors = ButtonDefaults.buttonColors(
                    contentColor = colorScheme.onError,
                    backgroundColor = colorScheme.error
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 13.dp,
                        end = 13.dp
                    )
                ) {
                    Text("Delete Template Task")
                }
            }
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

                    Text("Are you sure you want to delete this template task?")
                }

            },

            buttons = {
                Row(
                    modifier = Modifier
                        .padding(SmallPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { deletionAlert = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorScheme.tertiary,
                            contentColor = colorScheme.onError
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            templateTask?.let { viewModel.deleteTemplate(it, navController) }
                            deletionAlert = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorScheme.error,
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

@Composable
fun formatDateTime(
    dateTime: LocalDateTime?,
    formatterDate: DateTimeFormatter,
    formatterTime: DateTimeFormatter
): String? {
    return if (dateTime != null) {
        "${dateTime.format(formatterDate)} at ${dateTime.format(formatterTime)}"
    } else {
        null
    }
}

@Composable
fun ViewTemplateTaskScreen(id: Int, navController: NavController) {
    Scaffold(
        topBar = { HomeTopBar() }
    ) { innerPadding ->
        ViewTemplateTaskContent(
            Modifier
                .padding(innerPadding)
                .padding(start = MediumPadding, end = MediumPadding),
            viewModel<ViewTemplateTaskViewModel>(), id, navController
        )
    }
}