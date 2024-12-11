package com.example.test

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class TaskViewModel(private val repository: TaskItemRepository):ViewModel()
{
    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()

    fun deleteTaskItem(newTask: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(newTask)
    }

    fun addTaskItem(newTask: TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(newTask)
    }

    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch{
        repository.updateTaskItem(taskItem)
    }

}

class TaskItemModelFactory(private val repository: TaskItemRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}