package group.project.fixitapp.utils

import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.data.entities.TaskEntity
import java.time.LocalDateTime

enum class TaskSortOrder {
    Title, DueDate, Priority
}

enum class ListSortOrder {
    Name, CreatedAt, UpdatedAt
}

fun sortTasks(tasks: List<TaskEntity>, order: TaskSortOrder): List<TaskEntity> {
    return when (order) {
        TaskSortOrder.Title -> tasks.sortedBy { it.title }
        // Descending, tasks without a value last — matches SQLite's ORDER BY ... DESC
        TaskSortOrder.DueDate ->
            tasks.sortedWith(compareByDescending(nullsFirst<LocalDateTime>()) { it.dueAt })
        TaskSortOrder.Priority ->
            tasks.sortedWith(compareByDescending(nullsFirst<Int>()) { it.priority })
    }
}

fun sortLists(lists: List<ListEntity>, order: ListSortOrder): List<ListEntity> {
    return when (order) {
        ListSortOrder.Name -> lists.sortedBy { it.name }
        ListSortOrder.CreatedAt -> lists.sortedBy { it.createdAt }
        ListSortOrder.UpdatedAt -> lists.sortedByDescending { it.updatedAt }
    }
}
