package com.birumuda.pos.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.db.DbContract
import com.birumuda.pos.data.model.Category

class CategoryRepository(private val dbHelper: AppDatabaseHelper) {

    fun insertIfNotExists(nama: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.CategoryTable.COLUMN_NAME, nama)
        }
        return db.insertWithOnConflict(
            DbContract.CategoryTable.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun getCategoryIdByName(nama: String): Long {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbContract.CategoryTable.TABLE_NAME,
            arrayOf(DbContract.CategoryTable.COLUMN_ID),
            "${DbContract.CategoryTable.COLUMN_NAME}=?",
            arrayOf(nama),
            null, null, null
        )

        val id = if (cursor.moveToFirst()) cursor.getLong(0) else -1L
        cursor.close()
        return id
    }

    fun getAll(): List<Category> {
        val list = ArrayList<Category>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbContract.CategoryTable.TABLE_NAME,
            null, null, null, null, null,
            DbContract.CategoryTable.COLUMN_NAME
        )

        while (cursor.moveToNext()) {
            list.add(
                Category(
                    categoryId = cursor.getLong(0),
                    nama = cursor.getString(1)
                )
            )
        }
        cursor.close()
        return list
    }
}
