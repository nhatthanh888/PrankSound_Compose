package com.example.pranksound.data.dto.prank

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrankCall(
    val audioLink: String? = null,
    val avatar: String? = null,
    val id: String="",
    val index: Int=0,
    val phoneNumber: String? = null,
    val thumb: String = "",
    val title: String="",
    val videoLink: String = ""
): Parcelable
