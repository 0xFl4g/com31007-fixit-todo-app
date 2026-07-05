package group.project.fixitapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.utils.cancelTaskReminder
import group.project.fixitapp.utils.scheduleTaskReminder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime

open class TaskViewModel(app: Application) : AndroidViewModel(app) {
    protected val taskDao = AppDatabase.getDatabase(app).taskDAO()
    protected val settingDao = AppDatabase.getDatabase(app).settingDAO()
    private val listDao = AppDatabase.getDatabase(app).listDAO()

    val lists: StateFlow<List<ListEntity>> = listDao.observeAllLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    protected fun saveBitmapToFile(context: Context, bitmap: ImageBitmap, taskId: Int): String {
        val filename = "task_image_$taskId.png"
        val file = File(context.filesDir, filename)
        val stream: OutputStream = FileOutputStream(file)

        val androidBitmap = bitmap.asAndroidBitmap()
        androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        return file.absolutePath
    }

    fun getBitmapFromFile(filename: String): ImageBitmap? {
        return BitmapFactory.decodeFile(filename)?.asImageBitmap()
    }

    /**
     * Schedules the task's reminder alarm if the task has an upcoming reminder,
     * isn't completed, and the notification setting is enabled.
     */
    protected suspend fun scheduleReminderIfEnabled(task: TaskEntity) {
        val taskId = task.id ?: return
        val reminderAt = task.reminderAt ?: return
        if (task.completedAt != null) return
        if (!reminderAt.isAfter(LocalDateTime.now())) return
        if (settingDao.getSettingByName("notifyDueReminderTasks")?.isActive == false) return
        scheduleTaskReminder(getApplication(), taskId, task.title, reminderAt)
    }

    protected fun cancelReminder(taskId: Int) {
        cancelTaskReminder(getApplication(), taskId)
    }
}
