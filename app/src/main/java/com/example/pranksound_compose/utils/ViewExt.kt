package com.example.pranksound_compose.utils

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.PrecomputedTextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import org.w3c.dom.Text



fun TextView.removeGradient() {
    this.paint.shader = null
}
fun TextView.setVerticalGradientText(
    startColor: String = "#B2FEFA",
    endColor: String ="#0ED2F7"
) {
    this.post {
        val shader = LinearGradient(
            0f, 0f, 0f, this.height.toFloat(),
            intArrayOf(
                startColor.toColorInt(),
                endColor.toColorInt()
            ),
            null,
            Shader.TileMode.CLAMP
        )
        this.paint.shader = shader
        this.invalidate()
    }
}

fun View.showKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toInvisible() {
    this.visibility = View.GONE
}


/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */


/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */



fun View.gone(hasAnim: Boolean = false) {
    if (!hasAnim) {
        this.visibility = View.GONE
    } else {
        animateScale(1f, 0f, 1f, 0f, 250) {
            this.visibility = View.GONE
        }
    }
}

fun View.gone(hasAnim: Boolean = false, onEnd: () -> Unit = {}) {
    if (!hasAnim) {
        this.visibility = View.GONE
        onEnd.invoke()
    } else {
        animateScale(1f, 0f, 1f, 0f, 250) {
            this.visibility = View.GONE
            onEnd.invoke()
        }
    }
}

fun View.toggled(hasAnim: Boolean = false) {
    if (isVisible) gone(hasAnim) else visible(hasAnim)
}

fun View.visible(hasAnim: Boolean = false, keepWithIsGone: Boolean = false) {
    if (keepWithIsGone && isVisible) return
    visible(hasAnim)
}

fun View.visible(hasAnim: Boolean = false) {
    if (!hasAnim) {
        this.visibility = View.VISIBLE
        if (scaleX == 0f || scaleY == 0f || alpha == 0f) {
            setScale(1f)
        }
    } else {
        setScale(0f)
        this.visibility = View.VISIBLE
        animateScale(0f, 1f, 0f, 1f, 250)
    }
}


fun View.animateScale(
    fromScale: Float,
    toScale: Float,
    fromAlpha: Float,
    toAlpha: Float,
    duration: Long = 100,
    onEnd: () -> Unit = {}
) {
    clearAnimation()
    animate().scaleXBy(fromScale).scaleYBy(fromScale).scaleX(toScale).scaleY(toScale)
        .alphaBy(fromAlpha).alpha(toAlpha).withEndAction(onEnd).setDuration(duration).start()
}


fun View.setScale(value: Float) {
    scaleX = value
    scaleY = value
    alpha = value
}





/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}




inline fun <T : ViewBinding> T.cached(crossinline block: T.() -> Unit): T {
    this.apply {
        block()
    }
    return this
}

fun AppCompatTextView.setTextFutureExt(text: String) =
        setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                        text,
                        TextViewCompat.getTextMetricsParams(this),
                        null
                )
        )

fun AppCompatEditText.setTextFutureExt(text: String) =
        setText(
                PrecomputedTextCompat.create(text, TextViewCompat.getTextMetricsParams(this))
        )
fun AppCompatActivity.
        replaceFragment(
    @IdRes containerId: Int,
    fragment: Fragment,
    isAddToBackStack: Boolean = true,
) {
    try {
        val fragmentTransaction = supportFragmentManager.beginTransaction()



        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(fragment::class.java.name)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.replace(containerId, fragment, fragment::class.java.name)
        fragmentTransaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.replaceFragment(
    @IdRes containerId: Int, fragment: Fragment,
    isAddToBackStack: Boolean = true,
    addFromActivity: Boolean = false
) {
    try {
        val fm = if (addFromActivity) (activity as AppCompatActivity).supportFragmentManager else childFragmentManager
        val fragmentTransaction = fm.beginTransaction()


        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(fragment::class.java.name)


        fragmentTransaction.replace(containerId, fragment, fragment::class.java.name)
        fragmentTransaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.replaceFragment(
    activity: Activity,
    @IdRes containerId: Int, fragment: Fragment,
    isAddToBackStack: Boolean = true,
    addFromActivity: Boolean = false
) {
    try {
        val fm = if (addFromActivity) (activity as AppCompatActivity).supportFragmentManager else childFragmentManager
        val fragmentTransaction = fm.beginTransaction()


        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(fragment::class.java.name)


        fragmentTransaction.replace(containerId, fragment, fragment::class.java.name)
        fragmentTransaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun Uri?.openInBrowser(context: Context) {
    this ?: return // Do nothing if uri is null

    val browserIntent = Intent(Intent.ACTION_VIEW, this)
    ContextCompat.startActivity(context, browserIntent, null)
}