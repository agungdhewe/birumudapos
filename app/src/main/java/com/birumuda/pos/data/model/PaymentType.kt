package com.birumuda.pos.data.model

enum class PaymentType(
	val displayName: String,
	val isElectronic: Boolean,
	val requiresReference: Boolean
) {
	CASH(
		displayName = "Cash",
		isElectronic = false,
		requiresReference = false
	),
	QRIS(
		displayName = "QRIS",
		isElectronic = true,
		requiresReference = true
	),
	EDC(
		displayName = "EDC",
		isElectronic = true,
		requiresReference = true
	),
	COUPON(
		displayName = "Coupon",
		isElectronic = false,
		requiresReference = false
	);

	companion object {
		fun fromName(name: String): PaymentType {
			return entries.first { it.name == name }
		}
	}
}
