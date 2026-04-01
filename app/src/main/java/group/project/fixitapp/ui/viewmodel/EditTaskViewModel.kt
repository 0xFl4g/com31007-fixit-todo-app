package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.entities.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditTaskViewModel(app: Application) : TaskViewModel(app) {
    private val _task = MutableStateFlow<TaskEntity?>(null)
    val task: StateFlow<TaskEntity?> get() = _task.asStateFlow()

    fun loadTaskById(taskId: Int) {
        viewModelScope.launch {
            _task.value = taskDao.getTaskById(taskId)
        }
    }

    fun editTask(
        navController: NavController,
        id: Int,
        title: String,
        note: String?,
        priority: Int,
        reminderDate: LocalDateTime?,
        dueDate: LocalDateTime?,
        lat: Double?,
        long: Double?,
        bitmap: ImageBitmap?,
        listId: Int?
    ) {
        viewModelScope.launch {
            // Step 1: Get the task
            val fetchedTask = taskDao.getTaskById(id) ?: return@launch

            // Step 2: Save the new image and get the path
            var imagePath = fetchedTask.image
            bitmap?.let {
                imagePath =
                    saveBitmapToFile(getApplication<Application>().applicationContext, it, id)
            }

            val updatedTask = fetchedTask.copy(
                title = title,
                note = note,
                updatedAt = LocalDateTime.now(),
                priority = priority,
                reminderAt = reminderDate,
                dueAt = dueDate,
                latitude = lat,
                longitude = long,
                image = imagePath,
                listId = listId
            )

            taskDao.updateTask(updatedTask)
            navController.navigate(
                Destinations.TASK_DETAIL_ROUTE.replace(
                    "{taskId}",
                    id.toString()
                )
            )
        }
    }
}