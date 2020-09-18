package com.example.notesharing.module

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.notesharing.adapter.MyAllNotesAdapter
import com.example.notesharing.model.OneNoteModel
import com.example.notesharing.ui.fragment.MyAllNotesFragment
import com.example.notesharing.ui.fragment.MyAllNotesFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent


@InstallIn(ApplicationComponent::class)
@Module
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFireStoreInstant(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

//    @Provides
//    fun provideMyAllNoteAdapter(): MyAllNotesAdapter {
//        return MyAllNotesAdapter()
//    }

//    @Provides
//    fun provideMyAllNoteFragment(): MyAllNotesFragment {
//        return MyAllNotesFragment()
//    }
//
//    @Provides
//    fun provideMyAllNoteFragmentDirection(): MyAllNotesFragmentDirections.Companion {
//        return MyAllNotesFragmentDirections
//    }
//
//    @Provides
//    fun provideMyAllNotesFragmentFindNavController(myAllNotesFragment: MyAllNotesFragment): NavController {
//        return myAllNotesFragment.findNavController()
//    }
}