package com.example.todolist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.db.MyDbManager

class MyAdapter( listMain: ArrayList<ListItem>, contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    var myList = listMain
    val context = contextM

    class MyHolder(view: View, val contextV: Context) : RecyclerView.ViewHolder(view){

        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val imageIcon: ImageView = view.findViewById(R.id.imageView)
        val context = contextV

        fun setData(item: ListItem){

            tvTitle.text = item.title
            tvTime.text = item.time

            if (item.uri != "empty")
                imageIcon.visibility = View.VISIBLE
            else
                imageIcon.visibility = View.INVISIBLE
            itemView.setOnClickListener{

                val intent = Intent(context, EditActivity::class.java).apply {

                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstants.I_ID, item.id)

                    Log.d("ooo", "I_TITLE_KEY ${MyIntentConstants.I_TITLE_KEY.toString()}")
                    Log.d("ooo", "I_DESC_KEY ${MyIntentConstants.I_DESC_KEY.toString()}")
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return MyHolder(view, context)
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(myList.get(position))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: ArrayList<ListItem>){
        myList.clear()
        myList.addAll(listItems)
        notifyDataSetChanged()
    }
    fun removeItem(position: Int, dbManager: MyDbManager){
        dbManager.removeItemFromDb(myList[position].id.toString())
        myList.removeAt(position)
        notifyItemRangeChanged(0, myList.size)
        notifyItemRemoved(position)

    }
}