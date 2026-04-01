package group.project.fixitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import group.project.fixitapp.data.Converters
import java.time.LocalDateTime

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = ListEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("list_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null, // Auto-generated primary key
    val title: String,
    @ColumnInfo(name = "due_at")
    val dueAt: LocalDateTime?,
    @ColumnInfo(name = "completed_at")
    var completedAt: LocalDateTime?,
    @ColumnInfo(name = "reminder_at")
    val reminderAt: LocalDateTime?,
    val note: String?,
    val priority: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val image: String?,
    @ColumnInfo(name = "list_id")
    val listId: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

