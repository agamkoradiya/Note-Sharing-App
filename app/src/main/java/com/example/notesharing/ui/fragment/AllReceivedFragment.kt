package com.example.notesharing.ui.fragment

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notesharing.R
import com.example.notesharing.adapter.MyReceivedNotesAdapter
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.viewmodel.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_all_received.*

@AndroidEntryPoint
class AllReceivedFragment : Fragment(R.layout.fragment_all_received) {

    private val viewModel by viewModels<NoteViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllReceivedNotes(requireContext())

        val myReceivedNotesAdapter = MyReceivedNotesAdapter(
            AllReceivedFragmentDirections,
            this.findNavController()
        )

        allReceivedNotesRecyclerView.adapter = myReceivedNotesAdapter
        allReceivedNotesRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = 20
            }
        })
        viewModel.allReceivedNotes.observe(viewLifecycleOwner, Observer {
            myReceivedNotesAdapter.setData(it)
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
                val currentReceivedNote = myReceivedNotesAdapter.allReceivedNotesList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        confirmDelete(currentReceivedNote)
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
            attachToRecyclerView(allReceivedNotesRecyclerView)
        }
    }

    private fun confirmDelete(currentReceivedNote: OneNoteModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this received note ?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteAReceivedNote(requireContext(), currentReceivedNote.id, dialog)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.show()
    }
}