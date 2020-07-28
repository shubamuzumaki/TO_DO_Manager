package com.example.taskmanager.task

import android.util.Log
import androidx.annotation.WorkerThread
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
        val MAXIMUM_DEPTH_ALLOWED = 5
    }


    private val repository = TaskRepository(parentTaskId, taskDao)
    val tasks:LiveData<List<Task>> = repository.allTasks
    private  var parentDepth = 0

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var progressUpdaterJob:Job? = null

    private val _openAddTaskDialog = MutableLiveData<Boolean>()
    val openAddTaskDialog :LiveData<Boolean>
        get() = _openAddTaskDialog

    private val _navigateToSelf = MutableLiveData<Long>()
    val navigateToSelf :LiveData<Long>
        get() = _navigateToSelf

    init {
        uiScope.launch(Dispatchers.IO) {
            val parent = repository.getTask(parentTaskId)
            parentDepth = parent?.depth ?: 0
        }
    }

    fun updateParentComposite(isComposite:Boolean){
        uiScope.launch {
            repository.updateisComposite(isComposite)
        }
    }

    fun onFabClicked(){
        _openAddTaskDialog.value = true
    }
    fun onAddTaskDialogCompleted(){
        _openAddTaskDialog.value = false
    }

    fun onTaskItemClicked(task: Task){
        if(task.depth <= MAXIMUM_DEPTH_ALLOWED){
            _navigateToSelf.value = task.taskId
        }
    }

    fun onNavigationToSelfCompleted(){
        _navigateToSelf.value = null
    }

    fun clearChildTasks() = uiScope.launch{
        repository.clearTasks()
        updateParentComposite(false)
        onProgressChanged()
//        repository.clearAll() //debug
    }

    fun insertTask(description:String) = uiScope.launch {
        repository.insert(Task(description = description, depth = parentDepth+1))
        updateParentComposite(true)
        onProgressChanged()
    }

    fun ontoggleButtonClicked(task: Task){
        val newProgress = if(task.progress == 0L) 100L else 0L
        uiScope.launch {
            repository.updateProgress(task.taskId, newProgress)
        }
        onProgressChanged()
    }

    fun onProgressChanged(){
        progressUpdaterJob?.cancel()
        progressUpdaterJob = uiScope.launch {
            repository.updateProgress(ROOT_TASK_ID, -parentTaskId)

            repository.calculateAndUpdateProgress(parentTaskId)

            repository.updateProgress(ROOT_TASK_ID, 0L)
        }

    }

    fun removeTask(task:Task?){
        task?.let {
            uiScope.launch {
                repository.removeTask(task)
                onProgressChanged()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Log.i(TAG,"cancelled")
    }
}