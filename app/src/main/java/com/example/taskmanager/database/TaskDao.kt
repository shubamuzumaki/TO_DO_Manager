package com.example.taskmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface TaskDao{

    @Insert
    fun insert(task:Task):Long

    @Insert
    fun insert(child:Child)

    @Update
    fun update(task:Task)

    @Update
    fun update(child: Child)

    @Delete
    fun delete(task:Task)

    @Query("UPDATE task_table SET progress=:progress WHERE taskId=:taskId")
    fun updateProgress(taskId:Long, progress:Long)

    @Query("UPDATE task_table SET composite=:isComposite WHERE taskId=:taskId")
    fun updateisComposite(taskId:Long, isComposite:Int)

    @Query("DELETE from task_table")
    fun clearAll()

    @Query("DELETE from task_table where taskId = :taskId")
    fun clearTask(taskId:Long)

    @Query("DELETE from task_table where taskId in (Select child_id from child_table where parent_id =:parentTaskId)")
    fun clearChildTasks(parentTaskId:Long)


    @Query("SELECT * from task_table")
    fun getTasks(): LiveData<List<Task>>

    @Query("SELECT * from task_table where taskId in (SELECT child_id from child_table where parent_id = :parentTaskId)")
    fun getChildTasks(parentTaskId: Long):LiveData<List<Task>>


    @Query("SELECT * from task_table WHERE taskId =:rootTaskId")
    fun getTask(rootTaskId:Long):Task?

    @Query("SELECT SUM(progress) FROM task_table where taskId in (SELECT child_id from child_table where parent_id = :parentTaskId)")
    fun getChildProgressSum(parentTaskId:Long):Long

    @Query("SELECT COUNT(*) FROM task_table where taskId in (SELECT child_id from child_table where parent_id = :parentTaskId)")
    fun getChildCount(parentTaskId: Long):Long


    @Query("SELECT parent_id FROM child_table where child_id= :childId")
    fun getParentTaskId(childId: Long):Long

    @RawQuery
    fun insertRecords(query: SupportSQLiteQuery): Boolean
}