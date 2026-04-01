package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import group.project.fixitapp.data.entities.TaskEntity
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

    @Query("SELECT title FROM task WHERE id = :id LIMIT 1")
    suspend fun getTaskNameById(id: Int): String

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    //Returns the name of the title
    @Query("SELECT * FROM task WHERE title = :taskName LIMIT 1")
    suspend fun getTaskByName(taskName: String): TaskEntity?

    @Query("SELECT * FROM task WHERE list_id = :listId")
    suspend fun getTasksByListId(listId: Int): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id = :listId ORDER BY title ASC")
    suspend fun getTasksByListIdSortedByTitle(listId: Int): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id = :listId ORDER BY due_at DESC")
    suspend fun getTasksByListIdSortedByDueDate(listId: Int): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id = :listId ORDER BY priority DESC")
    suspend fun getTasksByListIdSortedByPriority(listId: Int): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id IS NULL ORDER BY title ASC")
    suspend fun getUnlistedTasksSortedByTitle(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id IS NULL ORDER BY due_at DESC")
    suspend fun getUnlistedTasksSortedByDueDate(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE list_id IS NULL ORDER BY priority DESC")
    suspend fun getUnlistedTasksSortedByPriority(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE date(due_at) = date(:today) ORDER BY title ASC")
    suspend fun getTasksDueTodaySortedByTitle(today: LocalDateTime): List<TaskEntity>

    @Query("SELECT * FROM task WHERE date(due_at) = date(:today) ORDER BY due_at DESC")
    suspend fun getTasksDueTodaySortedByDueDate(today: LocalDateTime): List<TaskEntity>

    @Query("SELECT * FROM task WHERE date(due_at) = date(:today) ORDER BY priority DESC")
    suspend fun getTasksDueTodaySortedByPriority(today: LocalDateTime): List<TaskEntity>

    // Get list name
    @Query("SELECT name FROM list WHERE id = :listId LIMIT 1")
    suspend fun getListNameById(listId: Int): String

    @Query("UPDATE task SET list_id = null where id = :id")
    suspend fun editTaskListIdToNull(id: Int)
}
