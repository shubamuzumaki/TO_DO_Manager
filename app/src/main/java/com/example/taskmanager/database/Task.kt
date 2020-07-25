package com.example.taskmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId:Long = 0L,

    @ColumnInfo(name="description")
    val description:String,

    @ColumnInfo(name = "composite")
    val isComposite: Boolean = false,

    @ColumnInfo(name= "progress")
    val progress:Int = 0
)

@Entity(tableName = "child_table",
    foreignKeys =[
       ForeignKey(entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["parent_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE)
])
data class Child(
    @PrimaryKey(autoGenerate = false)
    val child_id:Long,

    @ColumnInfo(name="parent_id")
    val parent_id:Long
)