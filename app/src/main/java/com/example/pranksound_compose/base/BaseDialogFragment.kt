package com.example.pranksoundalpha.base

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.example.plant.utils.minhtn.SharePreferenceExt
import com.example.plant.utils.minhtn.SharePreferenceExt.LANGUAGE_CODE
import com.orhanobut.hawk.Hawk
import java.util.Locale
import kotlin.math.roundToInt

abstract class BaseDialogFragment<VB : ViewBinding>(
    private val widthRatio: Float = 0.9f,
    private val isCanceledOnTouchOutside: Boolean = false,
    private val gravity: Int = Gravity.CENTER
) : DialogFragment() {

    lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getDataBinding()
        updateLanguage()
        return binding.root

    }
    private fun updateLanguage() {
        val country = SharePreferenceExt.currentLanguageCode
        val locale = Locale(
            Hawk.get(LANGUAGE_CODE, "en"), country
        )
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mDialog = dialog
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
            if (mDialog.window != null) {
                mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mDialog.window!!.setLayout(
                    (requireActivity().resources.displayMetrics.widthPixels * widthRatio).roundToInt(),
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                val layoutParams = mDialog.window!!.attributes
                layoutParams.gravity = gravity
                mDialog.window!!.attributes = layoutParams
            }
        }


        initView()
        addEvent()
        addObservers()
        initData()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded) {
            return
        }
        try {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        if (isAdded) {
            try {
                super.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    abstract fun getDataBinding(): VB

    open fun initView() {}

    open fun addEvent() {}

    open fun addObservers() {}

    open fun initData() {}
}