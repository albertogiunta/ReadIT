package com.jaus.albertogiunta.readit.viewPresenter.base

interface BasePresenter<in V : BaseView> {

    fun attachView(view: V)

    fun detachView()
}

abstract class BasePresenterImpl<V : BaseView> : BasePresenter<V> {

    protected var view: V? = null

    override fun attachView(view: V) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }
}