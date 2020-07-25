package com.example.taskmanager.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.database.TaskDao

class TaskViewModelFactory(
    private val parentTaskId:Long,
    private val datasource:TaskDao
) :ViewModelProvider.Factory{

    @SuppressWarnings("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(parentTaskId, datasource) as T
        }
        throw  IllegalArgumentException("Unknown Viewmodel Class")
    }
}