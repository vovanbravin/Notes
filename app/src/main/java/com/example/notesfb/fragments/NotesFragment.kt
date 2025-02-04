package com.example.notesfb.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notesfb.R
import com.example.notesfb.activities.MyApp
import com.example.notesfb.adapters.NoteAdapter
import com.example.notesfb.databinding.FragmentNotesBinding
import com.example.notesfb.models.Note
import com.example.notesfb.viewModels.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration


class NotesFragment : Fragment(), View.OnClickListener, NoteAdapter.NoteListener {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: NoteAdapter
    private lateinit var listener: ListenerRegistration
    private val viewModel: NoteViewModel by activityViewModels{
        NoteViewModel.NoteViewModelFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

       menuHost.addMenuProvider(object: MenuProvider
       {
           override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
               menuInflater.inflate(R.menu.notes_menu, menu)
           }

           override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
               when(menuItem.itemId)
               {
                   R.id.signOut-> {
                       val snackbar = Snackbar.make(requireView(), resources.getString(R.string.singOutSnacbarText), Snackbar.LENGTH_LONG)
                       snackbar.setAction(resources.getString(R.string.Verify), object: View.OnClickListener{
                           override fun onClick(v: View?) {
                               setFragment(LoginFragment.newInstance(), R.id.place)
                               firebaseAuth.signOut()
                           }

                       })
                       snackbar.show()
                   }
                   R.id.delete_all->
                   {
                       val snackbar = Snackbar.make(requireView(), resources.getString(R.string.verify_delete_all_notes), Snackbar.LENGTH_LONG)
                       snackbar.setAction(resources.getString(R.string.Verify), object: View.OnClickListener{
                           override fun onClick(v: View?) {
                                deleteAllNotes()
                           }

                       })
                       snackbar.show()
                   }
                   R.id.account->
                   {
                       setFragment(AccountFragment.newInstance(), R.id.place)
                   }


               }
                return true
           }

       },viewLifecycleOwner, Lifecycle.State.RESUMED)

        val actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.setTitle(resources.getString(R.string.notes))
        actionBar?.setDisplayHomeAsUpEnabled(false)

        init()
        getNotes()
        subscribe()
    }


    private fun deleteAllNotes()
    {
        viewModel.notes.value.forEach {
            deleteNote(it)
        }
    }


    private fun getNotes()
    {
        listener =  firestore.collection("users")
            .document(firebaseAuth.uid?:"")
            .collection("notes")
            .addSnapshotListener { data, error ->
                val isLocal = data?.metadata?.hasPendingWrites()
                firestore.collection("users")
                    .document(firebaseAuth.uid ?: "")
                    .collection("notes")
                    .document()
                    .update("savedInFirestore", isLocal)
                val list = data?.toObjects(Note::class.java) ?: emptyList<Note>()
                viewModel.setNotes(list)
                }
    }

    private fun subscribe()
    {
        lifecycleScope.launch {
            viewModel.notes.collect{
                adapter.submitList(it)
            }
        }
    }

    private fun init() = with(binding)
    {
        add.setOnClickListener(this@NotesFragment)

        firebaseAuth = (context?.applicationContext as MyApp).firebaseAuth
        firestore = (context?.applicationContext as MyApp).firestore

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteNote(viewModel.notes.value.get(position))

            }

        }


        rcView.layoutManager = GridLayoutManager(activity, 2)
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rcView)
        adapter = NoteAdapter(requireContext(), this@NotesFragment)
        rcView.adapter = adapter
    }

    override fun onClickItem(note: Note) {
        viewModel.setSelectedItem(note)
        setFragment(NewNoteFragment.newInstance(), R.id.place)
    }

    private fun deleteNote(note: Note)
    {
        val userCollection =  firestore.collection("users")
            .document(firebaseAuth.uid ?: "")
            .collection("notes")

        userCollection
            .whereEqualTo("lastTimeUpdate", note.lastTimeUpdate)
            .get()
            .addOnSuccessListener { snapshot ->
                if(!snapshot.isEmpty)
                {
                    for (document in snapshot.documents)
                    {
                        document.reference.delete()
                    }
                }
            }

    }


    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.add->{
                setFragment(NewNoteFragment.newInstance(), R.id.place)
            }
        }
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
        fun newInstance() = NotesFragment()
    }




}