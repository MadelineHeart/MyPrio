package com.madhaus.myprio.presentation.taskfeed

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.madhaus.myprio.R
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.databinding.TaskFeedViewBinding
import javax.inject.Inject

class TaskFeedFragment: Fragment() {

    @Inject lateinit var vm: TaskFeedViewModel

    private val binding get() = _binding!!
    private var _binding: TaskFeedViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        BaseDaggerComponent.injector.inject(this)

        val binding = TaskFeedViewBinding.inflate(inflater, container, false)
        binding.tasks = vm.getTaskList()
        binding.adapter = TaskFeedAdapter(requireContext())
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachListeners()
        setupToolbar()
    }

    private fun attachListeners() {
        binding.addButton.setOnClickListener { _ ->
            vm.goToNewTask()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            handleMenuItem(item)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.task_feed_menu)
        binding.toolbar.title = "Task Feed"
        binding.toolbar.setupWithNavController(findNavController())
    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                vm.goToSettings()
                true
            } else ->
                false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}