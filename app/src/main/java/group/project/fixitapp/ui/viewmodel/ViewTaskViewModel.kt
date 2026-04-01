package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.data.entities.TemplateEntity
import group.project.fixitapp.services.GeoLocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.sqrt

class ViewTaskViewModel(app: Application) : TaskViewModel(app) {
    private val templateDao = AppDatabase.getDatabase(app).templateDAO() // Add this line

    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> get() = _task.asStateFlow()

    fun loadTaskById(taskId: Int) {
        viewModelScope.launch {
            _task.value = taskDao.getTaskById(taskId)
        }
    }

    fun deleteTask(id: Int, navController: NavController) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(id) ?: return@launch
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

    fun distanceFromLocation(taskLat: Double, taskLong: Double): Float {

        GeoLocationService.locationViewModel?.fetchLocation()
        val currentLat = GeoLocationService.locationViewModel?.latitude
        val currentLong = GeoLocationService.locationViewModel?.longitude

        var distance = 0f

        if (currentLat != null && currentLong != null) {
            distance =
                ((currentLat.toFloat() - taskLat.toFloat()).pow(2) + (currentLong.toFloat() - taskLong.toFloat()).pow(
                    2
                ))
        }
        return sqrt(distance * 11139)
    }

    fun completeTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskCompleted(id, LocalDateTime.now())
        }
    }

    fun reopenTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskReopen(id, LocalDateTime.now())
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