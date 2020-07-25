package com.example.taskmanager.task

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.taskmanager.ROOT_TASK_ID
import com.example.taskmanager.database.Child
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskRepository (private val parentTaskId:Long, private val taskDao: TaskDao){
    val allTasks: LiveData<List<Task>> = taskDao.getChildTasks(parentTaskId)
    val totalProgress:LiveData<Int> = taskDao.getTotalProgress(parentTaskId)

//    val allTasks: LiveData<List<Task>> = taskDao.getTasks()

    companion object{
        val TAG = "@TaskRepository"
    }

    private suspend fun insertTask(task:Task):Long{
        return withContext(Dispatchers.IO){
            taskDao.insert(task)
        }
    }

    private suspend fun insertChild(child: Child){
        withContext(Dispatchers.IO){
            taskDao.insert(child)
        }
    }

    @WorkerThread
    suspend fun update(task: Task){
        withContext(Dispatchers.IO){
            taskDao.update(task)
        }
    }

    @WorkerThread
    suspend fun updateProgress(taskId:Long, newProgress:Int){
        withContext(Dispatchers.IO){
            taskDao.updateProgress(taskId,newProgress)
        }
    }

    @WorkerThread
    suspend fun updateisComposite(isComposite:Boolean){
        withContext(Dispatchers.IO){
            taskDao.updateisComposite(parentTaskId, if(isComposite) 1 else 0)
        }
    }

    @WorkerThread
    suspend fun insert(task: Task){
        withContext(Dispatchers.IO){
            val id = insertTask(task)
            insertChild(Child(id, parentTaskId))
        }
    }
    @WorkerThread
    suspend fun clearTasks(){
        withContext(Dispatchers.IO){
            taskDao.clearChildTasks(parentTaskId)
        }
    }

    @WorkerThread
    suspend fun clearAll(){
        withContext(Dispatchers.IO){
            if(parentTaskId == ROOT_TASK_ID){
                taskDao.clearAll()
            }
            else{
                Log.i("@TaskRepository", "databse cannot be cleared beacause fn is not called from prent task id doesnt match")
            }
        }
    }

}