package com.dev.cuckooxa.notesapp.db

import android.provider.BaseColumns

object DbContract {

    const val DATABASE_VERSION = 2
    const val DATABASE_NAME = "Notes.db"

    object MyTable : BaseColumns{
        const val TABLE_NAME = "my_table"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_IMAGE_URI = "image_uri"
    }

    object SqlQueries {
        const val SQL_CREATE_MY_TABLE =
            "CREATE TABLE IF NOT EXISTS ${MyTable.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${MyTable.COLUMN_NAME_TITLE} TEXT," +
                    "${MyTable.COLUMN_NAME_CONTENT} TEXT," +
                    "${MyTable.COLUMN_NAME_IMAGE_URI} TEXT)"

        const val SQL_DROP_MY_TABLE = "DROP TABLE IF EXISTS ${MyTable.TABLE_NAME}"
    }
}