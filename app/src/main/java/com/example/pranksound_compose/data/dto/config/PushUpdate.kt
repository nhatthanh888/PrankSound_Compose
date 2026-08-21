package com.example.pranksound.data.dto.config

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushUpdate(
    @SerializedName("message")
    var message: List<String> = listOf(),
    @SerializedName("new_package")
    var newPackage: String = "",
    @SerializedName("required")
    var required: Boolean = false,
    @SerializedName("Status")
    var status: Boolean = false,
    @SerializedName("title")
    var title: String = "",
    @SerializedName("version_code")
    var versionCode: Int = 0,
    @SerializedName("version_code_required")
    var versionCodeRequired: List<Int> = listOf(),
    @SerializedName("version_name")
    var versionName: String = ""
) : Parcelable