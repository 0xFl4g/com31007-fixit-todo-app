package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import group.project.fixitapp.data.entities.TemplateEntity

@Dao
interface TemplateDAO {
    @Insert
    suspend fun insertTemplate(template: TemplateEntity)

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("SELECT * FROM template_task")
    suspend fun getAllTemplates(): List<TemplateEntity>

    @Query("SELECT * FROM template_task WHERE id = :id")
    suspend fun getTemplateById(id: Int): TemplateEntity
}
