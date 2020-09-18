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
import com.example.notesharing.R
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.viewmodel.NoteViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_new_note.*
import java.text.DateFormatSymbols
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddNewNoteFragment : Fragment(R.layout.fragment_add_new_note) {

    private val viewModel by viewModels<NoteViewModel>()
    private var noteId: Int = Random().nextInt()

    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveANewNoteFab.setOnClickListener {

            it.hideKeyboard()

            val title = titleForNewNote.text.toString().trim()
            val body = bodyForNewNote.text.toString().trim()

            // GET CURRENT CALENDER
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
            val currentMonth = getMonth(Calendar.getInstance().get(Calendar.MONTH))
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
            val minute = Calendar.getInstance().get(Calendar.MINUTE).toString()
            val second = Calendar.getInstance().get(Calendar.SECOND).toString()
            val date = "$currentDay $currentMonth $currentYear"
            val time = "$hour:$minute:$second"
            Log.d("AddNewNoteFragment", date)
            Log.d("AddNewNoteFragment", time)

            when {
                title.isEmpty() -> {
                    titleForNewNote.error = "Title required"
                    titleForNewNote.requestFocus()
                    return@setOnClickListener
                }
                body.isEmpty() -> {
                    bodyForNewNote.error = "Note required"
                    bodyForNewNote.requestFocus()
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
                            Log.d("AddNewNoteFragment", colorHex)

                            Log.d("Thread", "onViewCreated: " + Thread.currentThread().id)
                            viewModel.saveANote(
                                OneNoteModel(
                                    noteId,
                                    title,
                                    body,
                                    colorHex,
                                    date,
                                    time = time,
                                    mAuth.currentUser?.email.toString(),
                                    writable = true,
                                    edited = false
                                ),
                                requireContext(),
                                noteId
                            )
                            findNavController().navigate(R.id.action_addNewNoteFragment_to_myAllNotesFragment)
                        }
                        .show()
                }
            }
        }
    }

    private fun getMonth(month: Int): String? {
        return DateFormatSymbols().shortMonths[month]
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}