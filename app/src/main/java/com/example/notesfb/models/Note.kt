package com.example.notesfb.models

data class Note(
    val title: String = "",
    val description: String = "",
    val lastTimeUpdate: String = "",
    val savedInFirestore: Boolean = false
)
