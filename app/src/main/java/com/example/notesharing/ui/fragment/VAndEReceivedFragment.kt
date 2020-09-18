package com.example.notesharing.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesharing.R
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.viewmodel.NoteViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_v_and_e_received.*

@AndroidEntryPoint
class VAndEReceivedFragment : Fragment(R.layout.fragment_v_and_e_received) {

    private val args: VAndEReceivedFragmentArgs by navArgs()
    private val viewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("VAndEReceivedFragment", "onViewCreated: ${args.selectedReceivedNote.toString()}")
        showAlreadyReceivedDetails()


        editImgVENote.setOnClickListener {
            cancelImgVENote.visibility = View.VISIBLE
            editImgVENote.visibility = View.GONE
            saveAVEFab.visibility = View.VISIBLE
            titleForVENote.isEnabled = true
            bodyForVENote.isEnabled = true
        }

        cancelImgVENote.setOnClickListener {
            editImgVENote.visibility = View.VISIBLE
            cancelImgVENote.visibility = View.GONE
            saveAVEFab.visibility = View.GONE
            titleForVENote.isEnabled = false
            bodyForVENote.isEnabled = false
            showAlreadyReceivedDetails()
        }

        saveAVEFab.setOnClickListener {
            it.hideKeyboard()

            val title = titleForVENote.text.toString().trim()
            val body = bodyForVENote.text.toString().trim()

            when {
                title.isEmpty() -> {
                    titleForVENote.error = "Title required"
                    titleForVENote.requestFocus()
                    return@setOnClickListener
                }
                body.isEmpty() -> {
                    bodyForVENote.error = "Note required"
                    bodyForVENote.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    ColorPickerDialog
                        .Builder(requireContext())                        // Pass Activity Instance
                        .setColorShape(ColorShape.CIRCLE)    // Default ColorShape.CIRCLE
                        .setTitle("Select your favourite color")
                        .setDefaultColor(Color.YELLOW) // Pass Default Color
                        .setColorListener { _, colorHex ->
                            // Handle Color Selection

                            Log.d("Thread", "onViewCreated: " + Thread.currentThread().id)
                            val currentReceivedNote = args.selectedReceivedNote
                            currentReceivedNote.title = title
                            currentReceivedNote.body = body
                            currentReceivedNote.edited = true
                            currentReceivedNote.color = colorHex
                            viewModel.saveReceivedNote(currentReceivedNote, requireContext())
                            findNavController().navigate(R.id.action_VAndEReceivedFragment_to_allReceivedFragment)
                        }
                        .show()
                }
            }


        }
    }

    private fun showAlreadyReceivedDetails() {
        titleForVENote.setText(args.selectedReceivedNote.title)
        bodyForVENote.setText(args.selectedReceivedNote.body)
        val writable = args.selectedReceivedNote.writable
        if (writable == true) {
            editImgVENote.visibility = View.VISIBLE
        } else {
            editImgVENote.visibility = View.GONE
            cancelImgVENote.visibility = View.GONE
        }
    }
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}