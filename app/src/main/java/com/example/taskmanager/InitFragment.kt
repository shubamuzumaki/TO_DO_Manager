package com.example.taskmanager


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.database.TaskDao
import com.example.taskmanager.databinding.FragmentInitBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
val ROOT_TASK_ID:Long = -786
val ROOT_TASK_NAME:String = "_SUPREME_TASK_"

class InitFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentInitBinding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_init, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val datasource = TaskDatabase.getInstance(application).taskDao

        CoroutineScope(Dispatchers.Main).launch {
            tryInsertRootTask(datasource)

            this@InitFragment.findNavController().navigate(
                InitFragmentDirections.actionInitFragmentToTaskFragment(ROOT_TASK_ID)
            )
        }
    }

    suspend fun tryInsertRootTask(datasource:TaskDao) {
        withContext(Dispatchers.IO){
            val id = datasource.insertRecords(SimpleSQLiteQuery("INSERT OR IGNORE INTO task_table VALUES(${ROOT_TASK_ID}, \"${ROOT_TASK_NAME}\", 1, 0);"))
//            Log.i("SHARMA", "root task inserted successfully: ${id}")
        }
    }

}
