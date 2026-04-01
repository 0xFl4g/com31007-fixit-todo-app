package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import group.project.fixitapp.Destinations
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.ListEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val listDao = AppDatabase.getDatabase(app).listDAO()
    private val taskDao = AppDatabase.getDatabase(app).taskDAO()

    private val _userLists = MutableStateFlow<List<ListEntity>>(emptyList())
    val userLists: StateFlow<List<ListEntity>> = _userLists

    init {
        loadUserLists()
    }

    private fun loadUserLists() {
        viewModelScope.launch {
            listDao.getAllLists().let {
                _userLists.value = it
            }
        }
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
            id.let { listDao.editListName(it, newName, LocalDateTime.now()) }
        }
    }

    private fun loadLists() {
        viewModelScope.launch {
            _userLists.value = when (sortOrder.value) {
                ListSortOrder.Name -> listDao.getListName()
                ListSortOrder.CreatedAt -> listDao.getListsSortedByCreate()
                ListSortOrder.UpdatedAt -> listDao.getListsSortedByUpdate()
            }
        }
    }

    enum class ListSortOrder {
        Name, CreatedAt, UpdatedAt,  // Add more if needed
    }

    private var sortOrder = MutableStateFlow(ListSortOrder.Name) // Default to sorting by name

    fun changeSortOrder(newOrder: ListSortOrder) {
        sortOrder.value = newOrder
        loadLists() // This should reload the lists with the new order
    }

    // Fun to delete list without saving tasks
    fun deleteListWithoutUnlisted(id: Int) {
        viewModelScope.launch {
            val tasksInList = taskDao.getTasksByListId(id)

            for (i in tasksInList) {
                taskDao.deleteTask(i)
            }

            listDao.deleteListById(id)

        }
    }

    fun deleteListMoveUnlisted(id: Int, navController: NavController) {
        viewModelScope.launch {
            val tasksInList = taskDao.getTasksByListId(id)

            for (i in tasksInList) {
                i.id?.let { taskDao.editTaskListIdToNull(it) }
            }

            listDao.deleteListById(id)

            navController.navigate(Destinations.HOME_ROUTE)
        }
    }

}
