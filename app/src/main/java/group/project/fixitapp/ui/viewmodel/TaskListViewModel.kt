package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class TaskListViewModel(app: Application) : AndroidViewModel(app) {
    private val taskDao = AppDatabase.getDatabase(app).taskDAO()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks

    private val _listName = MutableStateFlow("Default")
    val listName: StateFlow<String> get() = _listName.asStateFlow()

    fun loadMyDayTasks() {
        viewModelScope.launch {
            val today = LocalDate.now()
                .atStartOfDay() // Assuming you have converters for LocalDate/LocalDateTime
            _tasks.value = when (sortOrder.value) {
                TaskSortOrder.Title -> taskDao.getTasksDueTodaySortedByTitle(today)
                TaskSortOrder.DueDate -> taskDao.getTasksDueTodaySortedByDueDate(today)
                TaskSortOrder.Priority -> taskDao.getTasksDueTodaySortedByPriority(today)
            }
        }
    }

    fun loadUnlistedTasks() {
        viewModelScope.launch {
            _tasks.value = when (sortOrder.value) {
                TaskSortOrder.Title -> taskDao.getUnlistedTasksSortedByTitle()
                TaskSortOrder.DueDate -> taskDao.getUnlistedTasksSortedByDueDate()
                TaskSortOrder.Priority -> taskDao.getUnlistedTasksSortedByPriority()
            }
        }
    }

    fun loadTasksByListId(listId: Int) {
        viewModelScope.launch {
            _tasks.value = when (sortOrder.value) {
                TaskSortOrder.Title -> taskDao.getTasksByListIdSortedByTitle(listId)
                TaskSortOrder.DueDate -> taskDao.getTasksByListIdSortedByDueDate(listId)
                TaskSortOrder.Priority -> taskDao.getTasksByListIdSortedByPriority(listId)
            }
        }
    }

    enum class TaskSortOrder {
        Title, DueDate, Priority // Add more if needed
    }

    private val _sortOrder =
        MutableStateFlow(TaskSortOrder.Priority) // Default to sorting by priority
    val sortOrder: StateFlow<TaskSortOrder> get() = _sortOrder.asStateFlow()

    fun changeSortOrder(newOrder: TaskSortOrder, listId: Int) {
        _sortOrder.value = newOrder
        loadTasksByListId(listId) // This should reload the tasks with the new order
    }

    fun changeSortOrderForMyDay(newOrder: TaskSortOrder) {
        _sortOrder.value = newOrder
        loadMyDayTasks()
    }

    fun changeSortOrderForUnlisted(newOrder: TaskSortOrder) {
        _sortOrder.value = newOrder
        loadUnlistedTasks()
    }

    fun completeTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskCompleted(id, LocalDateTime.now())
        }
    }

    fun reopenTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskReopen(id, LocalDateTime.now())
        }
    }

    fun loadListName(id: Int) {
        viewModelScope.launch {
            val name = taskDao.getListNameById(id)
            _listName.value = name
        }
    }

}
