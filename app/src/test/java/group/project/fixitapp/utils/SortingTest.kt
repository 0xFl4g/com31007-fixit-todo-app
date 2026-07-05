package group.project.fixitapp.utils

import group.project.fixitapp.data.entities.ListEntity
import group.project.fixitapp.data.entities.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SortingTest {

    private fun task(
        id: Int,
        title: String,
        dueAt: LocalDateTime? = null,
        priority: Int? = null
    ) = TaskEntity(
        id = id,
        title = title,
        dueAt = dueAt,
        completedAt = null,
        reminderAt = null,
        note = null,
        priority = priority,
        latitude = null,
        longitude = null,
        image = null,
        listId = null,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        updatedAt = LocalDateTime.of(2026, 1, 1, 0, 0)
    )

    private fun list(id: Int, name: String, createdAt: LocalDateTime, updatedAt: LocalDateTime) =
        ListEntity(id = id, name = name, createdAt = createdAt, updatedAt = updatedAt)

    @Test
    fun `tasks sort by title ascending`() {
        val tasks = listOf(task(1, "banana"), task(2, "apple"), task(3, "cherry"))
        val sorted = sortTasks(tasks, TaskSortOrder.Title)
        assertEquals(listOf(2, 1, 3), sorted.map { it.id })
    }

    @Test
    fun `tasks sort by due date descending with nulls last`() {
        val tasks = listOf(
            task(1, "no due date"),
            task(2, "earlier", dueAt = LocalDateTime.of(2026, 7, 1, 9, 0)),
            task(3, "later", dueAt = LocalDateTime.of(2026, 7, 10, 9, 0))
        )
        val sorted = sortTasks(tasks, TaskSortOrder.DueDate)
        assertEquals(listOf(3, 2, 1), sorted.map { it.id })
    }

    @Test
    fun `tasks sort by priority descending with nulls last`() {
        val tasks = listOf(
            task(1, "none"),
            task(2, "low", priority = 1),
            task(3, "high", priority = 5)
        )
        val sorted = sortTasks(tasks, TaskSortOrder.Priority)
        assertEquals(listOf(3, 2, 1), sorted.map { it.id })
    }

    @Test
    fun `lists sort by name ascending`() {
        val t = LocalDateTime.of(2026, 1, 1, 0, 0)
        val lists = listOf(list(1, "Work", t, t), list(2, "Home", t, t))
        assertEquals(listOf(2, 1), sortLists(lists, ListSortOrder.Name).map { it.id })
    }

    @Test
    fun `lists sort by creation date ascending`() {
        val early = LocalDateTime.of(2026, 1, 1, 0, 0)
        val late = LocalDateTime.of(2026, 6, 1, 0, 0)
        val lists = listOf(list(1, "newer", late, late), list(2, "older", early, early))
        assertEquals(listOf(2, 1), sortLists(lists, ListSortOrder.CreatedAt).map { it.id })
    }

    @Test
    fun `lists sort by update date descending`() {
        val early = LocalDateTime.of(2026, 1, 1, 0, 0)
        val late = LocalDateTime.of(2026, 6, 1, 0, 0)
        val lists = listOf(list(1, "stale", early, early), list(2, "fresh", late, late))
        assertEquals(listOf(2, 1), sortLists(lists, ListSortOrder.UpdatedAt).map { it.id })
    }
}
