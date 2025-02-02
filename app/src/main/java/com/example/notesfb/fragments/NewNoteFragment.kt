package com.example.notesfb.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.notesfb.R
import com.example.notesfb.activities.MyApp
import com.example.notesfb.databinding.FragmentNewNoteBinding
import com.example.notesfb.models.Note
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewNoteFragment : Fragment() {

    private lateinit var binding: FragmentNewNoteBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        val menuHost: MenuHost = requireActivity()


        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId)
                {
                    R.id.save->{
                        val title = binding.noteTitle.text.toString()
                        val description = binding.noteDescription.text.toString()
                        addNote(title, description)
                        setFragment(NotesFragment.newInstance(), R.id.place)
                    }
                    android.R.id.home-> {
                        setFragment(NotesFragment.newInstance(), R.id.place)
                    }
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        val actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.setTitle(resources.getString(R.string.new_note))
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun init() = with(binding)
    {
        firebaseAuth = (context?.applicationContext as MyApp).firebaseAuth
        firestore = (context?.applicationContext as MyApp).firestore
    }

    private fun addNote(title: String,description:String)
    {
        val note = Note(title, description)
        firestore.collection("users")
            .document(firebaseAuth.uid ?: "")
            .collection("notes")
            .add(note)
    }




    private fun setFragment(fragment: Fragment, place: Int)
    {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(place, fragment)
            .commit()
    }



    companion object {

        @JvmStatic
        fun newInstance() = NewNoteFragment()
    }
}