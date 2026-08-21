package com.example.pranksound.data.dto.config

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class CMPModel(
    @Json(name = "status") val status: Boolean = false,

    @Json(name = "status_dialog") val status_dialog: Boolean = false
) : Parcelable













