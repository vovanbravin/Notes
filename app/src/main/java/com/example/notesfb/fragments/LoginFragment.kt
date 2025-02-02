package com.example.notesfb.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.example.notesfb.R
import com.example.notesfb.activities.MyApp
import com.example.notesfb.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding)
    {
        firebaseAuth = (context?.applicationContext as MyApp).firebaseAuth

        create.setOnClickListener(this@LoginFragment)
        signIn.setOnClickListener(this@LoginFragment)
        signUp.setOnClickListener(this@LoginFragment)
    }

    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.create->{
                signUp()
            }
            R.id.signIn-> {
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        setFragment(NotesFragment.newInstance(), R.id.place)
                    }
                    .addOnFailureListener{

                    }

            }
            R.id.signUp->{
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                val repPassword = binding.repPassword.text.toString()
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        signIn()
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }
    private fun signUp() = with(binding)
    {
        repPassword.visibility = View.VISIBLE
        tvRepPassword.visibility = View.VISIBLE
        create.visibility = View.GONE
        haveAccount.visibility = View.GONE
        signIn.visibility = View.GONE
        signUp.visibility = View.VISIBLE
    }

    private fun signIn() = with(binding)
    {
        repPassword.visibility = View.GONE
        tvRepPassword.visibility = View.GONE
        create.visibility = View.VISIBLE
        haveAccount.visibility = View.VISIBLE
        signIn.visibility = View.VISIBLE
        signUp.visibility = View.GONE
        email.setText("")
        password.setText("")
        repPassword.setText("")
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
        fun newInstance() = LoginFragment()
    }
}