package com.dev.cuckooxa.notesapp.fragments

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev.cuckooxa.notesapp.NotesAdapter
import com.dev.cuckooxa.notesapp.R
import com.dev.cuckooxa.notesapp.databinding.FragmentMainBinding
import com.dev.cuckooxa.notesapp.db.DbManager
import com.dev.cuckooxa.notesapp.model.Note
import com.dev.cuckooxa.notesapp.model.NoteModel
import java.io.Serializable


class MainFragment : Fragment(), NotesAdapter.Listener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var dbManager: DbManager
    private val adapter = NotesAdapter(ArrayList(), this).apply { setHasStableIds(true) }
    private val noteModel: NoteModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("DbManager", DbManager::class.java)!!
        }else{
            arguments?.getSerializable("DbManager") as DbManager
        }
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.slider_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        updateList("")
        binding.searchView.apply {
            isIconified = true
            onActionViewCollapsed()
        }
    }

    private fun updateList(searchText: String){
        val notes = dbManager.readFromDb(searchText)
        ifEmptyList(
            adapter.changedData(notes)
        )
    }

    private fun init() = with(binding){
        fbAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.place_holder, EditFragment.newInstance(dbManager))
                .addToBackStack(null)
                .commit()
        }
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        getTouchHelper().attachToRecyclerView(rvNotes)
        rvNotes.setItemViewCacheSize(20)
        rvNotes.adapter = adapter

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                updateList(p0!!)
                return true
            }

        })
    }

    override fun onItemClicked(note: Note) {
        noteModel.note.value = note
        parentFragmentManager.beginTransaction()
            .replace(R.id.place_holder, EditFragment.newInstance(dbManager, true))
            .addToBackStack(null)
            .commit()
    }

    private fun getTouchHelper(): ItemTouchHelper{
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                ifEmptyList(
                    adapter.deleteNote(viewHolder.adapterPosition, dbManager)
                )
            }

        })
    }

    private fun ifEmptyList(isEmpty: Boolean){
        if (isEmpty) {
            binding.tvNoNotes.text = getString(R.string.no_notes_yet)
        }else {
            binding.tvNoNotes.text = null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dbManager: DbManager) = MainFragment().apply {
            val args = Bundle().apply {
                putSerializable("DbManager", dbManager)
            }
            arguments = args
        }
    }

}