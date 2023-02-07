package com.madhaus.myprio.presentation.taskfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.madhaus.myprio.R
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.data.Task
import com.madhaus.myprio.databinding.TaskItemBinding
import com.madhaus.myprio.presentation.models.PresoTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskFeedAdapter(private val context: Context) :
    RecyclerView.Adapter<TaskFeedAdapter.Holder>() {

    @Inject
    lateinit var vm: TaskFeedViewModel
    private var currentTastList: List<Task> = listOf()

    init {
        BaseDaggerComponent.injector.inject(this)

        currentTastList = vm.getTaskList()
        CoroutineScope(Dispatchers.Main).launch {
            vm.getTaskListFlow().collectLatest { onListUpdate(it) }
        }
    }

    inner class Holder(val itemRowBinding: TaskItemBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {

        fun bind(newTask: Task) {
            itemRowBinding.presoTask = PresoTask(context, newTask)
            itemRowBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding: TaskItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.task_item, parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val task = currentTastList[position]
        holder.bind(task)

        holder.itemView.setOnClickListener { toggleExpandedView(holder) }
        holder.itemRowBinding.editButton.setOnClickListener { vm.goToEditTask(task.id) }
        holder.itemRowBinding.doneButton.setOnClickListener {
            vm.markTaskDone(task.id)
            toggleExpandedView(holder, View.GONE)
        }
    }

    override fun getItemCount() = currentTastList.size

    private fun onListUpdate(newList: List<Task>) {
        currentTastList = newList
        notifyDataSetChanged()
    }

    private fun toggleExpandedView(holder: Holder, visibilityOverride: Int? = null) {
        holder.itemRowBinding.description.visibility =
            if (holder.itemRowBinding.presoTask?.description?.isEmpty() != false) View.GONE else View.VISIBLE
        val currentVis = holder.itemRowBinding.expanded.visibility
        holder.itemRowBinding.expanded.visibility =
            visibilityOverride ?: if (currentVis == View.VISIBLE) View.GONE else View.VISIBLE
    }
}
