package com.birumuda.pos.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val itemId: Long = 0L,
    val nama: String,
    val deskripsi: String? = null,
    val categoryId: Long,
    val harga: Long,
    val cogs: Long,
    val picture: String? = null
) : Parcelable