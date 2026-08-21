package com.example.pranksound.utils

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pranksound_compose.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by TruyenDev on 30/10/2022.
 */


//private suspend fun getVideos(): List<MyVideo> {
//    val videos = mutableListOf<MyVideo>()
//
//    withContext(Dispatchers.IO) {
//        val uriQuery = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else {
//            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//        }
//        val file = File(Environment.DIRECTORY_MOVIES + File.separator + FileUtil.FOLDER_NAME)
//
//        val projection = arrayOf(
//            MediaStore.Video.Media._ID,
//            MediaStore.Video.Media.DISPLAY_NAME,
//            MediaStore.Video.Media.SIZE,
//            MediaStore.Video.Media.DURATION
//        )
//
//        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
//
//        getApplication<Application>().contentResolver.query(
//            uriQuery,
//            projection,
//            MediaStore.Video.Media.DATA + " LIKE ? ",
//            arrayOf("%${file.absolutePath}%"),
//            sortOrder
//        )?.use { cursor ->
//
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
//            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
//            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val uri = ContentUris.withAppendedId(uriQuery, id)
//                val name = cursor.getString(nameColumn)
//                val size = cursor.getLong(sizeColumn)
//                val duration = cursor.getLong(durationColumn)
//
//                videos += Video(id, name, uri, size, duration)
//            }
//
//        }
//    }
//
//    return videos
//}

