package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.entities.TaskEntity
import group.project.fixitapp.ui.pages.TaskLoadCriteria
import group.project.fixitapp.utils.TaskSortOrder
import group.project.fixitapp.utils.sortTasks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModel(app: Application) : TaskViewModel(app) {

    private val criteria = MutableStateFlow<TaskLoadCriteria?>(null)
    private val sortOrder = MutableStateFlow(TaskSortOrder.Priority) // Default to sorting by priority

    val tasks: StateFlow<List<TaskEntity>> = criteria
        .filterNotNull()
        .flatMapLatest { current ->
            when (current) {
                is TaskLoadCriteria.MyDay ->
                    taskDao.observeTasksDueToday(LocalDate.now().atStartOfDay())
                is TaskLoadCriteria.Unlisted -> taskDao.observeUnlistedTasks()
                is TaskLoadCriteria.ListId -> taskDao.observeTasksByListId(current.id)
            }
        }
        .combine(sortOrder, ::sortTasks)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _listName = MutableStateFlow("Default")
    val listName: StateFlow<String> get() = _listName.asStateFlow()

    fun setCriteria(newCriteria: TaskLoadCriteria) {
        criteria.value = newCriteria
        if (newCriteria is TaskLoadCriteria.ListId) {
            loadListName(newCriteria.id)
        }
    }

    fun changeSortOrder(newOrder: TaskSortOrder) {
        sortOrder.value = newOrder
    }

    fun completeTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskCompleted(id, LocalDateTime.now())
            cancelReminder(id)
        }
    }

    fun reopenTask(id: Int) {
        viewModelScope.launch {
            taskDao.updateTaskReopen(id, LocalDateTime.now())
            taskDao.getTaskById(id)?.let { scheduleReminderIfEnabled(it) }
        }
    }

    private fun loadListName(id: Int) {
        viewModelScope.launch {
            _listName.value = taskDao.getListNameById(id)
        }
    }
}
