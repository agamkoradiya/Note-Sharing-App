package com.example.notesharing.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notesharing.R
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.ui.fragment.MyAllNotesFragmentDirections
import kotlinx.android.synthetic.main.my_note_layout.view.*

class MyAllNotesAdapter(
    private var myAllNotesFragmentDirections: MyAllNotesFragmentDirections.Companion,
    private val findNavController: NavController
) : RecyclerView.Adapter<MyAllNotesAdapter.MyViewHolder>() {

    var allPersonalNotesList = emptyList<OneNoteModel>()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun setData(allPersonalNotesList: List<OneNoteModel>) {
        this.allPersonalNotesList = allPersonalNotesList
        Log.d("MyAllNotesFragment", "data came")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_note_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allPersonalNotesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPersonalNote = allPersonalNotesList[position]
        Log.d("MyAllNotesFragment", "set ----> $currentPersonalNote")

        holder.itemView.decoration_line.setCardBackgroundColor(Color.parseColor(currentPersonalNote.color.toString()))
        holder.itemView.personal_note_title.text = currentPersonalNote.title
        holder.itemView.personal_note_date.text = currentPersonalNote.date
        holder.itemView.personal_note_time.text = currentPersonalNote.time
        holder.itemView.personal_note_body.text = currentPersonalNote.body
        holder.itemView.noteCardFrame.setOnClickListener {
            val action =
                myAllNotesFragmentDirections.actionMyAllNotesFragmentToViewAndEditPersonalNotesFragment(
                    currentPersonalNote
                )
            findNavController.navigate(action)
        }
    }
}