package com.arafat1419.warungku.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class WarungDomain(
    val photoUrl: String? = null,
    val name: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val address: String? = null
) : Parcelable, Serializable
