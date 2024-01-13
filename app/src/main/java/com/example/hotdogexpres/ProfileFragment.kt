package com.example.hotdogexpres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout


class ProfileFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access interactive elements by their IDs
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)
        val nameEt: EditText = view.findViewById(R.id.nameEt)
        val cardNumber: EditText = view.findViewById(R.id.cardNumberEt)
        val accountDetailsTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_account_details, null)
        val creditCardTab = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_creditcard, null)
        // Add tabs with custom views
        val accountDetailsTxt = tabLayout.newTab()
        accountDetailsTxt.customView = accountDetailsTab
        tabLayout.addTab(accountDetailsTxt)

        val creditCardTxt = tabLayout.newTab()
        creditCardTxt.customView = creditCardTab
        tabLayout.addTab(creditCardTxt)

        // Add a TabLayout.OnTabSelectedListener to listen for tab selection changes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Handle tab selection
                when (tab.position) {

                    0 -> {
                        nameEt.visibility = View.VISIBLE
                        cardNumber.visibility = View.GONE
                    }

                    1 -> {
                        cardNumber.visibility = View.VISIBLE
                        nameEt.visibility = View.GONE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselection
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection
            }
        })


    }
}