package com.example.taskmanager.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.databinding.FragmentTaskBinding


class TaskFragment : Fragment() {

    lateinit var binding:FragmentTaskBinding
    lateinit var taskViewModel:TaskViewModel
    lateinit var adapter:TaskAdapter
    lateinit var addTaskDialog:AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_task, container, false)

        val arguments = TaskFragmentArgs.fromBundle(requireArguments())

        //viewmodel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = TaskViewModelFactory(arguments.id, TaskDatabase.getInstance(application).taskDao)

        taskViewModel = ViewModelProvider(this, viewModelFactory).get(TaskViewModel::class.java)

        binding.viewModel = taskViewModel

        binding.setLifecycleOwner(this)

        adapter = TaskAdapter(TaskListener(
            {taskId->
                taskViewModel.onTaskItemClicked(taskId)
            },
            {task ->
                taskViewModel.ontoggleButtonClicked(task)
//                Toast.makeText(this.context,"desc: ${task.description} \nstate: ${task.progress}", Toast.LENGTH_SHORT).show()
            }))

        //recylerview
        binding.recylerView.adapter = adapter

        setHasOptionsMenu(true)

        initDialog()
        setObservers()

        return binding.root
    }

    private fun setObservers() {
        taskViewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        taskViewModel.openAddTaskDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    addTaskDialog.show()
                    taskViewModel.onAddTaskDialogCompleted()
                }
            }
        })

        taskViewModel.navigateToSelf.observe(viewLifecycleOwner, Observer {
            it?.let { taskId->
                this.findNavController().navigate(
                    TaskFragmentDirections.actionTaskFragmentSelf(taskId)
                )
                taskViewModel.onNavigationToSelfCompleted()
            }
        })

//        taskViewModel.isComposite.observe(viewLifecycleOwner, Observer {
//            if(it != null){
//                taskViewModel.updateParentComposite(it)
//            }
//        })

        taskViewModel.parentProgressAvg.observe(viewLifecycleOwner, Observer {
            it?.let {
                taskViewModel.updateParentProgress(it)
            }
        })
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(this.context)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_add_task, null)
        val taskDesriptionEditText: EditText = view.findViewById(R.id.task_description_et)

        builder.setView(view)
            .setPositiveButton("ADD") { dialog, id ->
                if(taskDesriptionEditText.text.isNotEmpty()){
                    taskViewModel.insertTask(taskDesriptionEditText.text.toString())
                    taskDesriptionEditText.setText("")
                }
            }
        addTaskDialog = builder.create()
        addTaskDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val wmlp = addTaskDialog.getWindow()
        wmlp?.setGravity(Gravity.BOTTOM)
        wmlp?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clear_all_menu -> taskViewModel.clearChildTasks()
        }
        return super.onOptionsItemSelected(item)
    }
}
