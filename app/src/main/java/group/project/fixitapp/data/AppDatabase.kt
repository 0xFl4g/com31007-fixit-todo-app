package group.project.fixitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import group.project.fixitapp.data.dao.ListDAO
import group.project.fixitapp.data.dao.SettingDAO
import group.project.fixitapp.data.dao.TaskDAO
import group.project.fixitapp.data.dao.TemplateDAO
import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.data.entities.SettingEntity
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(
    entities = [ListEntity::class, TaskEntity::class, TemplateEntity::class, SettingEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingDAO(): SettingDAO
    abstract fun listDAO(): ListDAO
    abstract fun taskDAO(): TaskDAO
    abstract fun templateDAO(): TemplateDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, populate: Boolean = true): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                 .apply {
                    if (populate) {
                        addCallback(roomCallback)
                    }
                }.build()
                INSTANCE = instance
                instance
            }
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabaseSetting(database.settingDAO())
                        populateDatabaseTemplate(database.templateDAO())
                    }
                }
            }
        }

        suspend fun populateDatabaseSetting(settingDao: SettingDAO) {
            settingDao.insertSetting(
                SettingEntity(
                    name = "notifyDueReminderTasks",
                    isActive = true
                )
            )
            settingDao.insertSetting(
                SettingEntity(
                    name = "notifyGeoLocatedTasks",
                    isActive = true
                )
            )
        }

        suspend fun populateDatabaseTemplate(templateDao: TemplateDAO) {
            templateDao.insertTemplate(
                TemplateEntity(
                    title = "Fix a laptop",
                    note = "Default template",
                    priority = 4,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
            templateDao.insertTemplate(
                TemplateEntity(
                    title = "Buy a new light bulb",
                    note = "Need to buy a new light bulb with the model number 1234",
                    priority = 2,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
            templateDao.insertTemplate(
                TemplateEntity(
                    title = "Push the button",
                    note = "Push the button like dah?",
                    priority = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
            templateDao.insertTemplate(
                TemplateEntity(
                    title = "Fix the garage door",
                    note = "Fix the wheel on the garage door by using the screwdriver",
                    priority = 3,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
            templateDao.insertTemplate(
                TemplateEntity(
                    title = "Reply to the email",
                    note = "Reply to the email from the boss",
                    priority = 5,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }
}
