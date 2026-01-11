package com.birumuda.pos.data.db

object DbContract {

    object CategoryTable {
        const val TABLE_NAME = "category"
        const val COLUMN_ID = "category_id"
        const val COLUMN_NAME = "nama"
        const val COLUMN_DESC = "deskripsi"
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

    object UserTable {
        const val TABLE_NAME = "user"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "fullname"
    }

    object PaymentTable {
        const val TABLE_NAME = "payment"
        const val COLUMN_NAME = "nama"
        const val COLUMN_DESC = "deskripsi"
        const val COLUMN_TYPE = "tipe"
        const val COLUMN_IS_NONACTIVE = "is_nonactive" // 0 = aktif, 1 = nonaktif
    }
}
