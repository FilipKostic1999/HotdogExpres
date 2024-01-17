package com.example.hotdogexpres.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.R
import com.example.hotdogexpres.classes.review

class reviewAdapter(private val listOfReviews: ArrayList<review>) :
    RecyclerView.Adapter<reviewAdapter.reviewViewHolder>() {


    private var onViewClickListener: OnViewClickListener? = null


    interface OnViewClickListener {
        fun onViewClick(Review: review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): reviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return reviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: reviewViewHolder, position: Int) {
        val review = listOfReviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return listOfReviews.size
    }



    fun setOnViewClickListener(listener: OnViewClickListener) {
        onViewClickListener = listener
    }

    inner class reviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.nameUserTxt)
        val ratingBarReview: RatingBar = itemView.findViewById(R.id.ratingBarUserReview)
        val reviewText: TextView = itemView.findViewById(R.id.userReviewTxt)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)

        fun bind(Review: review) {
            name.text = Review.nameReviewer
            reviewText.text = Review.reviewText
            ratingBarReview.rating = Review.reviewRating



            deleteBtn.setOnClickListener {
                onViewClickListener?.onViewClick(Review)
            }
        }






        private fun isNetworkConnected(): Boolean {
            val connectivityManager = itemView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }





    }
}
