@file:JvmName("FileUtil")
@file:Suppress("unused")

package xyz.dean.androiddemos.utils

import okio.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.ZipInputStream

private val log: Log by lazy { xyz.dean.androiddemos.common.log }
private const val TAG = "FileUtil"

/**
 * Decompress the zip file to the given directory.
 */
fun File.decompressTo(outPath: File): Boolean {
    try {
        use(
            c1 = source().buffer(),
            c2 = ZipInputStream(FileInputStream(this))
        ) { zipSource, zipStream ->
            while (true) {
                val zipEntry = zipStream.nextEntry
                    ?: break
                val entryName = zipEntry.name
                val file = File(outPath, entryName)
                if (zipEntry.isDirectory && !file.exists()) {
                    file.mkdirs()
                    break
                }
                if (!file.exists()) {
                    file.parentFile?.mkdirs()
                    file.createNewFile()
                }
                file.sink().buffer().use { it.writeAll(zipSource) }
            }
        }
        return true
    } catch (e: IOException) {
        log.e(TAG, "Decompress the zip file failed.", e)
        return false
    }
}

/**
 * Calculate the MD5 for the given file.
 */
fun File.getMD5(): String? {
    if (!isFile) {
        log.e(TAG, "$name is not a file.")
        return null
    }

    try {
        use(
            c1 = HashingSink.md5(blackholeSink()),
            c2 = this.source().buffer()
        ) { hashSink, source ->
            source.readAll(hashSink)
            return hashSink.hash.hex()
        }
    } catch (e: IOException) {
        log.e(TAG, "Can not get file's MD5.", e)
        return null
    }
}