//fun Context.getVideos(types: List<String>, directoryPath: String? = null): List<MyVideo> {
//    val videoList = mutableListOf<MyVideo>()
//    val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//    val projection = arrayOf(
//        MediaStore.Video.Media.TITLE,
//        MediaStore.Video.Media.DISPLAY_NAME,
//        MediaStore.Video.Media.MIME_TYPE,
//        MediaStore.Video.Media.SIZE,
//        MediaStore.Video.Media.DATE_ADDED,
//        MediaStore.Video.Media.DATE_MODIFIED,
//        MediaStore.Video.Media.DATA,
//        MediaStore.Video.Media.HEIGHT,
//        MediaStore.Video.Media.WIDTH,
//        MediaStore.Video.Media.ALBUM,
//        MediaStore.Video.Media.ARTIST,
//        MediaStore.Video.Media.DURATION,
//        MediaStore.Video.Media.BUCKET_ID,
//        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
//        MediaStore.Video.Media.RESOLUTION
//    )
//
//    val selectionArgs = mutableListOf<String>()
//    val selectionBuilder = StringBuilder()
//
//    // Lọc theo loại file (MP4, MKV,...)
//    selectionBuilder.append("${MediaStore.Video.Media.MIME_TYPE} IN (${types.joinToString { "?" }})")
//    selectionArgs.addAll(types)
//
//    // Nếu có đường dẫn thư mục, lọc theo thư mục đó
//    directoryPath?.let {
//        selectionBuilder.append(" AND ${MediaStore.Video.Media.DATA} LIKE ?")
//        selectionArgs.add("$directoryPath%")
//    }
//
//    val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
//
//    contentResolver.query(uri, projection, selectionBuilder.toString(), selectionArgs.toTypedArray(), sortOrder)?.use { cursor ->
//        val columnIndices = mapOf(
//            "title" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE),
//            "displayName" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME),
//            "mimeType" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE),
//            "size" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE),
//            "dateAdded" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED),
//            "dateModified" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED),
//            "data" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA),
//            "height" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT),
//            "width" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH),
//            "album" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM),
//            "artist" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST),
//            "duration" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION),
//            "bucketID" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID),
//            "bucketDisplayName" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME),
//            "resolution" to cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)
//        )
//
//        while (cursor.moveToNext()) {
//            val myFile = MyFile(
//                title = cursor.getString(columnIndices["title"]!!) ?: "Unknown",
//                displayName = cursor.getString(columnIndices["displayName"]!!) ?: "Unknown",
//                mimeType = cursor.getString(columnIndices["mimeType"]!!) ?: "video/*",
//                size = cursor.getLong(columnIndices["size"]!!),
//                dateAdded = cursor.getLong(columnIndices["dateAdded"]!!),
//                dateModified = cursor.getLong(columnIndices["dateModified"]!!),
//                data = cursor.getString(columnIndices["data"]!!) ?: ""
//            )
//
//            val myVideo = MyVideo(
//                myFile = myFile,
//                height = cursor.getLong(columnIndices["height"]!!),
//                width = cursor.getLong(columnIndices["width"]!!),
//                album = cursor.getString(columnIndices["album"]!!) ?: "Unknown Album",
//                artist = cursor.getString(columnIndices["artist"]!!) ?: "Unknown Artist",
//                duration = cursor.getLong(columnIndices["duration"]!!),
//                bucketID = cursor.getLong(columnIndices["bucketID"]!!),
//                bucketDisplayName = cursor.getString(columnIndices["bucketDisplayName"]!!) ?: "Unknown Folder",
//                resolution = cursor.getString(columnIndices["resolution"]!!) ?: "Unknown Resolution"
//            )
//
//            videoList.add(myVideo)
//        }
//    }
//    return videoList
//}
//
//
//fun Context.getAudios(types: List<String>, isMusic: Boolean): List<MyAudio> {
//    val audioList = mutableListOf<MyAudio>()
//    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//    Log.d("MinhTN912 - LOGIC", "getAudios: $uri")
//    val projection = arrayOf(
//        MediaStore.Audio.Media.TITLE,
//        MediaStore.Audio.Media.DISPLAY_NAME,
//        MediaStore.Audio.Media.MIME_TYPE,
//        MediaStore.Audio.Media.SIZE,
//        MediaStore.Audio.Media.DATE_ADDED,
//        MediaStore.Audio.Media.DATE_MODIFIED,
//        MediaStore.Audio.Media.DATA,
//        MediaStore.Audio.Media.ALBUM,
//        MediaStore.Audio.Media.ARTIST,
//        MediaStore.Audio.Media.DURATION
//    )
//
//    val selection = buildString {
//        append("${MediaStore.Audio.Media.MIME_TYPE} IN (${types.joinToString { "'$it'" }})")
//        if (isMusic) {
//            append(" AND ${MediaStore.Audio.Media.IS_MUSIC} != 0")
//        }
//    }
//    val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"
//
//    contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
//        val columnIndices = mapOf(
//            "title" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE),
//            "displayName" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME),
//            "mimeType" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE),
//            "size" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE),
//            "dateAdded" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED),
//            "dateModified" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED),
//            "data" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA),
//            "album" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM),
//            "artist" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST),
//            "duration" to cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//        )
//
//        while (cursor.moveToNext()) {
//            val myFile = MyFile(
//                title = cursor.getString(columnIndices["title"]!!) ?: "Unknown",
//                displayName = cursor.getString(columnIndices["displayName"]!!) ?: "Unknown",
//                mimeType = cursor.getString(columnIndices["mimeType"]!!) ?: "audio/*",
//                size = cursor.getLong(columnIndices["size"]!!),
//                dateAdded = cursor.getLong(columnIndices["dateAdded"]!!),
//                dateModified = cursor.getLong(columnIndices["dateModified"]!!),
//                data = cursor.getString(columnIndices["data"]!!) ?: ""
//            )
//            Log.d("MinhTN912 - LOGIC", "getAudios: ${myFile.data}")
//
//            val myAudio = MyAudio(
//                myFile = myFile,
//                album = cursor.getString(columnIndices["album"]!!) ?: "Unknown Album",
//                artist = cursor.getString(columnIndices["artist"]!!) ?: "Unknown Artist",
//                duration = cursor.getLong(columnIndices["duration"]!!)
//            )
//
//            audioList.add(myAudio)
//        }
//    }
//    return audioList
//}


//private suspend fun getPhoto(): List<Photo> {
//    val photos = mutableListOf<Photo>()
//    withContext(Dispatchers.IO) {
//        val uriQuery = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        } else {
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        }
//        val file = File(Environment.DIRECTORY_PICTURES + File.separator + FileUtil.FOLDER_NAME)
//        Log.d("TAG", "getPhoto: ${file.absolutePath}")
////            Log.d("TAG", "getPhoto: ${file.absolutePath}")
//        val files = file.listFiles()
//
//        // Count the number of files
//        val fileCount = files?.size ?: 0
//        Log.d("FileCount", "Number of files in  $fileCount")
//
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.SIZE,
//            MediaStore.Images.Media.DATE_TAKEN
//        )
//
//        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
//
//        getApplication<Application>().contentResolver.query(
//            uriQuery,
//            projection,
//            MediaStore.Images.Media.DATA + " LIKE ? ",
//            arrayOf("%${file.absolutePath}%"),
//            sortOrder
//        )?.use { cursor ->
//
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
//            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val uri = ContentUris.withAppendedId(uriQuery, id)
//                val name = cursor.getString(nameColumn)
//                val size = cursor.getLong(sizeColumn)
//                val duration = cursor.getLong(durationColumn)
//
//                photos += Photo(id, name, uri,size)
//            }
//
//        }
//    }
//
//    return photos
//}

