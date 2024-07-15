package com.dev.cuckooxa.notesapp.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import com.dev.cuckooxa.notesapp.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun ImageView.fromUri(note: Note) : Bitmap = withContext(Dispatchers.IO) {
    return@withContext context.contentResolver.openInputStream(Uri.parse(note.imageUri)).use {
        BitmapFactory.decodeStream(it)
    }
}

suspend fun ImageView.compressNoteImage(note: Note) : Bitmap = withContext(Dispatchers.IO) {
    val inputStream = context.contentResolver.openInputStream(Uri.parse(note.imageUri))
    val originalImage = BitmapFactory.decodeStream(inputStream)
    inputStream!!.close()
    val bos = ByteArrayOutputStream()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        originalImage.compress(Bitmap.CompressFormat.WEBP_LOSSY, 0, bos)
    } else {
        originalImage.compress(Bitmap.CompressFormat.WEBP, 0, bos)
    }
    val byteArray = bos.toByteArray()
    return@withContext BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}