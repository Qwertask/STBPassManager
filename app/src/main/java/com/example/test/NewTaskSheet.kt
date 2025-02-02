package com.example.test

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.test.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.log

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if(taskItem!=null){
            binding.taskTitle.text = "Изменить"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.login)
            binding.pass.text = editable.newEditable(taskItem!!.password)
        }else{
            binding.taskTitle.text = "Добавить"

        }
        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener{
            savAction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }


    private fun savAction() {
       val name = binding.name.text.toString()
       val desc = binding.desc.text.toString()
        val pass = binding.pass.text.toString()
        if(taskItem==null){
            val newTask = TaskItem(name,desc,pass)
            taskViewModel.addTaskItem(newTask)
        }else{
            taskItem!!.name = name
            taskItem!!.login = desc
            taskItem!!.password = pass
            taskViewModel.updateTaskItem(taskItem!!)
        }
        binding.name.setText("")
        binding.desc.setText("")
        binding.pass.setText("")
        dismiss()
    }
}