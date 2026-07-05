package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import group.project.fixitapp.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDAO {
    @Insert
    suspend fun insertTask(task: TaskEntity): Long // Returns the new ID

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE task SET completed_at = :time, updated_at = :time WHERE id = :taskId")
    suspend fun updateTaskCompleted(taskId: Int, time: LocalDateTime)

    @Query("UPDATE task SET completed_at = null, updated_at = :time WHERE id = :taskId")
    suspend fun updateTaskReopen(taskId: Int, time: LocalDateTime)

    @Query("DELETE FROM task")
    suspend fun deleteThemAll()

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND completed_at IS NULL")
    suspend fun getIncompleteTasksWithLocation(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Query("SELECT * FROM task WHERE list_id = :listId")
    suspend fun getTasksByListId(listId: Int): List<TaskEntity>

    // Observable variants — Room re-emits whenever the underlying tables change
    @Query("SELECT * FROM task WHERE id = :id")
    fun observeTaskById(id: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM task WHERE list_id = :listId")
    fun observeTasksByListId(listId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE list_id IS NULL")
    fun observeUnlistedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE date(due_at) = date(:today)")
    fun observeTasksDueToday(today: LocalDateTime): Flow<List<TaskEntity>>

    // Get list name
    @Query("SELECT name FROM list WHERE id = :listId LIMIT 1")
    suspend fun getListNameById(listId: Int): String

    @Query("UPDATE task SET list_id = null where id = :id")
    suspend fun editTaskListIdToNull(id: Int)
}
