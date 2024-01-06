package com.vortex.android.vortexchat.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.vortex.android.vortexchat.R
import com.vortex.android.vortexchat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_chats, R.id.navigation_global_chat, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        val bottomNavigationView = binding.navView
        bottomNavigationView.setupWithNavController(navController)
    }

    fun showBottomNavigation()
    {
        binding.navView.visibility = View.VISIBLE
        //(activity as MainActivity).hideBottomNavigation() внутри фрагмента
    }

    fun hideBottomNavigation()
    {
        binding.navView.visibility = View.GONE
    }

    //Без этого кнопка назад не работает
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}