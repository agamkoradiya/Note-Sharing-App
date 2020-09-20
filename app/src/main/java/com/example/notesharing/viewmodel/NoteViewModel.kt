package com.example.notesharing.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesharing.model.AllUsers
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.utils.Constants.ALL_NOTES
import com.example.notesharing.utils.Constants.PERSONAL_NOTE
import com.example.notesharing.utils.Constants.RECEIVED_NOTE
import com.example.notesharing.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel @ViewModelInject constructor(
    private val db: FirebaseFirestore,
    private val mAuth: FirebaseAuth
) : ViewModel() {

    val _allPersonalNotes = MutableLiveData<List<OneNoteModel>>()
    val allPersonalNotes: LiveData<List<OneNoteModel>>
        get() = _allPersonalNotes

    val _allReceivedNotes = MutableLiveData<List<OneNoteModel>>()
    val allReceivedNotes: LiveData<List<OneNoteModel>>
        get() = _allReceivedNotes


    fun saveANote(oneNoteModel: OneNoteModel, context: Context, noteId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Thread", "viewmodel: " + Thread.currentThread().id)

            db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
                PERSONAL_NOTE
            ).document("N-$noteId").set(oneNoteModel)
                .addOnSuccessListener {
                    context.toast("Note Saved")
                }.addOnFailureListener {
                    context.toast("Something went wrong")
                }
        }
    }

    fun deleteANote(context: Context, noteId: Int?, dialog: DialogInterface) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Thread", "viewmodel: " + Thread.currentThread().id)

            db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
                PERSONAL_NOTE
            ).document("N-$noteId").delete()
                .addOnSuccessListener {
                    context.toast("Note Deleted")
                    dialog.cancel()
                }.addOnFailureListener {
                    context.toast("Try Again")
                    dialog.cancel()
                }
        }
    }


    fun getAllPersonalNotes(context: Context) {
        db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
            PERSONAL_NOTE
        ).addSnapshotListener { value, error ->
            error?.let {
                context.toast("Try Again")
            }
            value?.let {
                val notes = mutableListOf<OneNoteModel>()
                for (document in it.iterator()) {
                    val note = document.toObject(OneNoteModel::class.java)
                    Log.d("MyAllNotesFragment", "Note   -> $note ")
                    notes.add(note)
                }
                _allPersonalNotes.value = notes
            }
        }
    }


    fun getAllReceivedNotes(context: Context) {
        db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
            RECEIVED_NOTE
        ).addSnapshotListener { value, error ->
            error?.let {
                context.toast("Try Again")
            }
            value?.let {
                val notes = mutableListOf<OneNoteModel>()
                for (document in it.iterator()) {
                    val note = document.toObject(OneNoteModel::class.java)
                    Log.d("MyAllNotesFragment", "Received Notes   -> $note ")
                    notes.add(note)
                }
                _allReceivedNotes.value = notes
            }
        }
    }


    fun checkIsUserExist(
        context: Context,
        receiversMail: String,
        noteModel: OneNoteModel,
        editText: EditText,
        alertDialog: AlertDialog
    ) {
        db.collection("allUsers").whereEqualTo(FieldPath.documentId(), receiversMail)
            .get().addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    context.toast("Email doesn't exist!!!")
                    editText.error = "User doesn't exist"
                } else {
                    context.toast("Sending...")
                    val receiversMailId = it.toObjects(AllUsers::class.java)
                    Log.d("checkIsUserExist", ">  ${receiversMailId[0].uid}")
                    val receiversUid = receiversMailId[0].uid

                    // Save Process Started
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("Thread", "viewmodel: " + Thread.currentThread().id)
                        db.collection(ALL_NOTES).document(receiversUid).collection(
                            RECEIVED_NOTE
                        ).document("N-${noteModel.id}").set(noteModel)
                            .addOnSuccessListener {
                                context.toast("Sent")
                                alertDialog.dismiss()
                            }.addOnFailureListener {
                                context.toast("Try Again...")
                                alertDialog.dismiss()
                            }
                    }

                }
            }
//            .addSnapshotListener { value, error ->
//                Log.d("checkIsUserExist", value.toString())
//                error?.let {
//                    context.toast("UserName Not Found")
//                    Log.d("checkIsUserExist", "checkIsUserExist: $error")
//                }
//
//                value?.let {
//                    for (document in it.iterator()) {
//                        val user = document.toObject(AllUsers::class.java)
//                        Log.d("checkIsUserExist", "userList   -> $user ")
//                    }
//                }
//            }
    }


    fun saveReceivedNote(oneNoteModel: OneNoteModel, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Thread", "viewmodel: " + Thread.currentThread().id)

            db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
                RECEIVED_NOTE
            ).document("N-${oneNoteModel.id}").set(oneNoteModel)
                .addOnSuccessListener {
                    context.toast("Note Saved")
                }.addOnFailureListener {
                    context.toast("Something went wrong")
                }
        }
    }

    fun deleteAReceivedNote(context: Context, noteId: Int?, dialog: DialogInterface) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Thread", "viewmodel: " + Thread.currentThread().id)

            db.collection(ALL_NOTES).document(mAuth.currentUser?.uid.toString()).collection(
                RECEIVED_NOTE
            ).document("N-$noteId").delete()
                .addOnSuccessListener {
                    context.toast("Note Deleted")
                    dialog.cancel()
                }.addOnFailureListener {
                    context.toast("Try Again")
                    dialog.cancel()
                }
        }
    }
}