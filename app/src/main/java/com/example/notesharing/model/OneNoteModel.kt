package com.example.notesharing.model

import java.io.Serializable

data class OneNoteModel(
    var id: Int? = null,
    var title: String = "",
    var body: String = "",
    var color: String = "",
    var date: String = "",
    var time: String = "",
    var createdBy: String = "",
    var writable: Boolean? = null,
    var edited: Boolean? = null
) : Serializable