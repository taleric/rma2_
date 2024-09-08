package com.example.aleric.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aleric.repository.KoncertRepository

class KoncertViewModelFactory(private val repository: KoncertRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KoncertViewModel::class.java)) {
            return KoncertViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}