package com.example.aleric.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "koncerts")
data class Koncert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val naziv: String,
    val izvođač: String,
    val lokacija: String,
    val datum: String,
    val slika: String,
    val opis: String
) : Parcelable