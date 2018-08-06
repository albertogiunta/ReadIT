package com.jaus.albertogiunta.readit.viewPresenter.backgroundAddition

import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenter
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseView

object BackgroundAdditionContract {

    interface View : BaseView {

        fun closeActivity()

    }

    interface Presenter : BasePresenter<View> {

        var dao: LinkDao

        fun onActivityResumed()

        fun onLinkAdditionRequest(url: String)
    }

}