package com.kissj.notes.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.kissj.notes.R
import com.kissj.notes.database.NotesDatabase
import com.kissj.notes.entities.Note
import java.time.LocalDate

class CreateNoteActivity : ComponentActivity() {
    lateinit var inputNoteTitle: EditText
    lateinit var inputNoteSubtitle: EditText
    lateinit var inputNote: EditText
    lateinit var textDateTime: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_note)
        /*  ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
              val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
              v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
              insets
          } */

        val imageBack: ImageView = findViewById(R.id.imageBack)
        imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val imageSave: ImageView = findViewById(R.id.imageSave)
        imageSave.setOnClickListener {
            saveNote()
        }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle)
        inputNote = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.textDateTime)

        textDateTime.setText(LocalDate.now().toString())

    }

    fun saveNote() {
        if (inputNoteTitle.getText().isNullOrBlank()) {
            Toast.makeText(this, "Note title is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        if (inputNote.getText().isNullOrBlank()) {
            Toast.makeText(this, "Note content is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            title = inputNoteTitle.text.toString(),
            subTitle = inputNoteSubtitle.text.toString(),
            dateTime = textDateTime.text.toString(),
            noteText = inputNote.text.toString()
        )

        class SaveTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg p0: Void?): Void? {
                NotesDatabase.getInstance(applicationContext).noteDao().insertNote(note)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        SaveTask().execute()
    }
}