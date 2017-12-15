package com.jaus.albertogiunta.readit.viewPresenter.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.design.snackbar

interface BaseView {

    fun getContext(): Context

    fun showError(error: String)

    fun showError(@StringRes errorResId: Int)

    fun showMessage(message: String)

    fun showMessage(@StringRes messageResId: Int)

}

abstract class BaseActivity<in V : BaseView, P : BasePresenter<V>> : AppCompatActivity(), BaseView {

    protected abstract var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun getContext(): Context = this@BaseActivity

    override fun showError(error: String) {
        snackbar(findViewById(android.R.id.content), error)
    }

    override fun showError(errorResId: Int) {
        snackbar(findViewById(android.R.id.content), errorResId)
    }

    override fun showMessage(message: String) {
        snackbar(findViewById(android.R.id.content), message)
    }

    override fun showMessage(messageResId: Int) {
        snackbar(findViewById(android.R.id.content), messageResId)
    }

}