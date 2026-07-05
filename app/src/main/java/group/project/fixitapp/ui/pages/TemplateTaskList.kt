package group.project.fixitapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.entities.TemplateEntity
import group.project.fixitapp.ui.theme.MediumPadding
import group.project.fixitapp.ui.theme.SmallPadding
import group.project.fixitapp.ui.viewmodel.TemplateTaskListViewModel

@Composable
fun TemplateTaskListScreen(viewModel: TemplateTaskListViewModel, navController: NavController) {
    val templateTasks by viewModel.templateTasks.collectAsState()

    Scaffold(
        topBar = { HomeTopBar() }
    ) { contentPadding ->
        Column {
            Text(
                text = "Choose a Template Task: ",
                fontSize = 20.sp,
                modifier = Modifier.padding(MediumPadding)
            )
            LazyColumn(contentPadding = contentPadding) {
                items(templateTasks) { templateTask ->
                    TemplateTaskItem(
                        templateTask = templateTask,
                        onTaskClick = {
                            navController.navigate(
                                Destinations.TEMPLATE_TASK_DETAIL_ROUTE.replace(
                                    "{templateTaskId}",
                                    templateTask.id.toString()
                                )
                            )
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun TemplateTaskItem(templateTask: TemplateEntity, onTaskClick: () -> Unit) {

    Card( // Wrap the Row in a Card
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick)
            .padding(SmallPadding)
    ) {
        Row(
            modifier = Modifier.padding(MediumPadding)
        ) {
            Text(
                text = templateTask.title,
                modifier = Modifier.padding(MediumPadding),
                fontSize = 15.sp // Increase font size
            )
        }
    }
}