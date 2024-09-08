package com.example.aleric.repository

import com.example.aleric.data.Koncert
import com.example.aleric.data.KoncertDao

class KoncertRepository(private val koncertDao: KoncertDao) {

    suspend fun insert(koncert: Koncert) {
        koncertDao.insert(koncert)
    }

    suspend fun update(koncert: Koncert) {
        koncertDao.update(koncert)
    }

    suspend fun delete(koncert: Koncert) {
        koncertDao.delete(koncert)
    }

    suspend fun getAllKoncerts(): List<Koncert> {
        return koncertDao.getAllKoncerts()
    }
}