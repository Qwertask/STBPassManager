package com.example.test

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.test.databinding.TaskItemCellBinding


class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemListener
): RecyclerView.ViewHolder(binding.root) {
    fun bindTaskItem(taskItem: TaskItem)
    {
        binding.name.text = taskItem.name
        binding.login.text = taskItem.login
        binding.password.text = taskItem.password


        binding.deleteButton.setOnClickListener {
            clickListener.deleteTaskItem(taskItem)
        }

        binding.taskCellContainer.setOnClickListener{
            clickListener.editTaskItem(taskItem)
        }
    }
}