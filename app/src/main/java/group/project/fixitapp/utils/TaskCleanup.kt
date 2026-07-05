package group.project.fixitapp.utils

import android.content.Context
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import java.io.File
import java.time.LocalDateTime

/**
 * Removes everything a task leaves behind outside the database:
 * its scheduled reminder alarm and its image file.
 */
fun cleanupTaskArtifacts(context: Context, task: TaskEntity) {
    task.id?.let { cancelTaskReminder(context, it) }
    task.image?.let { path -> File(path).delete() }
}

/**
 * Schedules reminder alarms for every incomplete task with an upcoming reminder,
 * unless reminder notifications are disabled. AlarmManager alarms don't survive
 * a reboot, so this runs on BOOT_COMPLETED and when the setting is re-enabled.
 */
suspend fun rescheduleAllReminders(context: Context) {
    val db = AppDatabase.getDatabase(context)
    if (db.settingDAO().getSettingByName("notifyDueReminderTasks")?.isActive == false) return

    val now = LocalDateTime.now()
    for (task in db.taskDAO().getAllTasks()) {
        val taskId = task.id ?: continue
        val reminderAt = task.reminderAt ?: continue
        if (task.completedAt == null && reminderAt.isAfter(now)) {
            scheduleTaskReminder(context, taskId, task.title, reminderAt)
        }
    }
}
