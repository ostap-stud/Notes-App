package com.dev.cuckooxa.notesapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.dev.cuckooxa.notesapp.model.Note
import java.io.Serializable


class DbManager(context: Context) : Serializable {
    private val dbHelper = DbHelper(context)
    private var database: SQLiteDatabase? = null

    fun openDb(){
        database = dbHelper.writableDatabase
    }

    fun insertToDb(title: String, content: String, image: String){
        val values = ContentValues().apply {
            put(DbContract.MyTable.COLUMN_NAME_TITLE, title)
            put(DbContract.MyTable.COLUMN_NAME_CONTENT, content)
            put(DbContract.MyTable.COLUMN_NAME_IMAGE_URI, image)
        }

        database?.insert(DbContract.MyTable.TABLE_NAME, null, values)
    }

    fun deleteFromDb(id: Int){
        database?.delete(DbContract.MyTable.TABLE_NAME, "${BaseColumns._ID}=$id", null)
    }

    fun updateInDb(id: Int, title: String, content: String, image: String){
        val values = ContentValues().apply {
            put(BaseColumns._ID, id)
            put(DbContract.MyTable.COLUMN_NAME_TITLE, title)
            put(DbContract.MyTable.COLUMN_NAME_CONTENT, content)
            put(DbContract.MyTable.COLUMN_NAME_IMAGE_URI, image)
        }
        database?.update(DbContract.MyTable.TABLE_NAME, values, "${BaseColumns._ID}=$id", null)
    }

    @SuppressLint("Range")
    fun readFromDb(searchText: String): ArrayList<Note>{
        val list = ArrayList<Note>()

        database = dbHelper.readableDatabase
        val cursor = database?.query(
            DbContract.MyTable.TABLE_NAME,
            null,
            "${DbContract.MyTable.COLUMN_NAME_TITLE} LIKE ?",
            arrayOf("%$searchText%"),
            null,
            null,
            null)

        while (cursor?.moveToNext()!!){
            val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val title = cursor.getString(cursor.getColumnIndex(DbContract.MyTable.COLUMN_NAME_TITLE))
            val content = cursor.getString(cursor.getColumnIndex(DbContract.MyTable.COLUMN_NAME_CONTENT))
            val imageUri = cursor.getString(cursor.getColumnIndex(DbContract.MyTable.COLUMN_NAME_IMAGE_URI))
            list.add(Note(id, title, content, imageUri))
        }
        cursor.close()

        return list
    }

    fun closeDb(){
        database?.close()
    }

}