package com.example.pranksound.data.dto.response

import com.example.pranksound.data.dto.prank.PrankCall

data class PrankCallResponse(
    val code: Int,
    val `data`: List<PrankCall>
) {

}