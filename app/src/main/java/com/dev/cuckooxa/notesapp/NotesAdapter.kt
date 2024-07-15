package com.dev.cuckooxa.notesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.dev.cuckooxa.notesapp.databinding.NoteItemBinding
import com.dev.cuckooxa.notesapp.db.DbManager
import com.dev.cuckooxa.notesapp.extensions.fromUri
import com.dev.cuckooxa.notesapp.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesAdapter(private var items: ArrayList<Note>, private val listener: Listener) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding: NoteItemBinding = NoteItemBinding.bind(itemView)

        fun bind(note: Note, listener: Listener) = with(binding){
            tvTitle.text = note.title
            tvContent.text = note.desc
            ivImageCard.apply {
                visibility = if (note.imageUri != "empty"){
                    /*CoroutineScope(Dispatchers.Main).launch {
                        val imageBitmap = fromUri(note)
                        setImageBitmap(imageBitmap)
                    }*/
                    Glide
                        .with(this)
                        .load(note.imageUri)
                        .dontTransform()
                        .into(this)
                    View.VISIBLE
                } else{
                    View.GONE
                }
            }
            itemView.setOnClickListener {
                listener.onItemClicked(note)
            }
        }
    }

    interface Listener{
        fun onItemClicked(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun changedData(list: List<Note>): Boolean{
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
        return items.isEmpty()
    }

    fun deleteNote(pos: Int, dbManager: DbManager): Boolean{
        dbManager.deleteFromDb(items[pos].id)
        items.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(0, items.size)
        return items.isEmpty()
    }

}