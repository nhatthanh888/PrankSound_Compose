package com.example.plant.utils.minhtn

import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import kotlin.reflect.KProperty

class HawkDelegate<T>(
    private val key: String,
    private val defaultValue: T,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return Hawk.get(key, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Hawk.put(key, value)
    }
}

class HawkObjectDelegate<T : Any>(
    private val key: String,
    private val clazz: Class<T>,
    private val defaultValue: T
) {
    private val gson = Gson()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val json = Hawk.get(key, "")
        return if (json.isNullOrBlank()) defaultValue else gson.fromJson(json, clazz)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Hawk.put(key, gson.toJson(value))
    }
}

