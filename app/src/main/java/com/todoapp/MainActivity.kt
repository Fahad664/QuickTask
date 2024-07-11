package com.todoapp

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.todoapp.network_api.ApiResponse
import com.todoapp.adapters.FragmentPagerAdapter
import com.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var apiResponse : ApiResponse

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.viewPager2.adapter = FragmentPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout,binding.viewPager2){ tab, position->
            when(position){
                0 -> tab.text = "Active"
                1 -> tab.text = "Cancel"
                2 -> tab.text = "Completed"
                3 -> tab.text = "All Events"
            }
        }.attach()

        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                // Check if you are at the first or last page
                if (position == binding.viewPager2.adapter?.itemCount?.minus(1)) {
//                    // To stop the automatic movement, you can simply set it back to the last valid position
                    binding.viewPager2.setCurrentItem(position, false)
                }

                //Can also be use
                /*if (position == 0 || position == (binding.viewPager2.adapter?.itemCount ?: 0) - 1) {
                    // Disable automatic scrolling by setting current item without smooth scrolling
                    binding.viewPager2.setCurrentItem(position, true)
                }*/
            }
        })


        binding.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"))

        supportActionBar?.title = "Todo"
        supportActionBar?.elevation = 0F


//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//        navController = navHostFragment.navController

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.reCreate){
            recreate()
        }
        return super.onOptionsItemSelected(item)
/*        return when (item.itemId) {
            R.id.reCreate -> {
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }*/
    }


}