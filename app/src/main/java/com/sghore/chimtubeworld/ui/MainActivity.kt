package com.sghore.chimtubeworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 인스턴스 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Bottom Nav 설정
        val navFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFrag.navController

        binding.bottomNavView.setupWithNavController(navController)
    }
}