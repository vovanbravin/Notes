package com.example.notesfb.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesfb.R
import com.example.notesfb.databinding.NoteItemBinding
import com.example.notesfb.models.Note

class NoteAdapter(val context: Context, val noteListener: NoteListener): ListAdapter<Note, NoteAdapter.NoteHolder>(NoteCompare()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder.createHolder(parent)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.setData(getItem(position), context, noteListener)
    }


    class NoteHolder(view: View): RecyclerView.ViewHolder(view)
    {
        private val binding = NoteItemBinding.bind(view)

        fun setData(note: Note, context: Context, noteListener: NoteListener) = with(binding)
        {
            title.text = note.title
            description.text = note.description
            indicator.setImageDrawable(
                if(note.savedInFirestore) ContextCompat.getDrawable(context, R.drawable.true_indicator)
                else ContextCompat.getDrawable(context, R.drawable.false_indicator)
            )
            itemView.setOnClickListener {
                noteListener.onClickItem(note)
            }
        }

        companion object
        {
            fun createHolder(parent: ViewGroup): NoteHolder {
                return NoteHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))
            }
        }
    }

    class NoteCompare(): DiffUtil.ItemCallback<Note>()
    {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.lastTimeUpdate == newItem.lastTimeUpdate
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    interface NoteListener{
        fun onClickItem(note: Note)
    }

}