package com.example.pranksound.data.dto.rate

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by TruyenDev on 16/01/2023.
 */
@Parcelize
data class RateResponse(
    @Json(name = "status")
    val status: Boolean? = null,
    @Json(name = "rate")
    val rate: MutableList<RateModel> = mutableListOf<RateModel>()
) : Parcelable