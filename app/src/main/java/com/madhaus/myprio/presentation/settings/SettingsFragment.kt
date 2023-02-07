package com.madhaus.myprio.presentation.settings

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.madhaus.myprio.R
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.databinding.SettingsViewBinding
import timber.log.Timber
import javax.inject.Inject

class SettingsFragment: Fragment() {
    @Inject lateinit var vm: SettingsViewModel

    private val binding get() = _binding!!
    private var _binding: SettingsViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        BaseDaggerComponent.injector.inject(this)

        val binding = SettingsViewBinding.inflate(inflater, container, false)
        binding.dailyDigestMinPrio = vm.getDailyDigestMinPrio()
        binding.dailyDigestSendTime = vm.getDailyDigestSendTime()
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachListeners()
        setupToolbar()
    }

    private fun attachListeners() {
        binding.digestMinPriorityEntry.doAfterTextChanged { text ->
            text?.let {
                if (it.isNotEmpty())
                    vm.setDailyDigestMinPrio(it.toString().toInt())
            }
        }
        binding.digestSendTimeEntry.setOnClickListener { openTimeDialog() }
    }

    private fun openTimeDialog() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            vm.setDailyDigestSendTime(hourOfDay, minute)
            binding.dailyDigestSendTime = vm.getDailyDigestSendTime()
        }
        TimePickerDialog(context, listener, 0, 0, true).show()
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.settings_menu)
        binding.toolbar.title = "Settings"
        binding.toolbar.setupWithNavController(findNavController())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}