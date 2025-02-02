package com.example.notesfb.viewModels

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel(val firestore: FirebaseFirestore,val firebaseAuth: FirebaseAuth): ViewModel() {
    private var _notes = MutableStateFlow(listOf<Note>())

    val notes = _notes.asStateFlow()

    private fun subscribe()
    {
        firestore.collection("users")
            .document(firebaseAuth.uid?:"")
            .collection("notes")
            .get(Source.CACHE)
            .addOnSuccessListener { data ->
                _notes.value = data?.toObjects(Note::class.java) ?: emptyList() }
    }

    class NoteViewModelFactory(val firebaseAuth: FirebaseAuth, val firestore: FirebaseFirestore): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(NoteViewModel::class.java))
            {
                @Suppress("UNCHECKED CAST")
                return NoteViewModel(firestore, firebaseAuth) as T
            }
            throw IllegalArgumentException("UNKNOWN ViewModel")
        }
    }

}