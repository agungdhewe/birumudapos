package com.birumuda.pos.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payment(
	val name: String,
	val description: String?,
	val type: PaymentType,
	val isNonActive: Boolean = false
) : Parcelable
