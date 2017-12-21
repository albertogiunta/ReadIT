package com.jaus.albertogiunta.readit.utils

import java.net.URL

object SystemUtils {

    fun getHostOfURL(url: String): String = URL(url).host

}