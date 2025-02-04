package com.example.notesfb.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.renderscript.Element.DataType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.notesfb.R
import com.example.notesfb.activities.MyApp
import com.example.notesfb.databinding.FragmentNewNoteBinding
import com.example.notesfb.models.Note
import com.example.notesfb.viewModels.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

class NewNoteFragment : Fragment() {

    private lateinit var binding: FragmentNewNoteBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: NoteViewModel by activityViewModels{
        NoteViewModel.NoteViewModelFactory()
    }
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

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId)
                {
                    R.id.save->{
                        val title = binding.noteTitle.text.toString()
                        val description = binding.noteDescription.text.toString()
                        if(viewModel.getSelectedItem() != null)
                        {
                            Log.d("MyLog", "update")
                            var item = viewModel.getSelectedItem()
                            item = item?.copy(title = title, description = description, savedInFirestore = false)
                            updateNote(item!!)
                        }
                        else {
                            val note = Note(title, description, getCurrentTime(), false)
                            addNote(note)
                        }
                        setFragment(NotesFragment.newInstance(), R.id.place)
                        viewModel.setSelectedItem(null)
                    }
                    android.R.id.home-> {
                        setFragment(NotesFragment.newInstance(), R.id.place)
                        viewModel.setSelectedItem(null)
                    }
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        val actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.setTitle(resources.getString(R.string.new_note))
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNote(note: Note)
    {
        val userCollection = firestore.collection("users")
            .document(firebaseAuth.uid ?: "")
            .collection("notes")

        userCollection
            .whereEqualTo("lastTimeUpdate", note.lastTimeUpdate)
            .get()
            .addOnSuccessListener { snapshot->
                if(!snapshot.isEmpty)
                {
                    for(document in snapshot.documents)
                    {
                        document.reference.update("title", note.title)
                        document.reference.update("description", note.description)
                        document.reference.update("lastTimeUpdate", getCurrentTime())
                    }
                }
            }
    }

    private fun updateSavedStatus(lastTimeUpdate: String)
    {
        val userCollection = firestore.collection("users")
            .document(firebaseAuth.uid ?: "")
            .collection("notes")

        userCollection
            .whereEqualTo("lastTimeUpdate", lastTimeUpdate)
            .get()
            .addOnSuccessListener { snapshot->
                if(!snapshot.isEmpty)
                {
                    for(document in snapshot.documents)
                    {
                        document.reference.update("savedInFirestore", true)
                    }
                }
            }
    }


    private fun init() = with(binding)
    {
        firebaseAuth = (context?.applicationContext as MyApp).firebaseAuth
        firestore = (context?.applicationContext as MyApp).firestore

        if(viewModel.getSelectedItem() != null)
        {
            val item = viewModel.getSelectedItem()
            noteTitle.setText(item?.title)
            noteDescription.setText(item?.description)
            lastUpdate.setText(item?.lastTimeUpdate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addNote(note: Note)
    {
        val item = firestore.collection("users")
            .document(firebaseAuth.uid ?: "")
            .collection("notes")
            .add(note)
        item.addOnSuccessListener {
            updateSavedStatus(note.lastTimeUpdate)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String
    {
        val formatter = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
        val date = Date()
        return formatter.format(date)
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