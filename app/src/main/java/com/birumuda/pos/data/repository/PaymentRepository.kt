package com.birumuda.pos.data.repository

import android.content.ContentValues
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.db.DbContract
import com.birumuda.pos.data.model.Payment
import com.birumuda.pos.data.model.PaymentType

class PaymentRepository(private val dbHelper: AppDatabaseHelper) {

	fun getAll(): List<Payment> {
		val list = mutableListOf<Payment>()
		val db = dbHelper.readableDatabase

		val cursor = db.query(
			DbContract.PaymentTable.TABLE_NAME,
			null,
			null,
			null,
			null,
			null,
			DbContract.PaymentTable.COLUMN_NAME
		)

		cursor.use {
			while (it.moveToNext()) {
				val name = it.getString(it.getColumnIndexOrThrow(DbContract.PaymentTable.COLUMN_NAME))
				val desc = it.getString(it.getColumnIndexOrThrow(DbContract.PaymentTable.COLUMN_DESC))
				val typeName = it.getString(it.getColumnIndexOrThrow(DbContract.PaymentTable.COLUMN_TYPE))

				list.add(
					Payment(
						name = name,
						description = desc,
						type = PaymentType.fromName(typeName)
					)
				)
			}
		}

		return list
	}

	fun insert(payment: Payment): Boolean {
		val db = dbHelper.writableDatabase

		val values = ContentValues().apply {
			put(DbContract.PaymentTable.COLUMN_NAME, payment.name)
			put(DbContract.PaymentTable.COLUMN_DESC, payment.description)
			put(DbContract.PaymentTable.COLUMN_TYPE, payment.type.name)
		}

		return db.insert(
			DbContract.PaymentTable.TABLE_NAME,
			null,
			values
		) != -1L
	}

	fun update(payment: Payment): Boolean {
		val db = dbHelper.writableDatabase

		val values = ContentValues().apply {
			put(DbContract.PaymentTable.COLUMN_DESC, payment.description)
			put(DbContract.PaymentTable.COLUMN_TYPE, payment.type.name)
		}

		return db.update(
			DbContract.PaymentTable.TABLE_NAME,
			values,
			"${DbContract.PaymentTable.COLUMN_NAME} = ?",
			arrayOf(payment.name)
		) > 0
	}

	fun delete(name: String): Boolean {
		val db = dbHelper.writableDatabase

		return db.delete(
			DbContract.PaymentTable.TABLE_NAME,
			"${DbContract.PaymentTable.COLUMN_NAME} = ?",
			arrayOf(name)
		) > 0
	}

	fun exists(name: String): Boolean {
		val db = dbHelper.readableDatabase

		val cursor = db.query(
			DbContract.PaymentTable.TABLE_NAME,
			arrayOf(DbContract.PaymentTable.COLUMN_NAME),
			"${DbContract.PaymentTable.COLUMN_NAME} = ?",
			arrayOf(name),
			null,
			null,
			null
		)

		cursor.use {
			return it.count > 0
		}
	}
}
