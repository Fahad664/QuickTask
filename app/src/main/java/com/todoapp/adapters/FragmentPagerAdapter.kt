package com.todoapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.todoapp.AllEventsFragment
import com.todoapp.CompletedEventsFragment
import com.todoapp.CancelEventsFragment
import com.todoapp.ActiveFragment

class FragmentPagerAdapter(activity:FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ActiveFragment()
            1 -> CancelEventsFragment()
            2 -> CompletedEventsFragment()
            else -> {
                AllEventsFragment()
            }
        }
    }

}