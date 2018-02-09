package com.jaus.albertogiunta.readit.db

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.jaus.albertogiunta.readit.utils.Utils
import com.jaus.albertogiunta.readit.viewPresenter.links.CardLayout
import org.joda.time.DateTime

object Settings : KotprefModel() {
    var showSeen by booleanPref(true)
    var cardLayout by enumValuePref(CardLayout.CARD1)
    var hideNotificationIfEmpty by booleanPref(true)
}

object Prefs : KotprefModel() {
    var tutorialSeen by booleanPref(false)
    var expiredLinksLastActivationTimestamp by stringPref(DateTime.now().minusDays(1).toString(Utils.dateTimeFormatISO8601))
    var lastInstalledVersion by intPref(0)
}