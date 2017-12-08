package com.jaus.albertogiunta.readit.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * VIEWS
 */

fun View.visible() {
    visibility = View.VISIBLE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}