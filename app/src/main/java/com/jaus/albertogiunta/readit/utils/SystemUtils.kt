package com.jaus.albertogiunta.readit.utils

import java.net.URL

object SystemUtils {

    fun getHostOfURL(url: String): String = URL(url).host

    fun ifAtLeast(version: Int, f: () -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= version) f()
    }

    fun atLeast(version: Int): Boolean = android.os.Build.VERSION.SDK_INT >= version

}