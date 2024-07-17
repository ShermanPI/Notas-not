package com.example.notasnot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notasnot.databinding.ActivityUpdateNoteBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1
    private var selectedImage: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_update_note)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)


        if(noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteById(noteId)


        binding.updateTitleEditText.setText(note?.title)
        binding.updateContentEditText.setText(note?.content)
        if (note?.image != null) {
            binding.updateNoteImageView.setImageBitmap(note.image)
        }


        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            if(selectedImage === null){
                selectedImage = note?.image
            }
            val updatedNote = Note(noteId, newTitle, newContent, dateFormat.format(Date()), selectedImage)
            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show()
        }

        binding.updateSelectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                selectedImage = BitmapFactory.decodeStream(inputStream)
                binding.updateNoteImageView.setImageBitmap(selectedImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}