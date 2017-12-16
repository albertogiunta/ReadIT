package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenter
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseView

object LinksContract {

    interface View : BaseView {
        fun startLoadingState()

        fun stopLoadingState()

        fun updateLinkListUI()
    }

    interface Presenter : BasePresenter<View> {

        var linkList: MutableList<Link>

        fun onLinkAdditionRequest(url: String)

        fun onLinkOpeningRequest()

    }

}