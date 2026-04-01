package group.project.fixitapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import group.project.fixitapp.data.entities.SettingEntity

@Dao
interface SettingDAO {
    @Insert
    suspend fun insertSetting(setting: SettingEntity)

    @Update
    suspend fun updateSetting(setting: SettingEntity)

    @Delete
    suspend fun deleteSetting(setting: SettingEntity)

    @Query("SELECT * FROM setting")
    suspend fun getAllSettings(): List<SettingEntity>

    @Query("SELECT * FROM setting WHERE name = :name")
    suspend fun getSettingByName(name: String): SettingEntity?

}
