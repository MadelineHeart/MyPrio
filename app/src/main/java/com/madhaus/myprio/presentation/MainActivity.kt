package com.madhaus.myprio.presentation

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.madhaus.myprio.R
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.databinding.ActivityMainBinding
import com.madhaus.myprio.presentation.settings.SettingsViewModel
import com.madhaus.myprio.presentation.taskfeed.TaskFeedViewModel
import com.madhaus.myprio.presentation.taskmanager.TaskManagerViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject lateinit var taskFeedViewModel: TaskFeedViewModel
    @Inject lateinit var taskManagerViewModel: TaskManagerViewModel
    @Inject lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BaseDaggerComponent.injector.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachNavListeners()

        val permissionRequester = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
        permissionRequester.launch(POST_NOTIFICATIONS)
    }

    private fun attachNavListeners() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        lifecycleScope.launchWhenResumed {
            taskFeedViewModel.goToManagerFlow.collectLatest { taskId ->
                val bundle = Bundle()
                taskId?.let { bundle.putSerializable("UUID", it) }
                navController.navigate(R.id.navigate_feed_to_manager, bundle)
            }
        }

        lifecycleScope.launchWhenResumed {
            taskFeedViewModel.goToSettingsFlow.collectLatest {
                navController.navigate(R.id.navigate_feed_to_settings)
            }
        }

        lifecycleScope.launchWhenResumed {
            taskManagerViewModel.saveAndExitFlow.collectLatest { didSave ->
                if (didSave) Snackbar.make(binding.root, "Saved!", Snackbar.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }

        lifecycleScope.launchWhenResumed {
            taskManagerViewModel.errorFlow.collectLatest { errorText ->
                Snackbar.make(binding.root, errorText, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}