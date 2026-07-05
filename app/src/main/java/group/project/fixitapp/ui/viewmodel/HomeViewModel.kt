package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.utils.ListSortOrder
import group.project.fixitapp.utils.cleanupTaskArtifacts
import group.project.fixitapp.utils.sortLists
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val listDao = AppDatabase.getDatabase(app).listDAO()
    private val taskDao = AppDatabase.getDatabase(app).taskDAO()

    private val sortOrder = MutableStateFlow(ListSortOrder.Name)

    val userLists: StateFlow<List<ListEntity>> =
        combine(listDao.observeAllLists(), sortOrder, ::sortLists)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun changeSortOrder(newOrder: ListSortOrder) {
        sortOrder.value = newOrder
    }

    fun addNewList(listName: String) {
        viewModelScope.launch {
            if (listName.isEmpty()) return@launch
            val newList = ListEntity(
                name = listName,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            listDao.insertList(newList)
        }
    }

    fun editListName(id: Int, newName: String) {
        viewModelScope.launch {
            if (newName.isEmpty()) return@launch
            listDao.editListName(id, newName, LocalDateTime.now())
        }
    }

    // Fun to delete list without saving tasks
    fun deleteListWithoutUnlisted(id: Int) {
        viewModelScope.launch {
            val tasksInList = taskDao.getTasksByListId(id)

            for (task in tasksInList) {
                cleanupTaskArtifacts(getApplication(), task)
                taskDao.deleteTask(task)
            }

            listDao.deleteListById(id)
        }
    }

    fun deleteListMoveUnlisted(id: Int) {
        viewModelScope.launch {
            val tasksInList = taskDao.getTasksByListId(id)

            for (task in tasksInList) {
                task.id?.let { taskDao.editTaskListIdToNull(it) }
            }

            listDao.deleteListById(id)
        }
    }
}
