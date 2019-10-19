package pl.szkoleniaandroid.billexpert

import android.content.ActivityNotFoundException
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

/**
 * Example of a #INSECURE FileProvider.
 * Please use FileProvider provided by Google:
 * {@linktourl https://developer.android.com/training/secure-file-sharing/setup-sharing }
 */
class CsvFileProvider : ContentProvider() {

    companion object {

        const val MIME_TYPE = "text/plain"

        fun showCsv(context: Context, csvBody: String, filename: String) {

            val fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fos.write(csvBody.toByteArray(Charset.defaultCharset()))
            fos.close()

            try {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(getFileUri(filename), "text/*")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Timber.w(e)
            }
        }

        private fun getFileUri(filename: String): Uri {
            return Uri.parse("content://pl.szkoleniaandroid.billexpert.csv/$filename")
        }
    }


    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(context.filesDir, uri.path)
        if (file.exists()) {
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
        throw FileNotFoundException(uri.path)
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return MIME_TYPE
    }

}
