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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_and_edit_personal_notes.*
import java.text.DateFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ViewAndEditPersonalNotesFragment : Fragment(R.layout.fragment_view_and_edit_personal_notes) {

    private val args: ViewAndEditPersonalNotesFragmentArgs by navArgs()
    private val viewModel by viewModels<NoteViewModel>()

    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAlreadySavedDetails()

        editImgViewEditNote.setOnClickListener {
            cancelImgViewEditNote.visibility = View.VISIBLE
            editImgViewEditNote.visibility = View.GONE
            saveAViewEditNoteFab.visibility = View.VISIBLE
            titleForViewEditNote.isEnabled = true
            bodyForViewEditNote.isEnabled = true
        }

        cancelImgViewEditNote.setOnClickListener {
            editImgViewEditNote.visibility = View.VISIBLE
            cancelImgViewEditNote.visibility = View.GONE
            saveAViewEditNoteFab.visibility = View.GONE
            titleForViewEditNote.isEnabled = false
            bodyForViewEditNote.isEnabled = false
            showAlreadySavedDetails()
        }

        saveAViewEditNoteFab.setOnClickListener {

            it.hideKeyboard()

            val title = titleForViewEditNote.text.toString().trim()
            val body = bodyForViewEditNote.text.toString().trim()

            // GET CURRENT CALENDER
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
            val currentMonth = getMonth(Calendar.getInstance().get(Calendar.MONTH))
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
            val minute = Calendar.getInstance().get(Calendar.MINUTE).toString()
            val second = Calendar.getInstance().get(Calendar.SECOND).toString()
            val date = "$currentDay $currentMonth $currentYear"
            val time = "$hour:$minute:$second"
            Log.d("ViewAndEditPersonal", date)
            Log.d("ViewAndEditPersonal", time)

            when {
                title.isEmpty() -> {
                    titleForViewEditNote.error = "Title required"
                    titleForViewEditNote.requestFocus()
                    return@setOnClickListener
                }
                body.isEmpty() -> {
                    bodyForViewEditNote.error = "Note required"
                    bodyForViewEditNote.requestFocus()
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
                            Log.d("ViewAndEditPersonal", colorHex)

                            Log.d("Thread", "onViewCreated: " + Thread.currentThread().id)
                            viewModel.saveANote(
                                OneNoteModel(
                                    args.selectedPersonalNote.id,
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
                                args.selectedPersonalNote.id?.toInt()
                            )
                            findNavController().navigate(R.id.action_viewAndEditPersonalNotesFragment_to_myAllNotesFragment)
                        }
                        .show()
                }
            }
        }
    }

    private fun showAlreadySavedDetails() {
        titleForViewEditNote.setText(args.selectedPersonalNote.title)
        bodyForViewEditNote.setText(args.selectedPersonalNote.body)
    }

    private fun getMonth(month: Int): String? {
        return DateFormatSymbols().shortMonths[month]
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}