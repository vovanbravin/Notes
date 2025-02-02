package com.example.notesfb.fragments

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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesfb.R
import com.example.notesfb.activities.MyApp
import com.example.notesfb.adapters.NoteAdapter
import com.example.notesfb.databinding.FragmentNotesBinding
import com.example.notesfb.models.Note
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObjects
import kotlin.time.Duration


class NotesFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: NoteAdapter
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
               }
                return true
           }

       },viewLifecycleOwner, Lifecycle.State.RESUMED)

        val actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.setTitle(resources.getString(R.string.notes))
        actionBar?.setDisplayHomeAsUpEnabled(false)

        init()
        suscribe()
    }

    private fun suscribe()
    {
        firestore.collection("users")
            .document(firebaseAuth.uid?:"")
            .collection("notes")
            .get(Source.CACHE)
            .addOnSuccessListener { data ->
                val list = data?.toObjects(Note::class.java) ?: emptyList<Note>()
                adapter.submitList(list)
                }
    }

    private fun init() = with(binding)
    {
        add.setOnClickListener(this@NotesFragment)

        firebaseAuth = (context?.applicationContext as MyApp).firebaseAuth
        firestore = (context?.applicationContext as MyApp).firestore

        rcView.layoutManager = GridLayoutManager(activity, 2)
        adapter = NoteAdapter(requireContext())
        rcView.adapter = adapter
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