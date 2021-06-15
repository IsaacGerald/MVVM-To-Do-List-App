package com.example.todo_listapp.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_listapp.data.Task
import com.example.todo_listapp.data.TaskDao
import com.example.todo_listapp.ui.ADD_TASK_RESULT_OK
import com.example.todo_listapp.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel
@Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {


    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val _addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = _addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        }else{
            val newTask = Task(name = taskName, important = taskImportance)
            createNewTask(newTask)
        }

    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        _addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))
    }


    private fun updateTask(updateTask: Task) = viewModelScope.launch {
        taskDao.update(updateTask)
        _addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }


    private fun createNewTask(newTask: Task) = viewModelScope.launch {
        taskDao.insertTask(newTask)
        _addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }


    sealed class AddEditTaskEvent{
        data class ShowInvalidInputMessage(val msg: String): AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int): AddEditTaskEvent()
    }


}