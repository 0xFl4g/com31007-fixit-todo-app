package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.entities.TaskEntity
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NewTaskViewModel(app: Application) : TaskViewModel(app) {
    fun addNewTask(
        navController: NavController,
        title: String,
        note: String?,
        priority: Int,
        reminder: LocalDateTime?,
        dueDate: LocalDateTime?,
        lat: Double?,
        long: Double?,
        bitmap: ImageBitmap?,
        listId: Int?
    ) {
        viewModelScope.launch {
            // Step 1: Insert the task without the image
            val newTask = TaskEntity(
                title = title,
                note = note,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                priority = priority,
                reminderAt = reminder,
                dueAt = dueDate,
                latitude = lat,
                longitude = long,
                image = null,
                completedAt = null,
                listId = listId
            )
            val taskId = taskDao.insertTask(newTask).toInt()

            // Step 2: Save the image and update the task
            bitmap?.let {
                val imagePath =
                    saveBitmapToFile(getApplication<Application>().applicationContext, it, taskId)
                val updatedTask = newTask.copy(id = taskId, image = imagePath)
                taskDao.updateTask(updatedTask)
            }

            scheduleReminderIfEnabled(newTask.copy(id = taskId))

            // redirect user to the task list page unlisted or specific depending on the list id
            if (listId == null) {
                navController.navigate(Destinations.TASK_LIST_UNLISTED_ROUTE)
            } else {
                navController.navigate(
                    Destinations.TASK_LIST_SPECIFIC_ID_ROUTE.replace(
                        "{listId}",
                        listId.toString()
                    )
                )
            }
        }
    }
}