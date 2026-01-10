package com.birumuda.pos.data.db

object DbContract {

    object CategoryTable {
        const val TABLE_NAME = "category"
        const val COLUMN_ID = "category_id"
        const val COLUMN_NAME = "nama"
    }

    object ItemTable {
        const val TABLE_NAME = "item"
        const val COLUMN_ID = "item_id"
        const val COLUMN_NAME = "nama"
        const val COLUMN_DESC = "deskripsi"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_PRICE = "harga"
        const val COLUMN_COGS = "cogs"
        const val COLUMN_PICTURE = "picture"
    }
}
