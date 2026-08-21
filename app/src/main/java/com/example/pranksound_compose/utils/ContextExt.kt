package com.example.pranksound_compose.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by CuongNK on 2/20/2023.
 */
object ContextExt {
    fun Context.showToast(message: CharSequence, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch(Dispatchers.Main)
        {
            Toast.makeText(this@showToast, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun Context.findActivity(): ComponentActivity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is ComponentActivity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    fun <T : Activity> Context.startActivity(
        des: Class<T>,
        intentBuilder: (Intent.() -> Unit)? = null
    ) {
        val intent = Intent(this, des)
        intentBuilder?.invoke(intent)
        startActivity(intent)
    }

    fun <T : Activity> Activity.startActivityForResult(
        requestCode: Int,
        des: Class<T>,
        intentBuilder: (Intent.() -> Unit)?
    ) {
        val intent = Intent(this, des)
        intentBuilder?.invoke(intent)
        startActivityForResult(intent, requestCode)
    }

    fun <T : Activity> Fragment.startActivity(
        des: Class<T>,
        intentBuilder: (Intent.() -> Unit)? = null
    ) {
        requireContext().startActivity(des, intentBuilder)
    }

}

@SuppressLint("QueryPermissionsNeeded")
inline fun Context.shareText(
    text: String, subject: String = "", onCantHandleAction: () -> Unit = {}
) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, text)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, null))
    } else {
        onCantHandleAction()
    }
}

fun Context.shareToApp(packageName: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool sound app!")

    shareIntent.setPackage(packageName)

    try {
        startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show()
    }
}

fun Context.shareToOtherApps() {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool sound app!")

    startActivity(Intent.createChooser(shareIntent, "Share via"))
}