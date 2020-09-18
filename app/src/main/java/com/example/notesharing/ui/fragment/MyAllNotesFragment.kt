package com.example.notesharing.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.notesharing.R
import com.example.notesharing.adapter.MyAllNotesAdapter
import com.example.notesharing.ui.activity.SignInActivity
import com.example.notesharing.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.bottomsheet_layout.view.*
import kotlinx.android.synthetic.main.fragment_my_all_notes.*
import java.text.DateFormatSymbols
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyAllNotesFragment : Fragment(R.layout.fragment_my_all_notes) {

    private val viewModel by viewModels<NoteViewModel>()

    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Call the function for get data:
        viewModel.getAllPersonalNotes(requireContext())

        val myAllNoteAdapter: MyAllNotesAdapter =
            MyAllNotesAdapter(MyAllNotesFragmentDirections, this.findNavController())

        myAllNotesRecyclerView.adapter = myAllNoteAdapter
        myAllNotesRecyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = 20
            }
        })
        viewModel.allPersonalNotes.observe(viewLifecycleOwner, Observer {
            Log.d("MyAllNotesFragment", "Observer  -> $it ")
            myAllNoteAdapter.setData(it)
        })


        // SWIPE LOGIC IMPLEMENT
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentPersonalNote = myAllNoteAdapter.allPersonalNotesList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        Log.d(
                            "MyAllNotesFragment",
                            "onSwiped left $position: -> ${currentPersonalNote.body}"
                        )
                        viewModel.deleteANote(
                            requireContext(),
                            currentPersonalNote.id?.toInt()
                        )
                        myAllNoteAdapter.notifyDataSetChanged()
                    }

                    ItemTouchHelper.RIGHT -> {
                        val newNoteId: Int = Random().nextInt()

                        Log.d(
                            "MyAllNotesFragment",
                            "onSwiped right $position: -> ${currentPersonalNote.body}"
                        )
                        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_layout, null)
                        val customDialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setTitle("Enter email address")
                            .setCancelable(false)
                            .show()

                        dialogView.friendsEmail.isFocusable = true
                        dialogView.cancel_alert_dialog.setOnClickListener {
                            customDialog.dismiss()
                            myAllNoteAdapter.notifyDataSetChanged()
                        }

                        dialogView.send_alert_dialog.setOnClickListener {

                            it.hideKeyboard()
                            Log.d("TAG", "switch > ${dialogView.is_writeable_switch.isChecked}")
                            val isWritable = !(dialogView.is_writeable_switch.isChecked)
                            Log.d("TAG", "isWritable > $isWritable")


                            val currentDay =
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
                            val currentMonth = getMonth(Calendar.getInstance().get(Calendar.MONTH))
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
                            val minute = Calendar.getInstance().get(Calendar.MINUTE).toString()
                            val second = Calendar.getInstance().get(Calendar.SECOND).toString()
                            val date = "$currentDay $currentMonth $currentYear"
                            val time = "$hour:$minute:$second"


                            Log.d("TAG", "before newNote >>>  $currentPersonalNote")

                            Log.d("TAG", "newNoteId:  $newNoteId")
                            currentPersonalNote.id = newNoteId
                            currentPersonalNote.date = date
                            currentPersonalNote.time = time
                            currentPersonalNote.writable = isWritable
                            Log.d("TAG", "newNote >>>  $currentPersonalNote")

                            viewModel.checkIsUserExist(
                                requireContext(),
                                dialogView.friendsEmail.text.toString(),
                                currentPersonalNote,
                                dialogView.friendsEmail,
                                customDialog
                            )
                            myAllNoteAdapter.notifyDataSetChanged()
                        }
                    }

                    else -> {
                        return
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_400
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue_400
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_share_24)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(myAllNotesRecyclerView)
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.receive -> {
                    // Handle favorite icon press
//                    Toast.makeText(requireContext(), "receive", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_myAllNotesFragment_to_allReceivedFragment)
                    true
                }
                R.id.sign_out -> {
//                    Toast.makeText(requireContext(), "sign out", Toast.LENGTH_SHORT).show()
                    // Handle search icon press
                    mAuth.signOut()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        addANewNoteFab.setOnClickListener {
            findNavController().navigate(R.id.action_myAllNotesFragment_to_addNewNoteFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getMonth(month: Int): String? {
        return DateFormatSymbols().shortMonths[month]
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}

