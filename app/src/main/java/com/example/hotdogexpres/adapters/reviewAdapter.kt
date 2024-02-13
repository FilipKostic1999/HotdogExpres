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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.R
import com.example.hotdogexpres.classes.review
import com.squareup.picasso.Picasso

class reviewAdapter(private val currentReviewId: String, private val listOfReviews: ArrayList<review>) :
    RecyclerView.Adapter<reviewAdapter.reviewViewHolder>() {


    private var onViewClickListener: OnViewClickListener? = null


    interface OnViewClickListener {
        fun onViewClick(reviews: review)
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
        val userReviewImg: ImageView = itemView.findViewById(R.id.userReviewImg)
        var ratingBarReview: RatingBar = itemView.findViewById(R.id.ratingBarUserReview)
        val reviewText: TextView = itemView.findViewById(R.id.userReviewTxt)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

        fun bind(reviews: review) {
            name.text = reviews.nameReviewer
            reviewText.text = "${reviews.reviewText}"
            ratingBarReview.rating = reviews.reviewRating
            dateTxt.text = "${reviews.date}"
            deleteBtn.isVisible = false


            if (reviews.reviewImgUrl != null && reviews.reviewImgUrl.isNotBlank()) {
                Picasso.get()
                    .load(reviews.reviewImgUrl)
                    .placeholder(R.drawable.user_img) // Placeholder image while loading
                    .error(R.drawable.user_img) // Error image if loading fails
                    .into(userReviewImg)
            } else {
                // Handle empty or null image URL
                userReviewImg.setImageResource(R.drawable.user_img)
            }


            if (currentReviewId == reviews.reviewId) {
                // Set visibility of the delete button based on whether it's the current user's review
                deleteBtn.isVisible = true
            } else if (currentReviewId == "yourReviews") {
                deleteBtn.isVisible = true
            }



            deleteBtn.setOnClickListener {
                onViewClickListener?.onViewClick(reviews)
            }
        }







        private fun isNetworkConnected(): Boolean {
            val connectivityManager = itemView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }





    }
}
