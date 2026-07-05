package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDAO {
    @Insert
    suspend fun insertTemplate(template: TemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("SELECT * FROM template_task")
    fun observeAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM template_task WHERE id = :id")
    fun observeTemplateById(id: Int): Flow<TemplateEntity?>
}
