package com.jaus.albertogiunta.readit.utils

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.jaus.albertogiunta.readit.viewPresenter.linksHome.CARD_LAYOUT

object Settings : KotprefModel() {
    var showSeen by booleanPref(true)
    var cardLayout by enumValuePref(CARD_LAYOUT.CARD1)
}

object Prefs : KotprefModel() {
    var lastInstalledVersion by intPref(0)
}