package com.dev.cuckooxa.notesapp.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteModel : ViewModel() {
    val note = MutableLiveData<Note>()
}