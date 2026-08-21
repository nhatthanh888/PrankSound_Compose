package com.example.pranksound.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val id: Long = 0L,
    val name: String? = "Unknown",
    var uri: Uri? = Uri.EMPTY,
    val size: Long = 0L,
) : Parcelable