package com.dev.cuckooxa.notesapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.Target
import com.dev.cuckooxa.notesapp.R
import com.dev.cuckooxa.notesapp.databinding.FragmentEditBinding
import com.dev.cuckooxa.notesapp.db.DbManager
import com.dev.cuckooxa.notesapp.extensions.fromUri
import com.dev.cuckooxa.notesapp.model.NoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private lateinit var dbManager: DbManager
    private val noteModel: NoteModel by activityViewModels()
    private var imageUri: String = "empty"
    private var editing: Boolean = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()){
        if (it != null){
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(it, flag)
            imageUri = it.toString()
            binding.ivImage.setImageURI(it)
        }else{
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("DbManager", DbManager::class.java)!!
        }else{
            arguments?.getSerializable("DbManager") as DbManager
        }
        editing = arguments?.getBoolean("Editing") as Boolean
        val transitionInflater = TransitionInflater.from(requireContext())
        enterTransition = transitionInflater.inflateTransition(R.transition.slider_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (editing){
                noteModel.note.observe(viewLifecycleOwner){
                    etTitle.setText(it.title)
                    etContent.setText(it.desc)
                    if (it.imageUri != "empty"){
                        /*viewLifecycleOwner.lifecycleScope.launch {
                            val bitmap = ivImage.fromUri(it)
                            ivImage.setImageBitmap(bitmap)
                            constraintLayout2.transitionToEnd()
                        }*/
                        Glide
                            .with(this@EditFragment)
                            .load(it.imageUri)
                            .dontTransform()
                            .into(ivImage)
                        constraintLayout2.transitionToEnd()
                        imageUri = it.imageUri
                    }
                }
            }

            ibEdit.setOnClickListener {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            fbSave.setOnClickListener {
                if (editing){
                    if (ifBlankEditTexts()) else {
                        noteModel.note.observe(viewLifecycleOwner){
                            dbManager.updateInDb(it.id, etTitle.text.toString(), etContent.text.toString(), imageUri)
                            parentFragmentManager.popBackStack()
                        }
                    }
                }else{
                    if (ifBlankEditTexts()) else {
                        dbManager.insertToDb(etTitle.text.toString(), etContent.text.toString(), imageUri)
                        parentFragmentManager.popBackStack()
                    }
                }
            }

            fbDelete.setOnClickListener {
                constraintLayout2.transitionToStart()
                ivImage.setImageResource(R.drawable.gallery)
                imageUri = "empty"
            }

        }
    }

    private fun ifBlankEditTexts(): Boolean = with(binding){
        when{
            (etTitle.text?.isBlank()!! && etContent.text?.isBlank()!!) -> {
                textInputLayoutTitle.error = getString(R.string.no_title)
                textInputLayoutContent.error = getString(R.string.no_content)
                return true
            }
            etTitle.text?.isBlank()!! -> {
                textInputLayoutTitle.error = getString(R.string.no_title)
                textInputLayoutContent.error = null
                return true
            }
            etContent.text?.isBlank()!! -> {
                textInputLayoutTitle.error = null
                textInputLayoutContent.error = getString(R.string.no_content)
                return true
            }
            else -> return false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(dbManager: DbManager, editing: Boolean? = false) = EditFragment().apply {
            val args = Bundle().apply {
                putSerializable("DbManager", dbManager)
                putBoolean("Editing", editing!!)
            }
            arguments = args
        }
    }

}