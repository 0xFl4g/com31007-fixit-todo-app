package group.project.fixitapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import group.project.fixitapp.utils.createNotification
import group.project.fixitapp.utils.showNotification

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val channelId = intent.getStringExtra("channelId") ?: return
        val title = intent.getStringExtra("title") ?: return
        val content = intent.getStringExtra("content") ?: ""

        val notification = createNotification(context, channelId, title, content)
        showNotification(context, notificationId, notification)
    }
}