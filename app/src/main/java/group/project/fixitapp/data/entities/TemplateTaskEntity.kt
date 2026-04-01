package group.project.fixitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import group.project.fixitapp.data.Converters
import java.time.LocalDateTime

@Entity(tableName = "template_task")
@TypeConverters(Converters::class)
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null, // Auto-generated primary key
    val title: String,
    val note: String?,
    val priority: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
