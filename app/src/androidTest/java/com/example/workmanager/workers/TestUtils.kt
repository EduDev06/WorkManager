package com.example.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.workmanager.util.OUTPUT_PATH
import java.io.*
import java.util.*

@Throws(Exception::class)
fun copyFileFromTestToTargetCtx(testCtx: Context, targetCtx: Context, fileName: String): Uri {

    val destinationFilename = String.format("image-test-%s.png", UUID.randomUUID().toString())
    val outputDir = File(targetCtx.filesDir, OUTPUT_PATH)

    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }

    val outputFile = File(outputDir, destinationFilename)

    val bis = BufferedInputStream(testCtx.assets.open(fileName))
    val bos = BufferedOutputStream(FileOutputStream(outputFile))
    val buf = ByteArray(1024)
    bis.read(buf)
    do {
        bos.write(buf)
    } while (bis.read(buf) != -1)
    bis.close()
    bos.close()

    return Uri.fromFile(outputFile)
}

fun uriFileExists(targetCtx: Context, uri: String?): Boolean {
    if (uri.isNullOrEmpty()) {
        return false
    }

    val resolver = targetCtx.contentResolver

    // Create a bitmap
    try {
        BitmapFactory.decodeStream(
            resolver.openInputStream(Uri.parse(uri)))
    } catch (e: FileNotFoundException) {
        return false
    }
    return true
}