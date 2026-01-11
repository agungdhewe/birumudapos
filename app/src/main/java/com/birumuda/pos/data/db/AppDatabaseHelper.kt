package com.birumuda.pos.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) :
	SQLiteOpenHelper(context, "birumuda_pos.db", null, 3) {

	override fun onCreate(db: SQLiteDatabase) {
		createCategoryTable(db)
		createItemTable(db)
		createUserTable(db)
		createPaymentTable(db)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		if (oldVersion < 2) {
			// di versi 2, tambahkan tabel user, dan penambahan kolom deskripsi pada cateoru
			createUserTable(db)
			db.execSQL("""
                ALTER TABLE ${DbContract.CategoryTable.TABLE_NAME}
                ADD COLUMN ${DbContract.CategoryTable.COLUMN_DESC} TEXT
            """)
		}

		if (oldVersion < 3) {
			// pada versi tiga terdapat penambahan table payment
			createPaymentTable(db)
		}
	}


	private fun createItemTable(db: SQLiteDatabase) {
		db.execSQL("""
            CREATE TABLE ${DbContract.ItemTable.TABLE_NAME} (
                ${DbContract.ItemTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DbContract.ItemTable.COLUMN_NAME} TEXT,
                ${DbContract.ItemTable.COLUMN_DESC} TEXT,
                ${DbContract.ItemTable.COLUMN_CATEGORY_ID} INTEGER,
                ${DbContract.ItemTable.COLUMN_PRICE} INTEGER,
                ${DbContract.ItemTable.COLUMN_COGS} INTEGER,
                ${DbContract.ItemTable.COLUMN_PICTURE} TEXT
            )
        """)
	}


	private fun createCategoryTable(db: SQLiteDatabase) {
		db.execSQL("""
            CREATE TABLE ${DbContract.CategoryTable.TABLE_NAME} (
                ${DbContract.CategoryTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DbContract.CategoryTable.COLUMN_NAME} TEXT UNIQUE,
                ${DbContract.CategoryTable.COLUMN_DESC} TEXT
            )
        """)
	}


	private fun createUserTable(db: SQLiteDatabase) {
		db.execSQL("""
			CREATE TABLE ${DbContract.UserTable.TABLE_NAME} (
				${DbContract.UserTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
				${DbContract.UserTable.COLUMN_USERNAME} TEXT UNIQUE,
				${DbContract.UserTable.COLUMN_PASSWORD} TEXT,
				${DbContract.UserTable.COLUMN_FULLNAME} TEXT
			)
        """)

	}

	private fun createPaymentTable(db: SQLiteDatabase) {
		db.execSQL("""
			CREATE TABLE ${DbContract.PaymentTable.TABLE_NAME} (
				${DbContract.PaymentTable.COLUMN_NAME} TEXT PRIMARY KEY,
				${DbContract.PaymentTable.COLUMN_DESC} TEXT,
				${DbContract.PaymentTable.COLUMN_TYPE} TEXT,
				${DbContract.PaymentTable.COLUMN_IS_NONACTIVE} INTEGER DEFAULT 0
			)
		""")
	}
}
