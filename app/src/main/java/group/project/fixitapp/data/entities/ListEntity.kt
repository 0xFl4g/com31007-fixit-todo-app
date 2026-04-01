package group.project.fixitapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import group.project.fixitapp.data.Converters
import java.time.LocalDateTime

@Entity(tableName = "list")
@TypeConverters(Converters::class)
data class ListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,  // Auto-generated primary key
    val name: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)


