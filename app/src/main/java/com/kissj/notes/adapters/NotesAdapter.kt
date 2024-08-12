package com.kissj.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kissj.notes.R
import com.kissj.notes.entities.Note

class NotesAdapter(val notes: List<Note>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_note, parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(notes.get(position))
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotesViewHolder : RecyclerView.ViewHolder {
        lateinit var textTitle: TextView
        lateinit var textSubtitle: TextView
        lateinit var textDateTime: TextView

        constructor(itemView: View) : super(itemView) {
            textTitle = itemView.findViewById(R.id.textTitle)
            textSubtitle = itemView.findViewById(R.id.textSubtitle)
            textDateTime = itemView.findViewById(R.id.textDateTime)
        }

        fun setNote(note: Note) {
            textTitle.setText(note.title)
            textSubtitle.setText(note.subTitle)
            textDateTime.setText(note.dateTime)
        }
    }
}