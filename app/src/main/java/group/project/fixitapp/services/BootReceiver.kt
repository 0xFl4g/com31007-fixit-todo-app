package group.project.fixitapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import group.project.fixitapp.utils.rescheduleAllReminders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AlarmManager alarms are cleared on reboot; this re-schedules all pending
 * task reminders once the device is back up.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                rescheduleAllReminders(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
