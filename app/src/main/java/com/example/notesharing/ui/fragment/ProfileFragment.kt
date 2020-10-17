package com.example.notesharing.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.notesharing.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = mAuth.currentUser

        Glide.with(requireContext())
            .load(user?.photoUrl)
            .placeholder(profile_image.drawable)
            .into(profile_image)

        profile_username.text = user?.displayName

        profile_email.text = user?.email
    }
}