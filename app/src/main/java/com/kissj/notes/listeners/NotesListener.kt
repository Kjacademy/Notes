package com.kissj.notes.listeners

import com.kissj.notes.entities.Note

interface NotesListener {
    fun noteClicked(note: Note, position: Int)
}