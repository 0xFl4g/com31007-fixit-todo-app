package group.project.fixitapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.ListEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

open class TaskViewModel(app: Application) : AndroidViewModel(app) {
    protected val taskDao = AppDatabase.getDatabase(app).taskDAO()
    private val listDao = AppDatabase.getDatabase(app).listDAO()

    val lists: StateFlow<List<ListEntity>> by lazy {
        MutableStateFlow(emptyList())
    }

    init {
        getAllLists()
    }

    protected fun saveBitmapToFile(context: Context, bitmap: ImageBitmap, taskId: Int): String {
        val filename = "task_image_$taskId.png"
        val file = File(context.filesDir, filename)
        val stream: OutputStream = FileOutputStream(file)

        val androidBitmap = bitmap.asAndroidBitmap()
        androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        return file.absolutePath
    }

    fun getBitmapFromFile(filename: String): ImageBitmap? {
        return BitmapFactory.decodeFile(filename)?.asImageBitmap()
    }

    private fun getAllLists() {
        viewModelScope.launch {
            listDao.getAllLists().let {
                (lists as MutableStateFlow).value = it
            }
        }
    }
}