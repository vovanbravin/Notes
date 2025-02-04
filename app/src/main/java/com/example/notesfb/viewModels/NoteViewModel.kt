package com.example.notesfb.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notesfb.models.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.protobuf.Internal.BooleanList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel(): ViewModel() {
    private var _notes = MutableStateFlow(listOf<Note>())

    val notes = _notes.asStateFlow()

    private var selectedItem: Note? = null

    private var rememberUser: Boolean = false

    fun setRemeberUser(status: Boolean)
    {
        this.rememberUser = status
    }

    fun getRememberUser(): Boolean
    {
        return this.rememberUser
    }

    fun setSelectedItem(selectedItem: Note?)
    {
        this.selectedItem = selectedItem
    }

    fun getSelectedItem(): Note?
    {
        return this.selectedItem
    }

    fun setNotes(notes: List<Note>)
    {
        _notes.value = notes
    }


    class NoteViewModelFactory(): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(NoteViewModel::class.java))
            {
                @Suppress("UNCHECKED CAST")
                return NoteViewModel() as T
            }
            throw IllegalArgumentException("UNKNOWN ViewModel")
        }
    }

}