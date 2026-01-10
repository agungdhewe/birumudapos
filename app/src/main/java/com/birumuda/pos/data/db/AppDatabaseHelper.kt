package com.birumuda.pos.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "birumuda_pos.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE ${DbContract.CategoryTable.TABLE_NAME} (
                ${DbContract.CategoryTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DbContract.CategoryTable.COLUMN_NAME} TEXT UNIQUE
            )
        """)

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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DbContract.ItemTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DbContract.CategoryTable.TABLE_NAME}")
        onCreate(db)
    }
}
