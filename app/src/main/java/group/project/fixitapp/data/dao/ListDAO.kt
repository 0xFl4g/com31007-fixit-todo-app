package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import group.project.fixitapp.data.entities.ListEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ListDAO {
    @Insert
    suspend fun insertList(list: ListEntity)

    @Update
    suspend fun updateList(list: ListEntity)

    @Delete
    suspend fun deleteList(list: ListEntity)

    @Query("SELECT * FROM list")
    suspend fun getAllLists(): List<ListEntity>

    @Query("SELECT * FROM list WHERE id = :id")
    fun getListById(id: Int): Flow<ListEntity>

    @Query("SELECT COUNT(*) FROM list")
    suspend fun numOfLists(): Int

    @Query("UPDATE list SET name = :name, updated_at = :currentTime WHERE id = :id")
    suspend fun editListName(
        id: Int,
        name: String,
        currentTime: LocalDateTime? = LocalDateTime.now()
    )

    @Query("SELECT * FROM list ORDER BY name ASC")
    suspend fun getListName(): List<ListEntity>

    @Query("SELECT * FROM list ORDER BY created_at ASC")
    suspend fun getListsSortedByCreate(): List<ListEntity>

    @Query("SELECT * FROM list ORDER BY updated_at DESC")
    suspend fun getListsSortedByUpdate(): List<ListEntity>


    @Query("DELETE FROM list")
    suspend fun deleteThemAll()

    @Query("DELETE FROM list WHERE id = :id")
    suspend fun deleteListById(id: Int)

}

