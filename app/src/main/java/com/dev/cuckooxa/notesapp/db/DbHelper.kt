package com.dev.cuckooxa.notesapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(DbContract.SqlQueries.SQL_CREATE_MY_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DbContract.SqlQueries.SQL_DROP_MY_TABLE)
        onCreate(p0)
    }
}