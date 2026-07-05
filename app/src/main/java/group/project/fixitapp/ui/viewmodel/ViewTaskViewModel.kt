package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.data.entities.TemplateEntity
import group.project.fixitapp.services.GeoLocationService
import group.project.fixitapp.utils.cleanupTaskArtifacts
import group.project.fixitapp.utils.haversineDistanceMetres
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
class ViewTaskViewModel(app: Application) : TaskViewModel(app) {
    private val templateDao = AppDatabase.getDatabase(app).templateDAO()

    private val taskId = MutableStateFlow<Int?>(null)

    val task: StateFlow<TaskEntity?> = taskId
        .filterNotNull()
        .flatMapLatest { taskDao.observeTaskById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun loadTaskById(id: Int) {
        taskId.value = id
    }

    fun deleteTask(id: Int, navController: NavController) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(id) ?: return@launch
            cleanupTaskArtifacts(getApplication(), task)
            taskDao.deleteTask(task)
            if (task.listId == null) {
                navController.navigate(Destinations.TASK_LIST_UNLISTED_ROUTE)
            } else {
                navController.navigate(
                    Destinations.TASK_LIST_SPECIFIC_ID_ROUTE.replace(
                        "{listId}",
                        task.listId.toString()
                    )
                )
            }
        }
    }

    fun distanceFromLocation(taskLat: Double, taskLong: Double): Float? {
        GeoLocationService.locationViewModel?.fetchLocation()
        val currentLat = GeoLocationService.locationViewModel?.latitude ?: return null
        val currentLong = GeoLocationService.locationViewModel?.longitude ?: return null

        return haversineDistanceMetres(currentLat, currentLong, taskLat, taskLong)
    }

    fun completeTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskCompleted(id, LocalDateTime.now())
            cancelReminder(id)
        }
    }

    fun reopenTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskReopen(id, LocalDateTime.now())
            taskDao.getTaskById(id)?.let { scheduleReminderIfEnabled(it) }
        }
    }

    fun saveAsTemplate(task: TaskEntity) {
        viewModelScope.launch {
            val template = TemplateEntity(
                title = task.title,
                note = task.note,
                priority = task.priority
            )
            templateDao.insertTemplate(template)
        }
    }
}
