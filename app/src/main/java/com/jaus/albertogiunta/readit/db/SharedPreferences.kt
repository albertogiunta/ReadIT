package com.jaus.albertogiunta.readit.db

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.jaus.albertogiunta.readit.viewPresenter.linksHome.CardLayout

object Settings : KotprefModel() {
    var showSeen by booleanPref(true)
    var cardLayout by enumValuePref(CardLayout.CARD1)
}

object Prefs : KotprefModel() {
    var tutorialSeen by booleanPref(false)
    var lastInstalledVersion by intPref(0)
}