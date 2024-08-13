package com.kissj.notes.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kissj.notes.R
import com.kissj.notes.database.NotesDatabase
import com.kissj.notes.entities.Note
import java.time.LocalDate

class CreateNoteActivity : ComponentActivity() {
    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteSubtitle: EditText
    private lateinit var inputNote: EditText
    private lateinit var textDateTime: TextView
    private lateinit var viewSubtitleIndicator: View
    private var selectedNoteColor = "#333333"

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
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator)

        textDateTime.setText(LocalDate.now().toString())

        initMiscellaneous()
        setSubtitleIndicatorColor()
    }

    private fun saveNote() {
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
            noteText = inputNote.text.toString(),
            color = selectedNoteColor
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

    private fun initMiscellaneous() {
        val layoutMiscellaneous: LinearLayout = findViewById(R.id.layoutMiscellaneous)
        val bottomSheetBehaviour: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(layoutMiscellaneous)
        layoutMiscellaneous.findViewById<TextView>(R.id.textMiscellaneous).setOnClickListener {
            if (bottomSheetBehaviour.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        val imageColor1 = layoutMiscellaneous.findViewById<ImageView>(R.id.imageColor1)
        val imageColor2 = layoutMiscellaneous.findViewById<ImageView>(R.id.imageColor2)
        val imageColor3 = layoutMiscellaneous.findViewById<ImageView>(R.id.imageColor3)
        val imageColor4 = layoutMiscellaneous.findViewById<ImageView>(R.id.imageColor4)
        val imageColor5 = layoutMiscellaneous.findViewById<ImageView>(R.id.imageColor5)

        layoutMiscellaneous.findViewById<View>(R.id.viewColor1).setOnClickListener {
            selectedNoteColor = "#333333"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor2).setOnClickListener {
            selectedNoteColor = "#fdbe3b"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor3).setOnClickListener {
            selectedNoteColor = "#ff4842"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor4).setOnClickListener {
            selectedNoteColor = "#3a52fc"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor5).setOnClickListener {
            selectedNoteColor = "#000000"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            setSubtitleIndicatorColor()
        }

    }

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable: GradientDrawable =
            viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }
}