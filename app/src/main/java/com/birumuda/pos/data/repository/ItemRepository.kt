package com.birumuda.pos.data.repository

import android.content.ContentValues
import com.birumuda.pos.data.db.AppDatabaseHelper
import com.birumuda.pos.data.db.DbContract
import com.birumuda.pos.data.model.Item

class ItemRepository(private val dbHelper: AppDatabaseHelper) {

    fun getAll(): ArrayList<Item> {
        val list = ArrayList<Item>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbContract.ItemTable.TABLE_NAME,
            null, null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            list.add(
                Item(
                    itemId = cursor.getLong(0),
                    nama = cursor.getString(1),
                    deskripsi = cursor.getString(2),
                    categoryId = cursor.getLong(3),
                    harga = cursor.getLong(4),
                    cogs = cursor.getLong(5),
                    picture = cursor.getString(6)
                )
            )
        }
        cursor.close()
        return list
    }

    fun insert(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.ItemTable.COLUMN_NAME, item.nama)
            put(DbContract.ItemTable.COLUMN_DESC, item.deskripsi)
            put(DbContract.ItemTable.COLUMN_CATEGORY_ID, item.categoryId)
            put(DbContract.ItemTable.COLUMN_PRICE, item.harga)
            put(DbContract.ItemTable.COLUMN_COGS, item.cogs)
            put(DbContract.ItemTable.COLUMN_PICTURE, item.picture)
        }
        db.insert(DbContract.ItemTable.TABLE_NAME, null, values)
    }

    fun update(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.ItemTable.COLUMN_NAME, item.nama)
            put(DbContract.ItemTable.COLUMN_CATEGORY_ID, item.categoryId)
            put(DbContract.ItemTable.COLUMN_PRICE, item.harga)
            put(DbContract.ItemTable.COLUMN_COGS, item.cogs)
            put(DbContract.ItemTable.COLUMN_PICTURE, item.picture) // ✅ INI KUNCI
        }
        db.update(
            DbContract.ItemTable.TABLE_NAME,
            values,
            "${DbContract.ItemTable.COLUMN_ID}=?",
            arrayOf(item.itemId.toString())
        )
    }

    fun delete(itemId: Long) {
        val db = dbHelper.writableDatabase
        db.delete(
            DbContract.ItemTable.TABLE_NAME,
            "${DbContract.ItemTable.COLUMN_ID}=?",
            arrayOf(itemId.toString())
        )
    }


    fun search(keyword: String, categoryId: Long?): ArrayList<Item> {
        val list = ArrayList<Item>()
        val db = dbHelper.readableDatabase

        val where = StringBuilder("1=1")
        val args = ArrayList<String>()

        if (keyword.isNotEmpty()) {
            where.append(" AND ${DbContract.ItemTable.COLUMN_NAME} LIKE ?")
            args.add("%$keyword%")
        }

        if (categoryId != null && categoryId != 0L) {
            where.append(" AND ${DbContract.ItemTable.COLUMN_CATEGORY_ID}=?")
            args.add(categoryId.toString())
        }

        val cursor = db.query(
            DbContract.ItemTable.TABLE_NAME,
            null,
            where.toString(),
            args.toTypedArray(),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            list.add(
                Item(
                    itemId = cursor.getLong(0),
                    nama = cursor.getString(1),
                    deskripsi = cursor.getString(2),
                    categoryId = cursor.getLong(3),
                    harga = cursor.getLong(4),
                    cogs = cursor.getLong(5),
                    picture = cursor.getString(6)
                )
            )
        }
        cursor.close()
        return list
    }

    // ================== TAMBAHAN (INI KUNCI) ==================
    fun isCategoryUsed(categoryId: Long): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM ${DbContract.ItemTable.TABLE_NAME} " +
                    "WHERE ${DbContract.ItemTable.COLUMN_CATEGORY_ID}=? LIMIT 1",
            arrayOf(categoryId.toString())
        )

        val used = cursor.moveToFirst()
        cursor.close()
        return used
    }

}
