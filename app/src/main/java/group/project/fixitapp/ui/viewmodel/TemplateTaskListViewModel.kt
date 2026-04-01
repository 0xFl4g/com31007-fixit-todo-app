package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TemplateTaskListViewModel(app: Application) : AndroidViewModel(app) {
    private val templateDAO = AppDatabase.getDatabase(app).templateDAO()

    private val _templateTasks = MutableStateFlow<List<TemplateEntity>>(emptyList())
    val templateTasks: StateFlow<List<TemplateEntity>> = _templateTasks

    init {
        loadTemplateTasks()
    }

    private fun loadTemplateTasks() {
        viewModelScope.launch {
            _templateTasks.value = templateDAO.getAllTemplates()
        }
    }

}