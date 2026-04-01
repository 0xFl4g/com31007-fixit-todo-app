package group.project.fixitapp.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import group.project.fixitapp.R
import group.project.fixitapp.services.NotificationReceiver

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
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}

// 3. Set up the AlarmManager
fun scheduleAlarm(context: Context, timeInMillis: Long, pendingIntent: PendingIntent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
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
@SuppressLint("MissingPermission")
fun showNotification(
    context: Context,
    notificationId: Int,
    notification: NotificationCompat.Builder
) {
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notification.build())
    }
}