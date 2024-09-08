package com.example.aleric.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aleric.data.Koncert
import com.example.aleric.repository.KoncertRepository
import kotlinx.coroutines.launch

class KoncertViewModel(private val repository: KoncertRepository) : ViewModel() {

    private val _koncerts = MutableLiveData<List<Koncert>>()
    val koncerts: LiveData<List<Koncert>> get() = _koncerts

    fun fetchKoncerts() {
        viewModelScope.launch {
            _koncerts.value = repository.getAllKoncerts()
        }
    }

    fun addKoncert(koncert: Koncert) {
        viewModelScope.launch {
            repository.insert(koncert)
            fetchKoncerts()
        }
    }

    fun updateKoncert(koncert: Koncert) {
        viewModelScope.launch {
            repository.update(koncert)
            fetchKoncerts()
        }
    }

    fun deleteKoncert(koncert: Koncert) {
        viewModelScope.launch {
            repository.delete(koncert)
            fetchKoncerts()
        }
    }
}