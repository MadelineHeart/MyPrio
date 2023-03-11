package com.madhaus.myprio.presentation.taskmanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
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
        binding.presoTask = PresoTask(vm.getTask(arguments?.getSerializable(MANAGER_TASK_ID_TAG) as? UUID), requireContext(), true)
        binding.vm = vm
        _binding = binding

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachListeners()
        setupToolbar()
        setupExampleCell()
    }

    private fun attachListeners() {
        binding.toolbar.setOnMenuItemClickListener { handleMenuItem(it) }

        binding.description.doOnTextChanged { _, _, _, count ->
            binding.exampleTaskCell.expanded.visibility = if (count > 0) View.VISIBLE else View.GONE }

        binding.saveButton.setOnClickListener { _ -> binding.presoTask?.let { vm.saveAndExit(it) } }
        binding.cancelButton.setOnClickListener { _ -> vm.cancelAndExit() }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.task_manager_menu)
        binding.toolbar.title = "Task Manager"
        binding.toolbar.setupWithNavController(findNavController())
    }

    private fun setupExampleCell() {
        binding.exampleTaskCell.editButton.visibility = View.GONE
        binding.exampleTaskCell.doneButton.visibility = View.GONE
    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                showDeleteDialog()
                true
            } else ->
                false
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Are you sure you want to delete this task?")
            .setPositiveButton("DELETE") { dialog, _ ->
                binding.presoTask?.let { vm.deleteTask(it) }
                dialog.dismiss()
            }
            .setNegativeButton("DISMISS") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MANAGER_TASK_ID_TAG = "My_Prio_Manager_Task_Id_tag"
    }
}
