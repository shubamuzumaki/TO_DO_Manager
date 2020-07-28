package com.example.taskmanager.database.util

interface TaskTouchHelperAdapter {
    fun onItemMove(fromPosition:Int, toPosition:Int)
    fun onItemSwiped(position:Int)
}