//fun Context.getImages(types: List<String>): List<MyImage> {
//    val imageList = mutableListOf<MyImage>()
//    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//    } else {
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//    }
//    val projection = arrayOf(
//        MediaStore.Images.Media.TITLE,
//        MediaStore.Images.Media.DISPLAY_NAME,
//        MediaStore.Images.Media.MIME_TYPE,
//        MediaStore.Images.Media.SIZE,
//        MediaStore.Images.Media.DATE_ADDED,
//        MediaStore.Images.Media.DATE_MODIFIED,
//        MediaStore.Images.Media.DATA,
//        MediaStore.Images.Media.HEIGHT,
//        MediaStore.Images.Media.WIDTH
//    )
//
//    val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (${types.joinToString { "'$it'" }})"
//    val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
//
//    contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
//        val titleIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE)
//        val displayNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
//        val mimeTypeIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
//        val sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
//        val dateAddedIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
//        val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
//        val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//        val heightIndex = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
//        val widthIndex = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
//
//        while (cursor.moveToNext()) {
//            val title = cursor.getString(titleIndex) ?: "Unknown"
//            val displayName = cursor.getString(displayNameIndex) ?: "Unknown"
//            val mimeType = cursor.getString(mimeTypeIndex) ?: "image/*"
//            val size = cursor.getLong(sizeIndex)
//            val dateAdded = cursor.getLong(dateAddedIndex)
//            val dateModified = cursor.getLong(dateModifiedIndex)
//            val data = cursor.getString(dataIndex) ?: ""
//            val height = cursor.getInt(heightIndex)
//            val width = cursor.getInt(widthIndex)
//
//            val myFile = MyFile(title, displayName, mimeType, size, dateAdded, dateModified, data)
//            val myImage = MyImage(myFile, height.toLong(), width.toLong())
//            imageList.add(myImage)
//        }
//    }
//    return imageList
//}

fun <T : Any> mutableLiveDataOf(value: T? = null): MutableLiveData<T> =
    if (value == null) MutableLiveData()
    else MutableLiveData(value)

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

class UtilsKotlin {
    /**
     *
     */
//    open fun doSharpen(original: Bitmap, radius: FloatArray?): Bitmap {
//        val bitmap = Bitmap.createBitmap(
//            original.width, original.height,
//            Bitmap.Config.ARGB_8888)
//        val rs: RenderScript = RenderScript.create(context)
//        val allocIn: Allocation = Allocation.createFromBitmap(rs, original)
//        val allocOut: Allocation = Allocation.createFromBitmap(rs, bitmap)
//        val convolution: ScriptIntrinsicConvolve3x3 = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
//        convolution.setInput(allocIn)
//        convolution.setCoefficients(radius)
//        convolution.forEach(allocOut)
//        allocOut.copyTo(bitmap)
//        rs.destroy()
//        return bitmap
//    }

    /**
     *
     */


