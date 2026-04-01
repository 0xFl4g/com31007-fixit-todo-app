package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ViewTemplateTaskViewModel(app: Application) : AndroidViewModel(app) {
    private val taskDAO = AppDatabase.getDatabase(app).taskDAO()
    private val templateDAO = AppDatabase.getDatabase(app).templateDAO()

    private val _templateTask = MutableStateFlow<TemplateEntity?>(null)
    val templateTask: StateFlow<TemplateEntity?> = _templateTask

    fun loadTemplateTask(id: Int) {
        viewModelScope.launch {
            _templateTask.value = templateDAO.getTemplateById(id)
        }
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