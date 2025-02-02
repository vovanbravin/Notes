package com.example.notesfb.activities

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyApp: Application() {
    val firebaseAuth by lazy { Firebase.auth }
    val firestore by lazy { Firebase.firestore }
}