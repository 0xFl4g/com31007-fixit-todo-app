package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.SettingEntity
import group.project.fixitapp.utils.cancelTaskReminder
import group.project.fixitapp.utils.cleanupTaskArtifacts
import group.project.fixitapp.utils.rescheduleAllReminders
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val settingDao = AppDatabase.getDatabase(app).settingDAO()
    private val taskDao = AppDatabase.getDatabase(app).taskDAO()
    private val listDao = AppDatabase.getDatabase(app).listDAO()

    var notifyDueReminderTasks by mutableStateOf(true)
    var notifyGeoLocatedTasks by mutableStateOf(true)

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = settingDao.getAllSettings()
            for (entity in settings) {
                when (entity.name) {
                    "notifyDueReminderTasks" -> notifyDueReminderTasks = entity.isActive
                    "notifyGeoLocatedTasks" -> notifyGeoLocatedTasks = entity.isActive
                }
            }
        }
    }

    fun updateNotifyDueReminderTasks(enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = settingDao.getSettingByName("notifyDueReminderTasks")
            currentSetting?.let {
                it.isActive = enabled
                it.updatedAt = LocalDateTime.now()
                updateSetting(it)
            }

            // Apply the toggle to already-existing reminders
            val app = getApplication<Application>()
            if (enabled) {
                rescheduleAllReminders(app)
            } else {
                for (task in taskDao.getAllTasks()) {
                    task.id?.let { cancelTaskReminder(app, it) }
                }
            }
        }
    }

    fun updateNotifyGeoLocatedTasks(enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = settingDao.getSettingByName("notifyGeoLocatedTasks")
            currentSetting?.let {
                it.isActive = enabled
                it.updatedAt = LocalDateTime.now()
                updateSetting(it)
            }
        }
    }

    // Suspends (rather than launching its own coroutine) so callers can rely on the
    // row being persisted before they act on it — e.g. rescheduleAllReminders re-reads
    // this setting from the DB and would otherwise see a stale value.
    private suspend fun updateSetting(setting: SettingEntity) {
        settingDao.updateSetting(setting)
        when (setting.name) {
            "notifyDueReminderTasks" -> notifyDueReminderTasks = setting.isActive
            "notifyGeoLocatedTasks" -> notifyGeoLocatedTasks = setting.isActive
        }
    }

    fun deleteEverything() {
        viewModelScope.launch {
            for (task in taskDao.getAllTasks()) {
                cleanupTaskArtifacts(getApplication(), task)
            }
            taskDao.deleteThemAll()
            listDao.deleteThemAll()
        }
    }
}
