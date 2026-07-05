package group.project.fixitapp.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import group.project.fixitapp.R
import group.project.fixitapp.services.NotificationReceiver
import java.time.LocalDateTime
import java.time.ZoneId

const val NOTIFICATION_CHANNEL_ID = "FixItApp"

// Geo-proximity notifications use a separate ID range so they never collide
// with reminder notifications, which use the task ID directly.
const val GEO_NOTIFICATION_ID_OFFSET = 100_000

// 1. Create a NotificationChannel (Oreo and above)
fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelId, channelName, importance)
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

// 2. Create a PendingIntent
fun createPendingIntent(
    context: Context,
    channelId: String,
    notificationId: Int,
    title: String,
    content: String
): PendingIntent {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("notificationId", notificationId)
        putExtra("channelId", channelId)
        putExtra("title", title)
        putExtra("content", content)
    }
    return PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

// 3. Set up the AlarmManager
fun scheduleAlarm(context: Context, timeInMillis: Long, pendingIntent: PendingIntent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }
}

/**
 * Schedules a reminder notification for a task. Reminders in the past are ignored.
 * The task ID doubles as the notification ID and PendingIntent request code, so
 * each task has at most one scheduled reminder.
 */
fun scheduleTaskReminder(context: Context, taskId: Int, title: String, reminderAt: LocalDateTime) {
    val timeInMillis = reminderAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    if (timeInMillis <= System.currentTimeMillis()) return

    val pendingIntent = createPendingIntent(
        context,
        NOTIFICATION_CHANNEL_ID,
        taskId,
        title,
        "Reminder: this task is due soon"
    )
    scheduleAlarm(context, timeInMillis, pendingIntent)
}

/** Cancels a previously scheduled reminder for the task, if any. */
fun cancelTaskReminder(context: Context, taskId: Int) {
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        taskId,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    ) ?: return
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
    pendingIntent.cancel()
}

// 4. Create a Notification
fun createNotification(
    context: Context,
    channelId: String,
    title: String,
    content: String
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.fixit_icon)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
}

// 5. Show the Notification
@SuppressLint("MissingPermission") // guarded by areNotificationsEnabled()
fun showNotification(
    context: Context,
    notificationId: Int,
    notification: NotificationCompat.Builder
) {
    with(NotificationManagerCompat.from(context)) {
        if (!areNotificationsEnabled()) return
        notify(notificationId, notification.build())
    }
}
