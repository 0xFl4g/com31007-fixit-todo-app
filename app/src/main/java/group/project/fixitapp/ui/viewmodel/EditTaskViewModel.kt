package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.entities.TaskEntity
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
class EditTaskViewModel(app: Application) : TaskViewModel(app) {
    private val taskId = MutableStateFlow<Int?>(null)

    val task: StateFlow<TaskEntity?> = taskId
        .filterNotNull()
        .flatMapLatest { taskDao.observeTaskById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun loadTaskById(id: Int) {
        taskId.value = id
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

            // Replace any previously scheduled reminder with the new one
            cancelReminder(id)
            scheduleReminderIfEnabled(updatedTask)

            navController.navigate(
                Destinations.TASK_DETAIL_ROUTE.replace(
                    "{taskId}",
                    id.toString()
                )
            )
        }
    }
}