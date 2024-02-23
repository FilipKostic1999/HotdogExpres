package com.example.hotdogexpres

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setActivityBackgroundColor(R.color.orange)
        // Set the default fragment when the app starts
        if (savedInstanceState == null) {
            replaceFragment(MapsFragment())
            bottomNavigationView.menu.findItem(R.id.nav_map)?.isChecked = true
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    replaceFragment(MapsFragment())
                    setActivityBackgroundColor(R.color.orange)
                    true
                }
                R.id.nav_AI -> {
                    replaceFragment(AIfragment())
                    setActivityBackgroundColor(R.color.orange)
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    setActivityBackgroundColor(R.color.white)
                    true
                }
                R.id.nav_login -> {
                    replaceFragment(LoginFragment())
                    setActivityBackgroundColor(R.color.orange)
                    true
                }
                // Add more cases for other fragments if needed
                else -> false
            }
        }
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setActivityBackgroundColor(colorResId: Int) {
        val color = resources.getColor(colorResId, theme)
        window.decorView.setBackgroundColor(color)
    }
}



