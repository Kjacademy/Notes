package com.kissj.notes.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kissj.notes.R
import com.kissj.notes.adapters.NotesAdapter
import com.kissj.notes.database.NotesDatabase
import com.kissj.notes.entities.Note

class MainActivity : ComponentActivity() {
    private lateinit var notesRecycleView: RecyclerView
    private val notesList = ArrayList<Note>()
    private lateinit var notesAdapter: NotesAdapter
    private val openCreateNoteActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                getNodes()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val imageAddNoteMain: ImageView = findViewById(R.id.imageAddNoteMain)
        imageAddNoteMain.setOnClickListener {
            val createNoteActivityIntent = Intent(applicationContext, CreateNoteActivity::class.java)
            openCreateNoteActivity.launch(createNoteActivityIntent)
        }

        notesAdapter = NotesAdapter(notesList)
        notesRecycleView = findViewById(R.id.notesRecyclerView)
        notesRecycleView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesRecycleView.adapter = notesAdapter

        getNodes()
    }

    private fun getNodes() {

        class GetNotesTask : AsyncTask<Void, Void, List<Note>>() {

            override fun doInBackground(vararg p0: Void?): List<Note> {
                return NotesDatabase.getInstance(applicationContext).noteDao().getAllNotes()
            }

            override fun onPostExecute(result: List<Note>?) {
                super.onPostExecute(result)

                if (result != null) {
                    if (notesList.size == 0) {
                        notesList.addAll(result)
                        notesAdapter.notifyDataSetChanged()
                    } else {
                        notesList.add(result[0])
                        notesAdapter.notifyItemInserted(0)
                    }
                    notesRecycleView.smoothScrollToPosition(0)
                }
            }
        }

        GetNotesTask().execute()
    }
}