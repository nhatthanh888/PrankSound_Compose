package com.example.pranksound.data.dto.prank

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SoundFolder(
    val id: Int,
    val name: String,
    val group: String,
    val thumb: String? = null,
):Parcelable
