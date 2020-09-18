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
import com.example.notesharing.ui.fragment.AllReceivedFragmentDirections
import kotlinx.android.synthetic.main.received_note_layout.view.*

class MyReceivedNotesAdapter(
    private val allReceivedFragmentDirections: AllReceivedFragmentDirections.Companion,
    private val findNavController: NavController
) : RecyclerView.Adapter<MyReceivedNotesAdapter.MyViewHolder>() {

    var allReceivedNotesList = emptyList<OneNoteModel>()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    fun setData(allReceivedNotesList: List<OneNoteModel>) {
        this.allReceivedNotesList = allReceivedNotesList
        Log.d("ReceivedNotesFragment", "data found ")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.received_note_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allReceivedNotesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReceivedNote = allReceivedNotesList[position]
        Log.d("ReceivedNotesFragment", "set ----> $currentReceivedNote")

        holder.itemView.r_decoration_line.setCardBackgroundColor(Color.parseColor(currentReceivedNote.color.toString()))
        holder.itemView.r_decoration_line2.setBackgroundColor(Color.parseColor(currentReceivedNote.color.toString()))
        holder.itemView.received_note_title.text = currentReceivedNote.title
        holder.itemView.received_note_date.text = currentReceivedNote.date
        holder.itemView.received_note_time.text = currentReceivedNote.time
        holder.itemView.received_note_body.text = currentReceivedNote.body
        holder.itemView.received_sent_by.text = currentReceivedNote.createdBy

        val edited = currentReceivedNote.edited
        if (edited!!) {
            holder.itemView.received_edited_by.visibility = View.VISIBLE
            holder.itemView.received_edited_by.text = "Edited by me"
        } else {
            holder.itemView.received_edited_by.visibility = View.GONE
        }

        holder.itemView.noteReceivedCardFrame.setOnClickListener {
            val action = allReceivedFragmentDirections.actionAllReceivedFragmentToVAndEReceivedFragment(currentReceivedNote)
            findNavController.navigate(action)
        }
    }
}