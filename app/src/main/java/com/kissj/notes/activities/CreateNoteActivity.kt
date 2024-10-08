package com.kissj.notes.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kissj.notes.R
import com.kissj.notes.database.NotesDatabase
import com.kissj.notes.entities.Note
import java.time.LocalDate
import java.util.Locale


const val REQUEST_CODE_STORAGE_PERMISSION = 1

class CreateNoteActivity : ComponentActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteSubtitle: EditText
    private lateinit var inputNote: EditText
    private lateinit var textDateTime: TextView
    private lateinit var viewSubtitleIndicator: View
    private lateinit var imageNote: ImageView
    private lateinit var textWebUrl: TextView
    private lateinit var layoutWebUrl: LinearLayout
    private lateinit var dialogAddUrl: AlertDialog

    private lateinit var alreadyAvailableNote: Note

    private var selectedNoteColor = "#333333"
    private var selectedImagePath = ""
    private val openGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null && it.data?.data != null) {
                    val uri = it.data?.data!!
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    setImageNoteBitmap(bitmap)
                    selectedImagePath = getPathFromUri(uri)
                }
            }
        }

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
        imageNote = findViewById(R.id.imageNote)

        textWebUrl = findViewById(R.id.textWebUrl)
        layoutWebUrl = findViewById(R.id.layoutWebURL)

        if (intent.getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = intent.getSerializableExtra("note") as Note
            setNote(note = alreadyAvailableNote)
        }

        initMiscellaneous()
        setSubtitleIndicatorColor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNote() {
        if (inputNoteTitle.text.isNullOrBlank()) {
            Toast.makeText(this, "Note title is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        if (inputNote.text.isNullOrBlank()) {
            Toast.makeText(this, "Note content is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            id = alreadyAvailableNote?.id,
            title = inputNoteTitle.text.toString(),
            subTitle = inputNoteSubtitle.text.toString(),
            dateTime = textDateTime.text.toString(),
            noteText = inputNote.text.toString(),
            color = selectedNoteColor,
            imagePath = selectedImagePath,
            webLink = textWebUrl.text?.toString() ?: ""
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

        if (alreadyAvailableNote.color.isNotBlank()) {
            when (alreadyAvailableNote.color) {
                "#333333" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor1).performClick()
                "#fdbe3b" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor2).performClick()
                "#ff4842" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor3).performClick()
                "#3a52fc" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor4).performClick()
                "#000000" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor5).performClick()
            }
        }

        layoutMiscellaneous.findViewById<LinearLayout>(R.id.layoutAddImage).setOnClickListener {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) { // check if we already have permission
                    val uri = Uri.parse(
                        java.lang.String.format(
                            Locale.ENGLISH,
                            "package:%s",
                            applicationContext.packageName
                        )
                    )
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            uri
                        )
                    )
                } else {
                    selectImage()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                } else {
                    selectImage()
                }
            }
        }

        layoutMiscellaneous.findViewById<LinearLayout>(R.id.layoutAddUrl).setOnClickListener {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddUrlDialog()
        }

    }

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable: GradientDrawable =
            viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGallery.launch(intent)
    }

    private fun getPathFromUri(contentUri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path ?: ""
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }

        return filePath
    }

    @SuppressLint("ResourceType")
    private fun showAddUrlDialog() {
        if (!this::dialogAddUrl.isInitialized) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(applicationContext).inflate(
                R.layout.layout_add_url,
                findViewById(R.id.layoutAddUrlContainer)
            )

            alertDialogBuilder.setView(view)
            dialogAddUrl = alertDialogBuilder.create()

            dialogAddUrl.window?.let {
                dialogAddUrl.window!!.setBackgroundDrawable(ColorDrawable(0))

            }


            val inputUrl = view.findViewById<EditText>(R.id.inputUrl)
            inputUrl.requestFocus()

            view.findViewById<TextView>(R.id.textAdd).setOnClickListener {
                if (inputUrl.text.isNullOrBlank()) {
                    Toast.makeText(applicationContext, "Enter URL ", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.WEB_URL.matcher(inputUrl.text).matches()) {
                    Toast.makeText(applicationContext, "Enter a valid URL ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    textWebUrl.text = inputUrl.text
                    layoutWebUrl.visibility = View.VISIBLE
                    dialogAddUrl.dismiss()
                }
            }

            view.findViewById<TextView>(R.id.textCancel).setOnClickListener {
                dialogAddUrl.dismiss()
            }
        }
        dialogAddUrl.show()
    }

    private fun setImageNoteBitmap(bitmap: Bitmap?) {
        imageNote.setImageBitmap(bitmap)
        imageNote.visibility = View.VISIBLE
    }

    private fun setNote(note: Note) {
        inputNoteTitle.setText(note.title)
        inputNoteSubtitle.setText(note.subTitle)
        textDateTime.text = note.dateTime
        inputNote.setText(note.noteText)
        selectedNoteColor = note.color
        setSubtitleIndicatorColor()

        if (note.imagePath.isNotBlank()) {
            val bitmap = BitmapFactory.decodeFile(note.imagePath)
            setImageNoteBitmap(bitmap)
            selectedImagePath = note.imagePath
        }

        if (note.webLink.isNotBlank()) {
            textWebUrl.text = note.webLink
            layoutWebUrl.visibility = View.VISIBLE
        }
    }
}