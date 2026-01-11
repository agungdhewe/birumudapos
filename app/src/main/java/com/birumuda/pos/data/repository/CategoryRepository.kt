package com.birumuda.pos.data.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.db.DbContract
import com.birumuda.pos.data.model.Category

class CategoryRepository(private val dbHelper: AppDatabaseHelper) {

    /**
     * Digunakan oleh ItemActivity (JANGAN DIUBAH)
     */
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

    /**
     * Digunakan oleh ItemActivity (JANGAN DIUBAH)
     */
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
            null,
            null,
            null,
            null,
            null,
            DbContract.CategoryTable.COLUMN_NAME
        )

        while (cursor.moveToNext()) {
            list.add(
                Category(
                    categoryId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DbContract.CategoryTable.COLUMN_ID)
                    ),
                    nama = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbContract.CategoryTable.COLUMN_NAME)
                    ),
                    deskripsi = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbContract.CategoryTable.COLUMN_DESC)
                    )
                )
            )
        }
        cursor.close()
        return list
    }

    fun insert(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.CategoryTable.COLUMN_NAME, category.nama)
            put(DbContract.CategoryTable.COLUMN_DESC, category.deskripsi)
        }
        return db.insert(DbContract.CategoryTable.TABLE_NAME, null, values)
    }

    // ================== FIX UTAMA ADA DI SINI ==================
    fun update(category: Category): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.CategoryTable.COLUMN_NAME, category.nama)
            put(DbContract.CategoryTable.COLUMN_DESC, category.deskripsi)
        }
        return db.update(
            DbContract.CategoryTable.TABLE_NAME,
            values,
            "${DbContract.CategoryTable.COLUMN_ID}=?",
            arrayOf(category.categoryId.toString())
        )
    }

    fun delete(categoryId: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DbContract.CategoryTable.TABLE_NAME,
            "${DbContract.CategoryTable.COLUMN_ID}=?",
            arrayOf(categoryId.toString())
        )
    }
}
