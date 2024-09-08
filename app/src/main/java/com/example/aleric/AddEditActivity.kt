package com.example.aleric

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.aleric.data.AppDatabase
import com.example.aleric.repository.KoncertRepository
import com.example.aleric.viewmodel.KoncertViewModel
import com.example.aleric.viewmodel.KoncertViewModelFactory
import com.example.aleric.data.Koncert
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.aleric.R
import java.io.File
import java.io.IOException

class AddEditActivity : AppCompatActivity() {

    private lateinit var nazivEditText: EditText
    private lateinit var izvođačEditText: EditText
    private lateinit var lokacijaEditText: EditText
    private lateinit var datumEditText: EditText
    private lateinit var slikaImageView: ImageView
    private lateinit var opisEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var takePhotoButton: Button
    private lateinit var cancelButton: Button


    private val GALLERY_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002

    private lateinit var currentPhotoPath: String

    private var selectedImageUri: Uri? = null

    private var koncertToEdit: Koncert? = null

    private val viewModel: KoncertViewModel by viewModels {
        KoncertViewModelFactory(KoncertRepository(AppDatabase.getDatabase(applicationContext).koncertDao()))
    }

    private fun applyButtonClickAnimation(button: Button) {
        val clickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click)
        button.startAnimation(clickAnimation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        nazivEditText = findViewById(R.id.nazivEditText)
        izvođačEditText = findViewById(R.id.izvođačEditText)
        lokacijaEditText = findViewById(R.id.lokacijaEditText)
        datumEditText = findViewById(R.id.datumEditText)
        slikaImageView = findViewById(R.id.slikaImageView)
        opisEditText = findViewById(R.id.opisEditText)
        saveButton = findViewById(R.id.saveButton)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        cancelButton = findViewById(R.id.cancelButton)


        koncertToEdit = intent.getParcelableExtra("koncert")

        koncertToEdit?.let { koncert ->
            nazivEditText.setText(koncert.naziv)
            izvođačEditText.setText(koncert.izvođač)
            lokacijaEditText.setText(koncert.lokacija)
            datumEditText.setText(koncert.datum)
            opisEditText.setText(koncert.opis)
            saveButton.text = "Save Event"
            koncert.slika?.let { imageUrl ->
                Glide.with(this).load(imageUrl).into(slikaImageView)
            }
        }

        saveButton.setOnClickListener {
            applyButtonClickAnimation(saveButton)
            val naziv = nazivEditText.text.toString()
            val izvođač = izvođačEditText.text.toString()
            val lokacija = lokacijaEditText.text.toString()
            val datum = datumEditText.text.toString()
            val slika = selectedImageUri?.toString() ?: ""
            val opis = opisEditText.text.toString()

            val koncert = Koncert(
                id = koncertToEdit?.id ?: 0,
                naziv = naziv,
                izvođač = izvođač,
                lokacija = lokacija,
                datum = datum,
                slika = slika,
                opis = opis
            )

            if (koncertToEdit == null) {
                viewModel.addKoncert(koncert)
                Log.d("AddEditActivity", "Added concert: $koncert")

            } else {
                viewModel.updateKoncert(koncert)
                Log.d("AddEditActivity", "Updated concert: $koncert")
            }
            setResult(RESULT_OK)
            finish()
        }

        uploadImageButton.setOnClickListener {
            openGallery()
        }

        takePhotoButton.setOnClickListener {
            openCamera()
        }

        cancelButton.setOnClickListener {
            applyButtonClickAnimation(cancelButton)
            finish()
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = createImageFile()
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    it
                )
                selectedImageUri = photoURI
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = System.currentTimeMillis()
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                currentPhotoPath = absolutePath
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Permission denied
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Camera permission is needed to take photos.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    selectedImageUri = data?.data
                    Glide.with(this).load(selectedImageUri).into(slikaImageView)
                }
                CAMERA_REQUEST_CODE -> {
                    val photoFile = File(currentPhotoPath)
                    if (photoFile.exists()) {
                        selectedImageUri = Uri.fromFile(photoFile)
                        Glide.with(this).load(selectedImageUri).into(slikaImageView)
                    } else {
                        Log.e("AddEditActivity", "Photo file not found")
                    }
                }
            }
        } else {
            Log.e("AddEditActivity", "Result not OK")
        }
    }

    private fun fadeIn(view: View) {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        view.startAnimation(fadeIn)
        view.visibility = View.VISIBLE
    }

    private fun fadeOut(view: View) {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        view.startAnimation(fadeOut)
        view.visibility = View.GONE
    }

}