package group.project.fixitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import group.project.fixitapp.data.Converters
import java.time.LocalDateTime

@Entity(
    tableName = "setting",
    indices = [Index(value = ["name"], unique = true)]  // Unique constraint on 'name'
)
@TypeConverters(Converters::class)
data class SettingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null, // Auto-generated primary key
    val name: String,
    @ColumnInfo(name = "is_active")
    var isActive: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
