package com.example.notesfb.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.notesfb.R
import com.example.notesfb.databinding.ActivityMainBinding
import com.example.notesfb.fragments.LoginFragment
import com.example.notesfb.fragments.NotesFragment
import com.example.notesfb.viewModels.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModel.NoteViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        firebaseAuth = (applicationContext as MyApp).firebaseAuth
        firestore = (applicationContext as MyApp).firestore
        if (firebaseAuth.currentUser != null) {
            viewModel.setRemeberUser(true)
            setFragment(NotesFragment.newInstance(), R.id.place)
        } else {
            setFragment(LoginFragment.newInstance(), R.id.place)
        }
    }


    private fun setFragment(f: Fragment, place: Int) {
        supportFragmentManager.beginTransaction()
            .replace(place, f).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyLog", "destroy Main")
        if(!viewModel.getRememberUser())
        {
            firebaseAuth.signOut()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MyLog", "stop Main + ${viewModel.getRememberUser()}")
        if(viewModel.getRememberUser())
        {
            firebaseAuth.signOut()
        }
    }

}