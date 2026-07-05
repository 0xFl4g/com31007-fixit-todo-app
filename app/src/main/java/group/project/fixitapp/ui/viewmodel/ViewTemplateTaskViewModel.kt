package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ViewTemplateTaskViewModel(app: Application) : AndroidViewModel(app) {
    private val taskDAO = AppDatabase.getDatabase(app).taskDAO()
    private val templateDAO = AppDatabase.getDatabase(app).templateDAO()

    private val templateTaskId = MutableStateFlow<Int?>(null)

    val templateTask: StateFlow<TemplateEntity?> = templateTaskId
        .filterNotNull()
        .flatMapLatest { templateDAO.observeTemplateById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun loadTemplateTask(id: Int) {
        templateTaskId.value = id
    }

    fun createTaskFromTemplate(template: TemplateEntity, navController: NavController) {
        viewModelScope.launch {
            val newTask = TaskEntity(
                title = template.title,
                note = template.note,
                priority = template.priority,
                reminderAt = null,
                dueAt = null,
                latitude = null,
                longitude = null,
                image = null,
                listId = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                completedAt = null
            )
            val taskID = taskDAO.insertTask(newTask)
            navController.navigate(
                Destinations.TASK_DETAIL_ROUTE.replace(
                    "{taskId}",
                    taskID.toString()
                )
            )
        }
    }

    fun deleteTemplate(template: TemplateEntity, navController: NavController) {
        viewModelScope.launch {
            templateDAO.deleteTemplate(template)
            navController.navigate(Destinations.TEMPLATE_TASK_LIST_ROUTE)
        }
    }
}
