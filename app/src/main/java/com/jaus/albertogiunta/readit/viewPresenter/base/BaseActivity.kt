package com.jaus.albertogiunta.readit.viewPresenter.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.Toast

interface BaseView {

    fun getContext(): Context

    fun showError(error: String)

    fun showError(@StringRes stringResId: Int)

    fun showMessage(@StringRes srtResId: Int)

    fun showMessage(message: String)

}

abstract class BaseActivity<in V : BaseView, P : BasePresenter<V>> : Activity(), BaseView {

    protected abstract var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun getContext(): Context = this@BaseActivity

    override fun showError(error: String) {
//        Snackbar.make(findViewById<View>(android.R.id.content).rootView, error, Snackbar.LENGTH_INDEFINITE).show()
//        snackbar(window.decorView.rootView, error)
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showError(stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(srtResId: Int) {
        Toast.makeText(this, srtResId, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}