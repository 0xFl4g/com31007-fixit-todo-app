package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import group.project.fixitapp.data.entities.ListEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ListDAO {
    @Insert
    suspend fun insertList(list: ListEntity)

    @Query("SELECT * FROM list")
    fun observeAllLists(): Flow<List<ListEntity>>

    @Query("UPDATE list SET name = :name, updated_at = :currentTime WHERE id = :id")
    suspend fun editListName(
        id: Int,
        name: String,
        currentTime: LocalDateTime? = LocalDateTime.now()
    )

    @Query("DELETE FROM list")
    suspend fun deleteThemAll()

    @Query("DELETE FROM list WHERE id = :id")
    suspend fun deleteListById(id: Int)
}
