package com.example.plant.utils.minhtn

import android.content.Context
import android.provider.Settings
import com.example.pranksound.data.dto.config.PushUpdate
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

object SharePreferenceExt {
    const val OPEN_APP_COUNT = "OPEN_APP_COUNT"
    const val CMP = "CMP"

    const val FIRST_SHOW_CMP = "FIRST_SHOW_CMP"
    const val PUSH_UPDATE = "PUSH_UPDATE"
    const val SHOW_IAP_AFTER_SPLASH = "SHOW_IAP_AFTER_SPLASH"
    const val IS_PASS_ONBOARD = "IS_PASS_ONBOARD"
    const val FIRST_CHOOSE_LG = "FIRST_CHOOSE_LG"
    const val LANGUAGE_CODE = "LANGUAGE_CODE"
    const val IS_LOOP = "IS_LOOP"
    const val IS_BG = "IS_BG"

    const val TIMEOUT_RELOAD_BANNER = "TIMEOUT_RELOAD_BANNER"

    var openAppCount by HawkDelegate(OPEN_APP_COUNT, 0)

    var firstShowCMP by HawkDelegate(FIRST_SHOW_CMP, true)

    var showIAPAfterSplash by HawkDelegate(SHOW_IAP_AFTER_SPLASH, false)

    var pushUpdate by HawkObjectDelegate(PUSH_UPDATE, PushUpdate::class.java, PushUpdate())

    var firstChooseLG by HawkDelegate(FIRST_CHOOSE_LG, true)

    var currentLanguageCode by HawkDelegate(LANGUAGE_CODE, "")

    var isPassOnboard by HawkDelegate(IS_PASS_ONBOARD, false)

    var isLoopSave by HawkDelegate(IS_LOOP, false)
    var isBG by HawkDelegate(IS_BG, false)

    var timeoutReloadBanner by HawkDelegate(TIMEOUT_RELOAD_BANNER, 3000L)

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

}
