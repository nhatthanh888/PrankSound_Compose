package com.example.pranksound_compose.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.example.pranksound_compose.App
import com.example.pranksound_compose.BuildConfig
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {

    fun Context.registerReceiverNotExported(
        receiver: BroadcastReceiver?, intentFilter: IntentFilter
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, intentFilter)
        }
    }

    fun saveCache(context: Context, data: String?, fileName: String?) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.message)
        }
    }

    fun linkToStore(context: Context) {
        val appPackageName: String =
            App.instance?.packageName!!
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }


    fun readFromFile(context: Context, fileName: String?): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput(fileName)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }

    fun formatTime(duration: Long): String {
        val formatter =
            SimpleDateFormat("hh:mm a", Locale.getDefault()) // Định dạng 12 giờ với AM/PM
        val date = Date(duration)
//        formatter.timeZone = TimeZone.getTimeZone("UTC") // Giữ nguyên mốc thời gian theo UTC
        return formatter.format(date).toUpperCase()
    }

    fun formatFileSize(fileSize: Long): String {
        val df = DecimalFormat("0.00")
        val fileSizeString: String = if (fileSize <= 0) {
            "0KB"
        } else if (fileSize < 1024 * 1024) {
            df.format(fileSize.toDouble() / 1024) + "KB"
        } else if (fileSize < 1024 * 1024 * 1024) {
            df.format(fileSize.toDouble() / (1024 * 1024)) + "MB"
        } else {
            df.format(fileSize.toDouble() / (1024 * 1024 * 1024)) + "GB"
        }
        return fileSizeString
    }

    fun shareFile(context: Context, file: File?, fileName: String?, pkg: String? = null) {
        try {
            val uri = FileProvider.getUriForFile(
                context, BuildConfig.APPLICATION_ID + ".provider", file!!
            )
            shareFile(context, uri, fileName, pkg)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun shareFile(context: Context, uri: Uri?, fileName: String?, pkg: String?) {
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
            emailIntent.`package` = pkg
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.setDataAndType(
                uri, context.contentResolver.getType(
                    uri!!
                )
            )
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(emailIntent)
        } catch (ex: Exception) {
            try {
                val intent: Intent = ShareCompat.IntentBuilder.from(context as Activity).setType(
                    context.contentResolver.getType(
                        uri!!
                    )
                ).setStream(uri).intent
                intent.putExtra(Intent.EXTRA_EMAIL, "")
                intent.putExtra(Intent.EXTRA_SUBJECT, fileName)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setPackage(pkg)
                val createChooser: Intent = Intent.createChooser(intent, "Share File")
                createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (createChooser.resolveActivity(context.packageManager) == null) {
                    return
                }
                context.startActivity(createChooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun readFileFromAssets(context: Context, fileName: String): String {
        val rs = StringBuilder()
        try {
            BufferedReader(InputStreamReader(context.assets.open(fileName))).use { reader ->
                var mLine: String?
                while (reader.readLine().also { mLine = it } != null) {
                    //process line
                    rs.append(mLine)
                }
            }
        } catch (e: IOException) {
            //log the exception
        }
        //log the exception
        return rs.toString()
    }

    fun readFileFromStorage(file: File): String {
        val rs = StringBuilder()
        try {
            BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                var mLine: String?
                while (reader.readLine().also { mLine = it } != null) {
                    //process line
                    rs.append(mLine)
                }
            }
        } catch (e: IOException) {
            //log the exception
        }
        //log the exception
        return rs.toString()
    }


    fun writeStringToStorage(outFile: File, sBody: String?) {
        try {
            val writer = FileWriter(outFile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun shareApp(context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "https://play.google.com/store/apps/details?id=" + context.packageName
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"))
    }


    fun support(context: Context, email: String, sub: String) {
        val mailIntent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:?SUBJECT=$sub&body=&to=$email")
        mailIntent.data = data
        context.startActivity(Intent.createChooser(mailIntent, "Send mail..."))
    }

    fun rateApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.packageName)
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    fun openStore(context: Context, pkg: String?) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                )
            )
        }
    }

    fun showPolicy(context: Context, policyUrl: String) {
        openWeb(context, policyUrl)
    }

    fun openWeb(context: Context, url: String?) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getCurrentCountScan(context: Context): Int {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getInt("count_scan", 0)
    }

    fun increaseCountScan(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var currentCount = getCurrentCountScan(context)
        currentCount += 1
        pref.edit().putInt("count_scan", currentCount).apply()
    }

    fun getCurrentCountLimit(context: Context): Int {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getInt("count_limit", 3)
    }

    fun increaseCountLimit(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var currentCount = getCurrentCountLimit(context)
        currentCount += 1
        pref.edit().putInt("count_limit", currentCount).apply()
    }


    fun isFirstOpenVideo(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time_open_video", true)
    }

    fun setFirstOpenVideo(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time_open_video", false).apply()
    }


    fun isFirstOpenApp(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time", true)
    }

    fun setFirstOpenApp(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time", false).apply()
    }

    fun isFirstOpenMain(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time_main", true)
    }

    fun setFirstOpenMain(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time_main", false).apply()
    }


    fun isFirstOpenCam(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time_cam", true)
//        return true
    }


    fun setFirstOpenCam(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time_cam", false).apply()
    }

    fun isFirstOpenCamRecord(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time_cam_record", true)
//        return true
    }

    fun setFirstOpenCamRecord(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time_cam_record", false).apply()
    }


    fun isFirstOpenStyle6(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getBoolean("first_time_style", true)
//        return true
    }

    fun setFirstOpenStyle6(context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putBoolean("first_time_style", false).apply()
    }


    fun isFirstUpload(context: Context): Boolean {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val isFirstTime: Boolean = pref.getBoolean("first_time_create", true)
        pref.edit().putBoolean("first_time_create", false).apply()
        return isFirstTime
    }

    fun setCamFacing(context: Context, facing: Int) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putInt("facing", facing).apply()
    }

    fun getCamFacing(context: Context): Int {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getInt("facing", 0)
    }

    fun saveGifts(context: Context, key: String, data: String) {
        val pref = context.getSharedPreferences("pref_setting", Context.MODE_PRIVATE)
        pref.edit().putString(key, data).apply()
    }

    fun getGift(context: Context, key: String): String? {
        val pref = context.getSharedPreferences("pref_setting", Context.MODE_PRIVATE)
        return pref.getString(key, "")
    }

//    fun saveUser(context: Context, data: User?) {
//        val pref = context.getSharedPreferences("pref_setting", Context.MODE_PRIVATE)
//        pref.edit().putString("user", Gson().toJson(data)).apply()
//    }
//
//    fun getUser(context: Context): User? {
//        val pref = context.getSharedPreferences("pref_setting", Context.MODE_PRIVATE)
//        val text = pref.getString("user", "")
//        if (text?.isEmpty() == true) return null
//        return Gson().fromJson(text, object : TypeToken<User>() {}.type) as User?
//    }

    fun muteAudio(context: Context) {
        val mAlramMAnager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0
            )
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0)
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0
            )
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
        }
    }

    fun unMuteAudio(context: Context) {
        val mAlramMAnager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0
            )
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0
            )
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0
            )
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0
            )
            mAlramMAnager.adjustStreamVolume(
                AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0
            )
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false)
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false)
        }
    }


    fun requestPermission(activity: Activity?, vararg permission: String) {
        activity?.let {
            ActivityCompat.requestPermissions(it, permission, 1)
        }
    }

    fun permissionGranted(context: Context?, permission: String): Boolean {
        repeat(permission.count()) {
            if (context?.let { it1 ->
                    ActivityCompat.checkSelfPermission(
                        it1, permission
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun gotoSettingDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Permission Denied!")
        builder.setMessage("Go to setting to accept permission!")
        builder.setPositiveButton(
            "Go Setting"
        ) { _, _ ->
            context.let {
                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:" + it.packageName)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                it.startActivity(i)
            }
        }
        val dialog = builder.create()


        dialog.show()
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return md5(
            Settings.Secure.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID
            )
        ).uppercase(Locale.getDefault())
    }

    private fun md5(s: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString: StringBuilder = StringBuilder()
            for (b in messageDigest) {
                val h = StringBuilder(Integer.toHexString(0xFF and b.toInt()))
                while (h.length < 2) h.insert(0, "0")
                hexString.append(h)
            }
            return hexString.toString()
        } catch (ignored: NoSuchAlgorithmException) {
        }
        return ""
    }

    fun getTopScore(context: Context): Int {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return pref.getInt("topScore", 0)
    }

    fun setTopScore(context: Context, score: Int) {
        val pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        pref.edit().putInt("topScore", score).apply()
    }

    fun getScreenSize(context: Context): Pair<Int, Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        return width to height
    }
}