    private fun getResizedBitmap(bm: Bitmap, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleHeight = newHeight.toFloat() / height
        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleHeight, scaleHeight)
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    /**
     *
     */
    open fun replaceColor(src: Bitmap, fromColor: Int, targetColor: Int): Bitmap {

        // Source image size
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        for (x in pixels.indices) {
            pixels[x] = if (pixels[x] == fromColor) targetColor else pixels[x]
        }
        // create result bitmap output
        val result = src.config?.let { Bitmap.createBitmap(width, height, it) }
        //set pixels
        result!!.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    fun darkenText(bmp: Bitmap, contrast: Float): Bitmap {
        val cm = ColorMatrix()
        val scale = contrast + 1f
        val translate = (-.5f * scale + .5f) * 255f
        cm.set(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val ret = bmp.config?.let { Bitmap.createBitmap(bmp.width, bmp.height, it) }
        val canvas = ret?.let { Canvas(it) }
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas!!.drawBitmap(bmp, 0f, 0f, paint)
        return ret
    }

    fun saveToInternalStorageCrop(croppedBitmap: Bitmap, context: Context): String? {

        var sharp =
            floatArrayOf(-0.15f, -0.15f, -0.15f, -0.15f, 2.2f, -0.15f, -0.15f, -0.15f, -0.15f)
        //you call the method above and just paste the bitmap you want to apply it and the float of above
//        val bitmapImage = doSharpen(croppedBitmap!! , sharp)
        val bitmapImage = replaceColor(croppedBitmap!!, Color.GRAY, Color.BLACK)
        val bitmapImageNew = darkenText(bitmapImage, 0.8f)
//        val bitmapImage = croppedBitmap ?: return ""
        val cw = ContextWrapper(context)
        val fileNameToSave = "BVDR_" + System.currentTimeMillis()
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "${fileNameToSave}.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImageNew.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mypath.absolutePath
    }


    fun downloadVideo(baseActivity: Context, url: String?, title: String?): Long {
        val direct =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/PrankSound")

        if (!direct.exists()) {
            direct.mkdirs()
        }
        val extension = url?.substring(url.lastIndexOf("."))
        val downloadReference: Long
        var dm: DownloadManager
        dm = baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS + "/PrankSound",
            "PrankSound" + System.currentTimeMillis() + extension
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(title)
        Toast.makeText(baseActivity, "start Downloading..", Toast.LENGTH_SHORT).show()

        downloadReference = dm?.enqueue(request) ?: 0

        return downloadReference

    }

    fun formatTime(millis: Long, context: Context): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            hours == 0L && minutes == 0L -> String.format(
                context.resources.getString(R.string.time_seconds_formatter), seconds
            )

            hours == 0L && minutes > 0L -> String.format(
                context.resources.getString(R.string.time_minutes_seconds_formatter),
                minutes,
                seconds
            )

            else -> context.resources.getString(
                R.string.time_hours_minutes_seconds_formatter,
                hours,
                minutes,
                seconds
            )
        }
    }

    fun getColorByPos(pos: Int): Int {
        return if (pos % 10 == 0) {
            R.color.pos_0
        } else if (pos % 10 == 1) {
            R.color.pos_1
        } else if (pos % 10 == 2) {
            R.color.pos_2
        } else if (pos % 10 == 3) {
            R.color.pos_3
        } else if (pos % 10 == 4) {
            R.color.pos_4
        } else if (pos % 10 == 5) {
            R.color.pos_5
        } else if (pos % 10 == 6) {
            R.color.pos_6
        } else if (pos % 10 == 7) {
            R.color.pos_7
        } else if (pos % 10 == 8) {
            R.color.pos_8
        } else
            R.color.pos_9
    }

    fun getColorTrendingCoverByPos(pos: Int): Int {
        return if (pos % 5 == 0) {
            R.color.pos_cover_trending_0
        } else if (pos % 5 == 1) {
            R.color.pos_cover_trending_1
        } else if (pos % 5 == 2) {
            R.color.pos_cover_trending_2
        } else if (pos % 5 == 3) {
            R.color.pos_cover_trending_3
        } else
            R.color.pos_cover_trending_4
    }

    fun getColorTrendingThumbByPos(pos: Int): Int {
        return if (pos % 5 == 0) {
            R.color.pos_thumb_trending_0
        } else if (pos % 5 == 1) {
            R.color.pos_thumb_trending_1
        } else if (pos % 5 == 2) {
            R.color.pos_thumb_trending_2
        } else if (pos % 5 == 3) {
            R.color.pos_thumb_trending_3
        } else
            R.color.pos_thumb_trending_4
    }


    fun formatAsTime(time: Long): String {
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(time) % 60L).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(time) % 60L).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(time).toInt()
        return if (hours == 0) String.format(
            "%02d:%02d",
            minutes,
            seconds
        ) else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

//    fun showDialogLoading(context: Context): Dialog {
//        val dialog = Dialog(context, R.style.RoundedCornersDialog)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialog_loading)
//        return dialog
//
//
//    }
}