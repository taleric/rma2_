package com.example.aleric

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.aleric.data.AppDatabase
import com.example.aleric.repository.KoncertRepository
import com.example.aleric.viewmodel.KoncertViewModel
import com.example.aleric.viewmodel.KoncertViewModelFactory
import com.example.aleric.R
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel: KoncertViewModel by viewModels {
        KoncertViewModelFactory(KoncertRepository(AppDatabase.getDatabase(this).koncertDao()))
    }

    private lateinit var addButton: Button
    private lateinit var noConcertsTextView: TextView
    private lateinit var concertsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton = findViewById(R.id.addButton)
        noConcertsTextView = findViewById(R.id.noConcertsTextView)
        concertsLayout = findViewById(R.id.concertsLayout)

        addButton.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivityForResult(intent, ADD_CONCERT_REQUEST_CODE)
        }

        viewModel.koncerts.observe(this, Observer { koncerts ->
            if (koncerts.isEmpty()) {
                fadeIn(noConcertsTextView)
                fadeOut(concertsLayout)
            } else {
                fadeOut(noConcertsTextView)
                fadeIn(concertsLayout)
                concertsLayout.removeAllViews()
                for (koncert in koncerts) {
                    val concertView = layoutInflater.inflate(R.layout.item_concert, concertsLayout, false)
                    val imageView = concertView.findViewById<ImageView>(R.id.concertImageView)
                    val nazivTextView = concertView.findViewById<TextView>(R.id.nazivTextView)
                    val izvođačTextView = concertView.findViewById<TextView>(R.id.izvođačTextView)
                    val lokacijaTextView = concertView.findViewById<TextView>(R.id.lokacijaTextView)
                    val datumTextView = concertView.findViewById<TextView>(R.id.datumTextView)
                    val opisTextView = concertView.findViewById<TextView>(R.id.opisTextView)
                    val editButton = concertView.findViewById<Button>(R.id.editButton)
                    val deleteButton = concertView.findViewById<Button>(R.id.deleteButton)

                    nazivTextView.text = "Naziv: ${koncert.naziv}"
                    izvođačTextView.text = "Izvođač: ${koncert.izvođač}"
                    lokacijaTextView.text = "Lokacija: ${koncert.lokacija}"
                    datumTextView.text = "Datum: ${koncert.datum}"
                    opisTextView.text = "Opis: ${koncert.opis}"

                    if (koncert.slika.isNotEmpty()) {
                        val fileUri = Uri.parse(koncert.slika)
                        Glide.with(this)
                            .load(fileUri)
                            .into(imageView)
                    }
                    editButton.setOnClickListener {
                        val intent = Intent(this, AddEditActivity::class.java).apply {
                            putExtra("koncert", koncert)
                        }
                        startActivityForResult(intent, EDIT_CONCERT_REQUEST_CODE)  // Updated to startActivityForResult
                    }

                    deleteButton.setOnClickListener {
                        // Confirm deletion with a dialog
                        AlertDialog.Builder(this)
                            .setTitle("Delete Concert")
                            .setMessage("Are you sure you want to delete this concert?")
                            .setPositiveButton("Yes") { _, _ ->
                                viewModel.deleteKoncert(koncert)
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                    concertsLayout.addView(concertView)
                }
            }
        })

        viewModel.fetchKoncerts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && (requestCode == ADD_CONCERT_REQUEST_CODE || requestCode == EDIT_CONCERT_REQUEST_CODE)) {
            // Fetch updated list of concerts
            viewModel.fetchKoncerts()
        }
    }

    companion object {
        private const val ADD_CONCERT_REQUEST_CODE = 1
        private const val EDIT_CONCERT_REQUEST_CODE = 2
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
