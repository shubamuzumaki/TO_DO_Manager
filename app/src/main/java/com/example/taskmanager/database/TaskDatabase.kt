package com.example.taskmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class,Child::class],
    version = 5,
    exportSchema = false)
abstract class TaskDatabase:RoomDatabase() {

    abstract val taskDao:TaskDao

    companion object{
        @Volatile
        private var INSTANCE:TaskDatabase? = null

        fun getInstance(context: Context):TaskDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }

        }
    }


}
//
//.addCallback(object : RoomDatabase.Callback(){
//    override fun onCreate(db: SupportSQLiteDatabase) {
//        super.onCreate(db)
//        db.execSQL(
//            """
//                            pragma recursive_triggers=1
//                            """.trimIndent())
//
//        db.execSQL(
//            """
//                            create trigger parent_progress after insert on task_table for every row
//                            BEGIN
//                                if(new.progress != old.progress) then
//                                    declare parentId int;
//                                    declare rowCount int;
//                                    declare sumProgress int;
//                                    declare avgProgress int;
//
//                                    set parentId = select parent_id from child_table where child_id = new.taskId;
//
//                                    if(parentId != -786) then
//                                        set rowCount = SELECT COUNT(*) FROM task_table where taskId in (SELECT child_id from child_table where parent_id = parentId);
//                                        set sumProgress = SELECT SUM(progress) FROM task_table where taskId in (SELECT child_id from child_table where parent_id = parentId);
//                                        set avgProgress = sumProgress/rowCount;
//
//                                        UPDATE task_table SET progress = avgProgress where  taskId = parentId;
//                                    end if;
//
//                                end if;
//                            END;
//                            """.trimIndent()
//        )
//    }
//})