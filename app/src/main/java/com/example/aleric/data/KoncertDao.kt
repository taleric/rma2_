package com.example.aleric.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface KoncertDao {

    @Insert
    suspend fun insert(koncert: Koncert)

    @Update
    suspend fun update(koncert: Koncert)

    @Delete
    suspend fun delete(koncert: Koncert)

    @Query("SELECT * FROM koncerts")
    suspend fun getAllKoncerts(): List<Koncert>
}
