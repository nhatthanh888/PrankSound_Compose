package com.example.pranksound.data.dto.rate

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by TruyenDev on 16/01/2023.
 */
@Parcelize
data class RateModel(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "id_show_rate")
    val id_show_rate: String? = null,
    @Json(name = "status")
    val status: Boolean? = null
) : Parcelable