package com.example.hotdogexpres.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotdogexpres.R
import com.example.hotdogexpres.classes.messages

class MessagesAdapter(private val messages: List<messages>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTxt1: TextView = itemView.findViewById(R.id.messageTxt1)
        private val messageTxt2: TextView = itemView.findViewById(R.id.messageTxt2)
        private val image1: ImageView = itemView.findViewById(R.id.chatImg1)
        private val image2: ImageView = itemView.findViewById(R.id.chatImg2)

        fun bind(message: messages) {

            if (message.userId.toInt() == 1) {
                messageTxt1.text = message.text
                image1.setImageResource(R.drawable.chatbot_img)

                image2.visibility = View.GONE
                messageTxt2.visibility = View.GONE

            } else if (message.userId.toInt() == 2) {
                messageTxt2.text = message.text
                image2.setImageResource(R.drawable.user_img)

                image1.visibility = View.GONE
                messageTxt1.visibility = View.GONE
            }


        }
    }
}