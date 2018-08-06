package com.jaus.albertogiunta.readit.viewPresenter.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.longToast

interface BaseView {

    fun getContext(): Context

    fun showErrorSnackbar(error: String)

    fun showErrorSnackbar(@StringRes errorResId: Int)

    fun showMessageSnackbar(message: String)

    fun showMessageSnackbar(@StringRes messageResId: Int)

    fun showErrorToast(error: String)

    fun showErrorToast(@StringRes errorResId: Int)

    fun showMessageToast(message: String)

    fun showMessageToast(@StringRes messageResId: Int)

}

abstract class BaseCompatActivity<in V : BaseView, P : BasePresenter<V>> : AppCompatActivity(), BaseView {

    protected abstract var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun getContext(): Context = this@BaseCompatActivity

    override fun showErrorSnackbar(error: String) {
        snackbar(findViewById(android.R.id.content), error)
    }

    override fun showErrorSnackbar(errorResId: Int) {
        snackbar(findViewById(android.R.id.content), errorResId)
    }

    override fun showMessageSnackbar(message: String) {
        snackbar(findViewById(android.R.id.content), message)
    }

    override fun showMessageSnackbar(messageResId: Int) {
        snackbar(findViewById(android.R.id.content), messageResId)
    }

    override fun showErrorToast(error: String) {
        longToast(error)
    }

    override fun showErrorToast(errorResId: Int) {
        longToast(errorResId)
    }

    override fun showMessageToast(message: String) {
        longToast(message)
    }

    override fun showMessageToast(messageResId: Int) {
        longToast(messageResId)
    }
}


abstract class BaseActivity<in V : BaseView, P : BasePresenter<V>> : Activity(), BaseView {

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

    override fun showErrorSnackbar(error: String) {
        snackbar(findViewById(android.R.id.content), error)
    }

    override fun showErrorSnackbar(errorResId: Int) {
        snackbar(findViewById(android.R.id.content), errorResId)
    }

    override fun showMessageSnackbar(message: String) {
        snackbar(findViewById(android.R.id.content), message)
    }

    override fun showMessageSnackbar(messageResId: Int) {
        snackbar(findViewById(android.R.id.content), messageResId)
    }

    override fun showErrorToast(error: String) {
        longToast(error)
    }

    override fun showErrorToast(errorResId: Int) {
        longToast(errorResId)
    }

    override fun showMessageToast(message: String) {
        longToast(message)
    }

    override fun showMessageToast(messageResId: Int) {
        longToast(messageResId)
    }
}