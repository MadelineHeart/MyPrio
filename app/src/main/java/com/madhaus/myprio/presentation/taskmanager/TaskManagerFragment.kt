package com.madhaus.myprio.presentation.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.madhaus.myprio.R
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.databinding.TaskManagerViewBinding
import com.madhaus.myprio.presentation.models.PresoTask
import java.util.*
import javax.inject.Inject

class TaskManagerFragment : Fragment() {

    @Inject lateinit var vm: TaskManagerViewModel

//     This property is only valid between onCreateView and
//     onDestroyView.
    private val binding get() = _binding!!
    private var _binding: TaskManagerViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        BaseDaggerComponent.injector.inject(this)

        val binding = TaskManagerViewBinding.inflate(inflater, container, false)
        binding.presoTask = PresoTask(requireContext(), vm.getTask(arguments?.getSerializable("UUID") as? UUID))
        binding.vm = vm
        _binding = binding

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachListeners()
        setupToolbar()
    }

    private fun attachListeners() {
        binding.toolbar.setOnMenuItemClickListener { handleMenuItem(it) }

        binding.saveButton.setOnClickListener { _ -> binding.presoTask?.let { vm.saveAndExit(it) } }
        binding.cancelButton.setOnClickListener { _ -> vm.cancelAndExit() }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.task_manager_menu)
        binding.toolbar.title = "Task Manager"
        binding.toolbar.setupWithNavController(findNavController())
    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                binding.presoTask?.id?.let { vm.deleteTask(it) }
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
