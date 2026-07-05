package group.project.fixitapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import group.project.fixitapp.data.AppDatabase
import group.project.fixitapp.data.entities.TemplateEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TemplateTaskListViewModel(app: Application) : AndroidViewModel(app) {
    private val templateDAO = AppDatabase.getDatabase(app).templateDAO()

    val templateTasks: StateFlow<List<TemplateEntity>> = templateDAO.observeAllTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
