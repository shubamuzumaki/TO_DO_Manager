package com.example.taskmanager.task

import android.util.Log
import androidx.lifecycle.*
import com.example.taskmanager.ROOT_TASK_ID
import com.example.taskmanager.database.Child
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDao
import kotlinx.coroutines.*
import kotlin.random.Random

class TaskViewModel(
    private val parentTaskId:Long,
    private val taskDao: TaskDao
) : ViewModel() {

    companion object{
        val TAG = "@TaskViewModel"
    }

    val repository = TaskRepository(parentTaskId, taskDao)
    val tasks:LiveData<List<Task>> = repository.allTasks

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _openAddTaskDialog = MutableLiveData<Boolean>()
    val openAddTaskDialog :LiveData<Boolean>
        get() = _openAddTaskDialog

    private val _navigateToSelf = MutableLiveData<Long>()
    val navigateToSelf :LiveData<Long>
        get() = _navigateToSelf

//    val isComposite:LiveData<Boolean> = Transformations.distinctUntilChanged(
//        Transformations.map(tasks){ list ->
//        !(list?.isEmpty() ?: true)
//    })

    val parentProgressAvg:LiveData<Int> = Transformations.distinctUntilChanged(
        Transformations.map(repository.totalProgress){
            var totalCount = tasks.value?.size ?: 1
            totalCount = if(totalCount == 0) 1 else totalCount
            if(it != null){
                it/totalCount
            }
            else
                totalCount
        }
    )

    fun updateParentProgress(newProgress:Int){
        viewModelScope.launch {
            repository.updateProgress(parentTaskId, newProgress)
        }
    }

    fun updateParentComposite(isComposite:Boolean){
        viewModelScope.launch {
            repository.updateisComposite(isComposite)
        }
    }

    fun onFabClicked(){
        _openAddTaskDialog.value = true
    }
    fun onAddTaskDialogCompleted(){
        _openAddTaskDialog.value = false
    }

    fun onTaskItemClicked(taskId: Long){
        _navigateToSelf.value = taskId
    }
    fun onNavigationToSelfCompleted(){
        _navigateToSelf.value = null
    }

    fun clearChildTasks() = viewModelScope.launch{
        repository.clearTasks()
        updateParentComposite(false)
//        repository.clearAll() //debug
    }

    fun insertTask(description:String) = viewModelScope.launch {
        repository.insert(Task(description = description))
        updateParentComposite(true)
    }

    fun ontoggleButtonClicked(task: Task){
        val newProgress = if(task.progress == 0) 100 else 0
        viewModelScope.launch {
            repository.updateProgress(task.taskId, newProgress)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}