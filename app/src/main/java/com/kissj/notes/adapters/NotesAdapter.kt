package com.kissj.notes.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kissj.notes.R
import com.kissj.notes.entities.Note
import com.makeramen.roundedimageview.RoundedImageView

class NotesAdapter(val notes: List<Note>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textSubtitle: TextView = itemView.findViewById(R.id.textSubtitle)
        private val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.layoutNote)
        private val imageNote: RoundedImageView = itemView.findViewById(R.id.imageNote)

        fun setNote(note: Note) {
            textTitle.text = note.title
            textSubtitle.text = note.subTitle
            textDateTime.text = note.dateTime

            val gradientDrawable = linearLayout.background as GradientDrawable
            if (note.color.isNotBlank()) {
                gradientDrawable.setColor(Color.parseColor(note.color))
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"))
            }

            if (note.imagePath.isNotBlank()) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                imageNote.visibility = View.VISIBLE
            } else {
                imageNote.visibility = View.GONE
            }
        }
    }
}