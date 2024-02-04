package com.example.hotdogexpres.adapters

import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.R
import com.example.hotdogexpres.classes.menuItems
import com.example.hotdogexpres.classes.review

class menuItemAdapter(private val currentMenuItemId: String, private val listOfMenuItems: ArrayList<menuItems>) :
    RecyclerView.Adapter<menuItemAdapter.menuViewHolder>() {


    private var onDeleteClickListener: OnDeleteClickListener? = null


    interface OnDeleteClickListener {
        fun onDeleteClick(menu: menuItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): menuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return menuViewHolder(view)
    }

    override fun onBindViewHolder(holder: menuViewHolder, position: Int) {
        val item = listOfMenuItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return listOfMenuItems.size
    }



    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    inner class menuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameItem: TextView = itemView.findViewById(R.id.menuItemNameTxt)
        var menuItemImg: ImageView = itemView.findViewById(R.id.menuItemImg)
        var menuItemDeleteBtn: Button = itemView.findViewById(R.id.menuItemDeleteBtn)

        fun bind(menu: menuItems) {

            nameItem.text = menu.nameItem

            if (menu.typeItem == "food") {
                menuItemImg.setImageResource(R.drawable.pasta_img)
            } else if (menu.typeItem == "drinks"){
                menuItemImg.setImageResource(R.drawable.wine_img)
            }

            if (currentMenuItemId == menu.userId) {
                // Set visibility of the delete button based on whether it's the current user's review
                menuItemDeleteBtn.isVisible = true
            } else {
                menuItemDeleteBtn.isVisible = false
            }



            menuItemDeleteBtn.setOnClickListener {
                onDeleteClickListener?.onDeleteClick(menu)
            }
        }







        private fun isNetworkConnected(): Boolean {
            val connectivityManager = itemView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }





    }
}