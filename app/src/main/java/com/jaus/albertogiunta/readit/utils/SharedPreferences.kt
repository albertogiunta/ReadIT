package com.jaus.albertogiunta.readit.utils

import com.chibatching.kotpref.KotprefModel

object Settings : KotprefModel() {
    var showSeen by booleanPref(true)
    var cardLayout by intPref(0)
}

object Prefs : KotprefModel() {
    var lastInstalledVersion by intPref(0)
}